package com.yelp.android.bento.componentcontrollers

class TabViewPagerComponentController : ViewPagerComponentController() {
    var pageTitles = listOf<String>()

    override fun getPageTitle(position: Int): CharSequence? {
        return pageTitles[position]
    }
}
