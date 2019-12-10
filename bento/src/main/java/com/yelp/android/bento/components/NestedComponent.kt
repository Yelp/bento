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
 * A {@link ListComponent} that supports nesting an inner component inside an outer viewHolder.
 * This can be used to add borders to components.
 */
open class NestedComponent(private val viewModel: NestedViewModel) : Component() {

    override fun getPresenter(position: Int) = viewModel.innerComponent.getPresenter(position)

    override fun getItem(position: Int) = viewModel

    override fun getCount() = 1

    override fun getHolderType(position: Int) = viewModel.outerComponentViewHolder::class.java
}

abstract class NestedOuterComponentViewHolder : ComponentViewHolder<Any?, NestedViewModel>() {

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

    override fun bind(presenter: Any?, element: NestedViewModel) {
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

data class NestedViewModel(
        val innerComponent: Component,
        val outerComponentViewHolder: NestedOuterComponentViewHolder
)