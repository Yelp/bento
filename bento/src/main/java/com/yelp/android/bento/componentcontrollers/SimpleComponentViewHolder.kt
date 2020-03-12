package com.yelp.android.bento.componentcontrollers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.yelp.android.bento.core.ComponentViewHolder

/**
 * Abstract implementation of [ComponentViewHolder] that provides a default implementation for
 * very simple components. This class should be extended when the component does not need dynamic
 * data and only has one layout that will ever be inflated.
 * <br></br><br></br>
 * See: ContributionsComponent for an example.
 */
abstract class SimpleComponentViewHolder<P> protected constructor(
    @param:LayoutRes
    @field:LayoutRes
    private val layoutId: Int
) : ComponentViewHolder<P, Unit?>() {

    protected abstract fun onViewCreated(itemView: View)

    override fun inflate(parent: ViewGroup): View {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        onViewCreated(itemView)
        return itemView
    }

    final override fun bind(presenter: P, element: Unit?) {
        bind(presenter)
    }

    /**
     * Using FindViewById is a heavy and non-performant method and should never be called in the
     * bind method. You should have fields for any views you wish to modify during the bind method
     * and instantiate them during inflation.
     */
    open fun bind(presenter: P) {}
}
