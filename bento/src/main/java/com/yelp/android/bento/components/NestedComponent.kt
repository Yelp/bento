package com.yelp.android.bento.components

import android.view.View
import android.view.ViewGroup
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
open class NestedComponent(private val viewModel: NestedViewModel<*>) : Component() {

    override fun getPresenter(position: Int) = viewModel.innerComponent.getPresenter(position)

    override fun getItem(position: Int) = viewModel

    override fun getCount() = 1

    override fun getHolderType(position: Int) = viewModel.outerComponentViewHolder
}

abstract class NestedOuterComponentViewHolder<T> : ComponentViewHolder<Any?, NestedViewModel<T>>() {

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

    override fun inflate(parent: ViewGroup): View {
        return parent.inflate<View>(outerLayout).apply {
            val recyclerView: RecyclerView = findViewById(recyclerViewId)
            controller = RecyclerViewComponentController(recyclerView, RecyclerView.VERTICAL)
        }
    }

    override fun bind(presenter: Any?, element: NestedViewModel<T>) {
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

data class NestedViewModel<T>(
        val innerComponent: Component,
        val outerComponentViewHolder: Class<out NestedOuterComponentViewHolder<T>>,
        val outerComponentViewModel: T
)