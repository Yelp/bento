package com.yelp.android.bento.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    private val rowStates = mutableMapOf<Int, MutableState<T>>()

    final override fun inflate(parent: ViewGroup): View {
        composeView = ComposeView(parent.context).apply {
            id = View.generateViewId()
        }
        return composeView
    }

    override fun bind(presenter: P, element: T) {
        this.presenter = presenter
        this.element = element
        composeView.setContent {
            key(absolutePosition){
                val state = remember { mutableStateOf(element) }
                BindView(presenter, state.value)
            }
        }
    }

    @Composable
    abstract fun BindView(presenter: P, element: T)
}
