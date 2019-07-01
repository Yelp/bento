package com.yelp.android.bento.core

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Represents a view holder to be used with the [Component]. This class is
 * responsible for inflating the associated view (when necessary) and populating the views with
 * data. The data will be provided by the adapter and will be of type T.
 *
 * ** NOTE: Subclasses must provide a no-arg constructor and be visible from this package **
 *
 * This class will be instantiated by the [ComponentController] when needed, by calling the
 * no-arg constructor. Unfortunately, this means all subclasses must be visible from this package
 * and provide a no-arg constructor.
 */
abstract class ComponentViewHolder<P, T> {

    /**
     * Contains the absolute position within the entire [ComponentController]. This should only be
     * set by the [ComponentController].
     */
    var absolutePosition: Int = -1

    /**
     * Called to inflate the layout needed to render the view. This is a good place to use
     * findViewById to get references to the different points in your view that you want to bind
     * data and click listeners to.
     */
    abstract fun inflate(parent: ViewGroup): View

    /**
     * Called to bind the component item's presenter and data item to the view. This is a
     * performance sensitive operation since it's called every time a view is recycled back
     * into the view port of the device. Try to front-load work into the building of the data item
     * or in [ComponentViewHolder.inflate].
     *
     * NOTE: Using FindViewById is a heavy and non-performant method and should never be called in
     * the bind method. You should have fields for any views you wish to modify during the bind
     * method and instantiate them during inflation.
     */
    abstract fun bind(presenter: P, element: T)

    /**
     * Called when a view has been attached to a window.
     * See [android.support.v7.widget.RecyclerView.Adapter.onViewAttachedToWindow]
     */
    open fun onViewAttachedToWindow() {}

    /**
     * Called when a view has been detached from its window.
     * See [android.support.v7.widget.RecyclerView.Adapter.onViewDetachedFromWindow]
     */
    open fun onViewDetachedFromWindow() {}

    /**
     * Called when a view has been recycled.
     * See [android.support.v7.widget.RecyclerView.Adapter.onViewRecycled]
     */
    open fun onViewRecycled() {}
}

fun View.setOnDragStartListener(callback: () -> Unit) {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            callback.invoke()
            return@setOnTouchListener true
        }
        false
    }
}