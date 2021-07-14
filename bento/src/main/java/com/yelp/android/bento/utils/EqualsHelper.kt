package com.yelp.android.bento.utils

import org.apache.commons.lang3.builder.EqualsBuilder

object EqualsHelper {
    // Works well enough, but not for collections.
    fun Any?.deepEquals(other: Any?): Boolean {
        return when {
            this == null && other == null -> true
            this == null || other == null -> false
            this::class != other::class -> false
            this::class.isData -> this == other
            else -> EqualsBuilder.reflectionEquals(this, other)
        }
    }
}
