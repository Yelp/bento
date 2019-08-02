package com.yelp.android.bento.componentcontrollers

import java.util.ArrayList

class TabViewPagerComponentController : ViewPagerComponentController() {
    var pageTitles: List<String> = ArrayList()

    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitles[position]
    }
}
