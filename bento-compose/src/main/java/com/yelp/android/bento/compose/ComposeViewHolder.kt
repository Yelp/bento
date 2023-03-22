package com.yelp.android.bento.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.yelp.android.bento.core.ComponentViewHolder

/**
 * ViewHolder which allows compatability with Jetpack Compose. Basically, this lets you write the
 * view holders view code with Compose.
 */
abstract class ComposeViewHolder<P, T> : ComponentViewHolder<P, T>() {

    var state: MutableState<T?> = mutableStateOf(null)
    var composeView: ComposeView? = null
    var presenter: P? = null
    var element: T? = null

    final override fun inflate(parent: ViewGroup): View {
        return ComposeView(parent.context)
            .apply {
            presenter?.let {
                element?.let { nonNullElement ->
                    BindView(
                        this,
                        it,
                        nonNullElement
                    )
                }
            }
        }.also { composeView = it }
//            .apply {
//            setContent {
//                state = remember { mutableStateOf(element) }
//                presenter?.let { element?.let { nonNullElement -> BindView(it, nonNullElement) } }
//            }
//            id = View.generateViewId()
//        }
//            .also {
//            composeView = it
//        }
    }

    override fun bind(presenter: P, element: T) {
        this.presenter = presenter
        this.element = element
        state.value = element
    }

    abstract fun BindView(composeView: ComposeView?, presenter: P, element: T)
}
