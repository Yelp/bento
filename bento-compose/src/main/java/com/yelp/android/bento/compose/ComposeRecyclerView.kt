package com.yelp.android.bento.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView

@Composable
fun RecyclerView(
    modifier: Modifier = Modifier,
    optionalAlsoBlock: (RecyclerView) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            RecyclerView(context).also {
                optionalAlsoBlock(it)
            }
        }
    )
}
