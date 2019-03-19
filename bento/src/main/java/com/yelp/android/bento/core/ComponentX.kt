package com.yelp.android.bento.core

/**
 * Returns the data items of the [Component] as a sequence.
 */
fun Component.asItemSequence(): Sequence<Any?> {
    class ComponentIterator(val component: Component) : Iterator<Any?> {
        private var index = 0

        override fun hasNext(): Boolean {
            return index < component.countInternal
        }

        override fun next(): Any? {
            return component.getItemInternal(index++)
        }
    }

    return Sequence {
        ComponentIterator(this)
    }
}