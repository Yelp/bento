package com.yelp.android.bento.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import com.yelp.android.bento.core.ComponentViewHolder

/**
 * ViewHolder which allows compatibility with Jetpack Compose. Basically, this lets you write the
 * view holders view code with Compose.
 */
@Deprecated(
    message = "Bento is deprecated! Only use ComposeViewHolder temporarily while migrating to full Jetpack Compose. Note that using Compose inside Bento may reduce performance. Please ensure a plan to migrate fully to Jetpack Compose soon.",
)
abstract class ComposeViewHolder<P, T> : ComponentViewHolder<P, T>() {

    private lateinit var composeView: ComposeView
    private val presenterState: MutableState<P?> = mutableStateOf(null)
    private val elementState: MutableState<T?> = mutableStateOf(null)

    final override fun inflate(parent: ViewGroup): View {
        composeView = ComposeView(parent.context).apply {
            id = View.generateViewId()
            setContent {
                val presenter: P? by remember { presenterState }
                val element: T? by remember { elementState }
                BindView(
                    presenter = presenter ?: return@setContent,
                    element = element ?: return@setContent
                )
            }
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return composeView
    }

    override fun bind(presenter: P, element: T) {
        presenterState.value = presenter
        elementState.value = element
    }

    @Composable
    abstract fun BindView(presenter: P, element: T)
}
