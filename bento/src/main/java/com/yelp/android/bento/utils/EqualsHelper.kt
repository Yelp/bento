package com.yelp.android.bento.utils

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object EqualsHelper {
    fun Any?.deepEquals(other: Any?): Boolean {
        if (this == null && other == null) {
            return true
        }
        if (this == null || other == null) {
            return false
        }

        if (this::class != other::class) return false

        val kClass: KClass<out Any> = this::class
        if (kClass.java.isPrimitive || kClass.isData) {
            return this == other
        }

        return kClass.memberProperties.all { member ->
            @Suppress("UNCHECKED_CAST")
            val castedMember = member as KProperty1<Any, *>
            val wasAccessible = castedMember.isAccessible
            castedMember.isAccessible = true
            val memberA = castedMember.get(this)
            val memberB = castedMember.get(other)
            castedMember.isAccessible = wasAccessible

            memberA.deepEquals(memberB)
        }
    }
}
