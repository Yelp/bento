package com.yelp.android.bentosampleapp.rules

import org.junit.rules.ExternalResource

class BeforeRule(val beforeCallback: () -> Unit) : ExternalResource() {

    override fun before() {
        beforeCallback()
    }
}
