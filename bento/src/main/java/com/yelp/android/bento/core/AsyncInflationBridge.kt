package com.yelp.android.bento.core

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController.constructViewHolder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * This acts as a bridge between RecyclerViewComponentController and the underlying
 * RecyclerView.Adapter. It intercepts the addComponent() calls and inflates views with
 * [BentoAsyncLayoutInflater]. Components are added to the RecyclerView when the inflation
 * is finished.
 */
internal class AsyncInflationBridge @JvmOverloads constructor(
    val recyclerView: RecyclerView,
    private val asyncInflaterDispatcher: CoroutineDispatcher = BentoAsyncLayoutInflater.dispatcher,
    private val defaultBridgeDispatcher: CoroutineDispatcher = dispatcher
) : CoroutineScope {

    internal companion object {
        private val lock = Mutex()

        private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job

    private val viewHolderMap = ConcurrentHashMap<Class<out ComponentViewHolder<*, *>>,
            ConcurrentLinkedDeque<ComponentViewHolder<*, *>>>()
    private val viewMap = ConcurrentHashMap<ComponentViewHolder<*, *>, View?>()
    private val inflationJobs = ConcurrentHashMap<Component, Job>()

    init {
        if (recyclerView.isAttachedToWindow) {
            findAndAttachToLifecycleOwner()
        } else {
            recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {
                    findAndAttachToLifecycleOwner()
                    recyclerView.removeOnAttachStateChangeListener(this)
                }
                override fun onViewDetachedFromWindow(v: View?) = Unit
            })
        }
    }

    /**
     * Inflates the views and creates the view holder for [component] on a background thread,
     * getting them nice and ready for RecyclerViewComponentController to use them.
     */
    fun asyncInflateViewsForComponent(component: Component, addComponentCallback: () -> Unit) {
        inflationJobs[component] = launch(defaultBridgeDispatcher) {
            val inflations = (0 until component.count).map { i ->
                async {
                    val viewHolderType = component.getHolderType(i)
                    val viewHolder: ComponentViewHolder<*, *> = constructViewHolder(viewHolderType)
                    addViewHolder(viewHolder, viewHolderType)
                    val (_, view) = BentoAsyncLayoutInflater.inflate(viewHolder, recyclerView, asyncInflaterDispatcher)
                    viewMap[viewHolder] = view
                }
            }
            lock.withLock {
                inflations.awaitAll()
            }
            withContext(Dispatchers.Main) {
                executeAddComponentCallback(addComponentCallback)
            }
        }
    }

    /**
     * Retrieves a previously async inflated view if there is one.
     */
    fun getView(viewHolder: ComponentViewHolder<*, *>) = viewMap.remove(viewHolder)

    /**
     * Retrieves a previously created view holder of the passed in [viewHolderType].
     */
    fun getViewHolder(
        viewHolderType: Any? // Class<out ComponentViewHolder<*, *>> doesn't work with Java.
    ): ComponentViewHolder<*, *>? {
        if (viewHolderMap.containsKey(viewHolderType)) {
            viewHolderMap[viewHolderType]?.let { list ->
                if (list.isNotEmpty()) {
                    return list.poll()
                }
            }
        }
        return null
    }

    /**
     * Cancels [component]'s inflation job if the component was removed.
     */
    fun trackComponentRemoval(component: Component) {
        inflationJobs[component]?.cancel()
    }

    fun cancelAllInflationJobs() {
        clearResources()
    }

    private fun addViewHolder(
        viewHolder: ComponentViewHolder<*, *>,
        viewHolderType: Class<out ComponentViewHolder<*, *>>
    ) {
        val viewHolders = viewHolderMap.getOrPut(viewHolderType) {
            ConcurrentLinkedDeque()
        }
        viewHolders.add(viewHolder)
        viewHolderMap[viewHolderType] = viewHolders
    }

    /**
     * Executes RecyclerViewComponentController's addComponent() callback on the main thread.
     */
    private fun executeAddComponentCallback(
        addComponentCallback: () -> Unit
    ) {
        try {
            addComponentCallback()
        } catch (exception: IllegalArgumentException) {
            exception.message?.let { message ->
                if (!message.contains("already added")) {
                    throw exception
                }
            }
        }
    }

    private fun findAndAttachToLifecycleOwner() {
        var owner = recyclerView.findViewTreeLifecycleOwner()
        if (owner == null) {
            if (recyclerView.context is LifecycleOwner) {
                owner = recyclerView.context as LifecycleOwner
            }
        }
        owner?.lifecycleScope?.launch(Dispatchers.Main.immediate) {
            owner.lifecycle.addObserver(object : LifecycleObserver {

                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun cleanUp() {
                    clearResources()
                    owner.lifecycle.removeObserver(this)
                }
            })
        }
    }

    private fun clearResources() {
        coroutineContext.cancelChildren()
        viewHolderMap.clear()
        viewMap.clear()
        inflationJobs.clear()
    }
}
