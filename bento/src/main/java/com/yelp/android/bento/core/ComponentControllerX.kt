package com.yelp.android.bento.core

fun ComponentController.asItemSequence(): Sequence<Any?> {
    return sequenceOf(*Array(size) { index ->
        get(index).asItemSequence()
    }).flatten()
}