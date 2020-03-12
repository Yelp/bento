package com.yelp.android.bentosampleapp.components

import android.util.Log
import com.yelp.android.bento.core.ComponentGroup

/**
 * Component that displays a long list and makes a log whenever there is a new item at the top of
 * the list.
 */
class VisibilityListererListComponent : ComponentGroup() {

    val data = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
            "sit amet egestas augue. Ut congue eleifend maximus. Vestibulum eget feugiat est. " +
            "Cras eget aliquam ligula, quis varius tortor. Praesent nec sollicitudin felis, sed " +
            "pharetra metus. Proin sodales mi sed urna condimentum, nec mollis lorem placerat." +
            " Nulla viverra tempor pellentesque. Fusce porta arcu et metus sagittis, ut luctus " +
            "nulla fermentum. Vivamus nec ipsum condimentum, congue nibh ac, condimentum enim." +
            " Aliquam mi nisi, rhoncus sed sollicitudin quis, imperdiet nec nibh. Aenean dictum, " +
            "eros quis tempus eleifend, arcu ipsum placerat orci, ac dignissim magna tortor " +
            "tristique orci. Sed bibendum lectus sit amet tellus ornare efficitur. Sed facilisis" +
            " lorem at erat pulvinar aliquam. Vivamus vestibulum varius elit ut venenatis. Morbi " +
            "et euismod mauris. Suspendisse at quam in quam suscipit fringilla.")
            .split(" ")

    init {
        addAll(data.map {
            LabeledComponent(it)
        })
    }

    override fun onItemAtTop(index: Int) {
        super.onItemAtTop(index)

        Log.i("Item At Top", data[index])
    }
}
