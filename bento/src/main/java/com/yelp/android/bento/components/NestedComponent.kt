package com.yelp.android.bento.components

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.utils.inflate

/**
 * A [Component] that supports nesting an inner Component inside an outer ViewHolder.
 * This can be used to add borders to components.
 *
 * To use it, create a ViewHolder class extending [NestedOuterComponentViewHolder] and
 * implement outerLayout and recyclerViewId to have it inflate the desired outer layout. Use
 * this new ViewHolder class to create the viewModel.
 */
open class NestedComponent<P>(
        private val presenter: P,
        private val viewModel: NestedViewModel<*, *>
) : Component() {

    override fun getPresenter(position: Int) = presenter

    override fun getItem(position: Int) = viewModel

    override fun getCount() = 1

    override fun getHolderType(position: Int) = viewModel.outerComponentViewHolder
}

abstract class NestedOuterComponentViewHolder<P, T> :
        ComponentViewHolder<P, NestedViewModel<P, T>>() {

    lateinit var controller: RecyclerViewComponentController

    /**
     * Layout id of the outer viewHolder's layout. The layout should contain a recyclerView
     * corresponding to recyclerViewId, which is what the inner component will be added to.
     */
    @get:LayoutRes
    abstract val outerLayout: Int

    /**
     * Id of recyclerView widget inside the outer layout. The recyclerView will be used to create
     * the component controller.
     */
    @get:IdRes
    abstract val recyclerViewId: Int

    @CallSuper
    override fun inflate(parent: ViewGroup): View {
        return parent.inflate<View>(outerLayout).also {
            controller = RecyclerViewComponentController(
                    it.findViewById(recyclerViewId),
                    RecyclerView.VERTICAL
            )
        }
    }

    @CallSuper
    override fun bind(presenter: P, element: NestedViewModel<P, T>) {
        if (controller.size > 0) {
            if (controller[0] != element.innerComponent) {
                controller.remove(controller[0])
                controller.addComponent(element.innerComponent)
            }
        } else {
            controller.addComponent(element.innerComponent)
        }
    }
}

data class NestedViewModel<P, T>(
        val innerComponent: Component,
        val outerComponentViewHolder: Class<out NestedOuterComponentViewHolder<P, T>>,
        val outerComponentViewModel: T
)