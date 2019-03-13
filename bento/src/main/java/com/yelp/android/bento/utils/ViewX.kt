package com.yelp.android.bento.utils

import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

inline fun <reified T : View> ViewGroup.inflate(@LayoutRes layout: Int): T {
    return LayoutInflater.from(context).inflate(layout, this, false) as T
}