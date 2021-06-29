package com.yelp.android.bento.core

import android.view.View
import android.view.ViewGroup
import io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

const val LOG_TAG = "pre-inflater"

/**
 * This acts as a bridge between RecyclerViewComponentController and the underlying
 * RecyclerView.Adapter. It intercepts the addComponent() calls and inflates views with
 * AsyncLayoutInflater. Components are added to the RecyclerView when the inflation
 * is finished.
 */
class LayoutPreInflater(
    private val asyncLayoutInflater: AsyncLayoutInflater,
    val viewGroup: ViewGroup
) {

    object ViewHolderInstanceCache {
        @JvmStatic
        val viewHolderMap =
            mutableMapOf<Class<out ComponentViewHolder<*, *>>, ComponentViewHolder<*, *>>()
    }

    private val sequentialScheduler: Scheduler = Schedulers.single()
    private val viewMap =
        mutableMapOf<Class<out ComponentViewHolder<*, *>>, MutableList<Pair<ComponentViewHolder<*, *>, View>>>()
    private val inflationInProgressSet = mutableSetOf<Component>()
    private val callbackList = LinkedList<() -> Unit>()

    fun asyncInflateViewsForComponent(component: Component, callback: () -> Unit) {
        val views = createAsyncInflationFlowablesForComponent(component)
        if (views.isEmpty()) {
            callbackList.add(callback)
            // Short-circuit
            return
        } else {
            callbackList.add(callback)
        }

        inflationInProgressSet.add(component)
        Flowable.fromIterable(views)
            .flatMap { task -> task.subscribeOn(sequentialScheduler) }
            .toList()
            .map { true }
            .subscribe({
                inflationInProgressSet.remove(component)
                if (inflationInProgressSet.isEmpty()) {
                    while (!callbackList.isEmpty()) {
                        callbackList.poll()()
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    /**
     * Retrieves a previously async inflated view if there is one.
     */
    fun getView(viewHolderType: Class<out ComponentViewHolder<*, *>>): Pair<ComponentViewHolder<*, *>, View>? {

        val views = viewMap[viewHolderType]
        if (views == null || views.isEmpty()) {
            // No pre-inflated views were found. This log tag is useul
            return null
        }
        val pair = views.removeAt(views.lastIndex)
        // We want to make sure we always return a view without a parent in case someone is
        // using this to create RecyclerView viewHolders.
        return if (pair.second.parent != null) {
            getView(viewHolderType)
        } else pair
    }

    private fun createAsyncInflationFlowablesForComponent(component: Component): MutableList<Flowable<View>> {
        val views = mutableListOf<Flowable<View>>()
        for (i in 0 until component.count) {
            val viewHolderType = component.getHolderType(i)
            views.add(createCompletableForViewConfig(viewHolderType))
        }
        return views
    }

    /**
     * This makes a Flowable<View> out of AsyncLayoutInflater's asynchronous callback. As a bonus,
     * it creates a view holder instance for each element in the Component on the background thread
     * too and caches the instance for use in RecyclerViewComponentController.
     */
    private fun createCompletableForViewConfig(
        viewHolderType: Class<out ComponentViewHolder<*, *>>
    ): Flowable<View> {
        return Flowable.create({ emitter: FlowableEmitter<View> ->
            val viewHolder: ComponentViewHolder<*, *> = constructViewHolder(viewHolderType)
            ViewHolderInstanceCache.viewHolderMap[viewHolderType] = viewHolder
//            if (viewHolder is AsyncCompat) {
            asyncLayoutInflater.inflate(viewHolder, viewGroup) { viewHolder, view ->
                addView(viewHolder, view, viewHolderType)
                emitter.onNext(view)
                emitter.onComplete()
            }
//            } else {
//                emitter.onComplete()
//            }
        }, BUFFER)
    }

    private fun addView(
        viewHolder: ComponentViewHolder<*, *>,
        view: View,
        viewHolderType: Class<out ComponentViewHolder<*, *>>
    ) {
        val views = viewMap.getOrPut(viewHolderType, { mutableListOf() })
        views.add(Pair(viewHolder, view))
    }

    private fun constructViewHolder(
        viewHolderType: Class<out ComponentViewHolder<*, *>>
    ): ComponentViewHolder<*, *> {
        return try {
            viewHolderType.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Failed to instantiate view holder", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed to instantiate view holder", e)
        }
    }
}
