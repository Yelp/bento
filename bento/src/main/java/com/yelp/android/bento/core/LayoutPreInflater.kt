package com.yelp.android.bento.core

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

const val LOG_TAG = "pre-inflater"

class LayoutPreInflater(private val asyncLayoutInflater: AsyncLayoutInflater) {

    private val viewMap = SparseArray<MutableList<View>>()
    private val waitingForInflationMap = ConcurrentHashMap<Component, () -> Unit>()

    private val callbackList = ConcurrentLinkedQueue<() -> Unit>()

    fun inflateAll(component: Component, callback: () -> Unit) {
        val views = inflateForComponent(component)
        if (views.isEmpty()) {
            // no async inflation to do, only proceed with callback
            // if not waiting for inflation to finish.
            // need to queue the work up
            if (waitingForInflationMap.isEmpty()) {
                Log.e("paul", "empty : $component)")
                callback()
                return
            } else {
                Log.e("paul", "queueing callback for : $component")
                callbackList.add(callback)
                return
                // add work to queue
                // return
            }
        }

        waitingForInflationMap[component] = callback
        Flowable.fromIterable(views)
                .flatMap { task -> task.subscribeOn(Schedulers.io()) }
                .toList()
                .map { _ -> true }
                .doAfterSuccess {
                    waitingForInflationMap.remove(component)
                    if (waitingForInflationMap.isEmpty()) {
                        Log.e("paul", "waitingForInflationMap empty chewing queue")

                        while (!callbackList.isEmpty()) {
                            callbackList.poll()()
                        }
                        callback()
                    } else {
                        Log.e("paul", "queueing callback for : $component")
                        callbackList.add(callback)
                    }
//                    callback()
                }
                .subscribe()
    }

    private fun inflateForComponent(component: Component): MutableList<Flowable<View>> {
        val views = mutableListOf<Flowable<View>>()
        for (i in 0 until component.count) {
            val component1 = component.getHolderType(i)
            // TODO see if we can call this whole thing on a background thread to get the
            // reflective call in constructViewHolder off the main thread too.
            val viewHolder: ComponentViewHolder<*, *> = constructViewHolder(component1)
            if (viewHolder is AsyncCompat) {
                val resId = (viewHolder as AsyncCompat).layoutId
                val viewFlowable = createCompletableForViewConfig(resId)
                views.add(viewFlowable)
            }
        }
        return views
    }

    private fun createCompletableForViewConfig(layoutResId: Int): Flowable<View> {
        return Flowable.create({ emitter: FlowableEmitter<View> ->
            asyncLayoutInflater.inflate(layoutResId, null
            ) { view: View, resid: Int, parent: ViewGroup? ->
                addView(view, layoutResId)
                emitter.onNext(view)
                emitter.onComplete()
            }
        }, BUFFER)
    }

    private fun addView(view: View, layoutResId: Int) {
        var views = viewMap[layoutResId]
        if (views == null) {
            views = ArrayList()
        }
        views.add(view)
        viewMap.put(layoutResId, views)
    }

    // The original LayoutPreInflater is synchronized. // TODO does it need to be?
    @Synchronized
    fun getView(layoutResId: Int): View? {
        val views = viewMap[layoutResId]
        val view: View
        if (views == null || views.isEmpty()) {
            // No pre-inflated views were found.
            Log.e(LOG_TAG, "No preinflated view found for: $layoutResId")
            return null
        }
        view = views.removeAt(0)
        viewMap.put(layoutResId, views)
        // We want to make sure we always return a view without a parent in case someone is
        // using this to create RecyclerView viewHolders.
        return if (view.parent != null) {
            getView(layoutResId)
        } else view
    }

    private fun constructViewHolder(
            viewHolderType: Class<out ComponentViewHolder<*, *>>): ComponentViewHolder<*, *> {
        return try {
            viewHolderType.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Failed to instantiate view holder", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed to instantiate view holder", e)
        }
    }
}