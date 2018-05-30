package com.yelp.android.bento.core

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

    abstract fun inflate(parent: ViewGroup): View

    /**
     * Using FindViewById is a heavy and non-performant method and should never be called in the
     * bind method. You should have fields for any views you wish to modify during the bind method
     * and instantiate them during inflation.
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
