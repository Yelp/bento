@file:JvmName("ComponentControllerX")
package com.yelp.android.bento.core

/**
 * Returns the data items of all the [Component]s in a [ComponentController] as a sequence.
 */
fun ComponentController.asItemSequence(): Sequence<Any?> {
    return sequenceOf(*Array(size) { index ->
        get(index).asItemSequence()
    }).flatten()
}
