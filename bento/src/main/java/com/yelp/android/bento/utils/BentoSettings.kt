package com.yelp.android.bento.utils

object BentoSettings {

    const val BENTO_TAG = "bento"

    /**
     * Global toggle for the async inflation feature in
     * [com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController]. When enabled,
     * all RecyclerViewComponentControllers will try to inflate their component's views on a
     * background thread. It's disabled by default.
     */
    @JvmStatic var asyncInflationEnabled = false

    @JvmStatic var loggingEnabled = false
}
