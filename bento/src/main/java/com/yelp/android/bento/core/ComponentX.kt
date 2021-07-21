package com.yelp.android.bento.core

import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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

/**
 * Helper delegate that can be used in a single item component, that will take care of notifying for
 * change when its value gets updated.
 */
fun <T> Component.notifyingItem(initialValue: T): ReadWriteProperty<Any?, T> {
    return object : ObservableProperty<T>(initialValue) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (oldValue != newValue) {
                notifyDataChanged()
            }
        }
    }
}
