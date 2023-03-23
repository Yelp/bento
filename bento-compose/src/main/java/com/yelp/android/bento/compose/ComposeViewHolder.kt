package com.yelp.android.bento.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.yelp.android.bento.core.ComponentViewHolder

/**
 * ViewHolder which allows compatability with Jetpack Compose. Basically, this lets you write the
 * view holders view code with Compose.
 */
abstract class ComposeViewHolder<P, T> : ComponentViewHolder<P, T>() {

    private lateinit var composeView: ComposeView
    var presenter: P? = null
    var element: T? = null

    final override fun inflate(parent: ViewGroup): View {
        composeView = ComposeView(parent.context).apply {
            id = View.generateViewId()
        }
        return composeView
    }

    override fun bind(presenter: P, element: T) {
        this.presenter = presenter
        this.element = element
        bindView(composeView, presenter, element)
    }

    abstract fun bindView(composeView: ComposeView, presenter: P, element: T)
}
