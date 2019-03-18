package com.yelp.android.bento.utils

/**
 * An interface to allow a class to provide its data as a sequence. Mostly used for espresso view
 * matching.
 */
interface Sequenceable {
    fun asItemSequence(): Sequence<Any?>
}