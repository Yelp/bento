package com.yelp.android.bento.core

import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController.constructViewHolder
import com.yelp.android.bento.core.AsyncInflationStrategy.BEST_GUESS
import com.yelp.android.bento.core.AsyncInflationStrategy.DEFAULT
import com.yelp.android.bento.core.AsyncInflationStrategy.SMART
import com.yelp.android.bento.utils.BentoSettings
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val DEFAULT_NUM_ABOVE_FOLD_VIEW_HOLDERS = 5
private const val MAX_VIEWS = 40
private const val VIEWS_PER_COMPONENT_THRESHOLD = 10

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

    // Keeps track of whether or not we inflated the below the fold set of views yet.
    private var belowTheFoldTriggered = false

    // Keeps track of whether or not we inflated the above the fold set of views yet.
    private var aboveTheFoldTriggered = false

    // Tracks how many views have been inflated.
    private val inflatedViewTracker = AtomicInteger(0)

    var asyncCacheKey: String = recyclerView.id.toString()
        set(value) {
            smartAsyncCacheEmptyOnStart = !SmartAsyncInflationCache.containsKey(value)
            field = value
        }

    var strategy = DEFAULT
    var numberOfAboveTheFoldViewHolders = DEFAULT_NUM_ABOVE_FOLD_VIEW_HOLDERS

    // Tracks if the async cache was empty for this RecyclerView before the RecyclerView was used.
    // If it was empty, it means this is the first time the cache is being populated. We have to
    // set this during this object's creation because there would be a cache hit as soon the first
    // component is added to this [recyclerView].
    private var smartAsyncCacheEmptyOnStart = !SmartAsyncInflationCache.containsKey(asyncCacheKey)

    init {
        if (recyclerView.isAttachedToWindow) {
            findAndAttachToLifecycleOwner()
        } else {
            recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    findAndAttachToLifecycleOwner()
                    recyclerView.removeOnAttachStateChangeListener(this)
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            })
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
        inflationJobs.remove(component)?.cancel()
    }

    fun cancelAllInflationJobs() {
        clearResources()
    }

    /**
     * Intercepts addComponent() calls from a RecyclerViewComponentController and pre-inflates
     * views asynchronously based on the set [AsyncInflationStrategy].
     */
    fun asyncInflateViewsForComponent(component: Component, addComponentCallback: () -> Unit) {
        if (strategy == DEFAULT) {
            if (smartAsyncCacheEmptyOnStart) {
                asyncInflateViewsBestGuess(component, addComponentCallback)
            } else {
                smartAsyncInflateViewsForComponent(addComponentCallback)
            }
        } else if (strategy == SMART) {
            smartAsyncInflateViewsForComponent(addComponentCallback)
        } else if (strategy == BEST_GUESS) {
            asyncInflateViewsBestGuess(component, addComponentCallback)
        }
    }

    /**
     * The "SMART" strategy for async inflation where adding components blocks on inflating above
     * the fold view holders before inflating below the fold view holders.
     */
    private fun smartAsyncInflateViewsForComponent(addComponentCallback: () -> Unit) {
        launch(defaultBridgeDispatcher) {
            // The lock will block all addComponent calls that occur until the above the fold
            // view holders are inflated.
            lock.withLock {
                // Only inflate above the fold if the cache wasn't empty in create, the below the
                // fold set hasn't been inflated yet and if there's a match for this page name in
                // the cache.
                if (!belowTheFoldTriggered && !smartAsyncCacheEmptyOnStart &&
                        SmartAsyncInflationCache.containsKey(asyncCacheKey)) {
                    if (!aboveTheFoldTriggered) {
                        val start = System.currentTimeMillis()
                        asyncInflateViewsIfPossible(true)
                        if (BentoSettings.loggingEnabled) {
                            val timeTaken = System.currentTimeMillis() - start
                            Log.i(BentoSettings.BENTO_TAG, "Above fold inflation time taken : $timeTaken ms")
                        }
                        aboveTheFoldTriggered = true
                    }
                }
                withContext(Dispatchers.Main) {
                    executeAddComponentCallback(addComponentCallback)
                }
            }
            // Outside the lock. The second time it's called, it should inflate the below the fold
            // view holders without blocking any more addComponent calls.
            if (!belowTheFoldTriggered && !smartAsyncCacheEmptyOnStart &&
                    SmartAsyncInflationCache.containsKey(asyncCacheKey)) {
                belowTheFoldTriggered = true
                asyncInflateViewsIfPossible(false)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun asyncInflateViewsIfPossible(isAboveTheFoldCall: Boolean) {
        if (asyncCacheKey.isEmpty()) return // There's no work to do.

        val listToPrepare = SmartAsyncInflationCache[asyncCacheKey] ?: mutableListOf()
        if (listToPrepare.isEmpty()) return // There's no work to do.

        val aboveFoldKeys = listToPrepare.take(numberOfAboveTheFoldViewHolders)
        val numberBelowFoldViewHolders = listToPrepare.size - numberOfAboveTheFoldViewHolders

        if (numberBelowFoldViewHolders < 0) {
            return // There's no work to do.
        }
        val viewHoldersToInflate = if (isAboveTheFoldCall) aboveFoldKeys else listToPrepare.takeLast(numberBelowFoldViewHolders)

        coroutineScope {
            val inflations = viewHoldersToInflate.map { viewHolderType ->
                async {
                    val viewHolder = constructViewHolder(viewHolderType as Class<out ComponentViewHolder<Any?, Any?>>?)
                    addViewHolder(viewHolder, viewHolderType as Class<out ComponentViewHolder<*, *>>)
                    val (_, view) = BentoAsyncLayoutInflater.inflate(viewHolder, recyclerView, asyncInflaterDispatcher)
                    viewMap[viewHolder] = view
                }
            }
            inflations.awaitAll()
        }
    }

    /**
     * The "BEST_GUESS" strategy for async view inflation. Inflates views for [component] if
     * its count is non-zero and if doing so doesn't inflate more
     * than [MAX_VIEWS].
     */
    private fun asyncInflateViewsBestGuess(component: Component, addComponentCallback: () -> Unit) {
        inflationJobs[component] = launch(defaultBridgeDispatcher) {
            var numberOfViewsToInflate = component.count
            // If there's more than 10, chances are they'll be recycled and more will go unused.
            // Not gonna lie. It's a guess and could be improved.
            numberOfViewsToInflate.coerceAtMost(VIEWS_PER_COMPONENT_THRESHOLD)

            // If this amount would tip us over the MAX views value, only inflate enough
            // to fill up to the max.
            numberOfViewsToInflate = numberOfViewsToInflate
                    .coerceAtMost(MAX_VIEWS - inflatedViewTracker.get())

            // Skip the below coroutine if there are either 0 views to inflate.
            if (numberOfViewsToInflate == 0) {
                lock.withLock {
                    // We still need the lock to make sure components are added in order. Otherwise,
                    // these ones would add above previous components that are being inflated below.
                }
                withContext(Dispatchers.Main) {
                    executeAddComponentCallback(addComponentCallback)
                }
            } else {
                inflatedViewTracker.getAndAdd(numberOfViewsToInflate)
                val inflations = (0 until numberOfViewsToInflate).map { i ->
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
        belowTheFoldTriggered = false // Ideally we wouldn't have to reset these at all but, we do.
        aboveTheFoldTriggered = false
        coroutineContext.cancelChildren()
        viewHolderMap.clear()
        viewMap.clear()
        inflationJobs.clear()
    }
}
