package com.yelp.android.bento.components

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentController
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder.TabViewPagerComponentViewHolderData
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentViewHolder

class TabViewPagerComponent(
    private val viewModel: TabViewPagerComponentViewModel,
    private val onPageChangeListener: TabViewPagerOnPageChangeListener?
) : Component(), ViewPager.OnPageChangeListener {
    private val viewPagerComponentController: TabViewPagerComponentController =
            TabViewPagerComponentController()
    private val tabViewPagerComponentViewHolderData: TabViewPagerComponentViewHolderData

    init {
        viewPagerComponentController.pageTitles = viewModel.pageTitles
        viewPagerComponentController.addAll(viewModel.componentList)
        tabViewPagerComponentViewHolderData =
                TabViewPagerComponentViewHolderData(viewModel, viewPagerComponentController)
    }

    override fun getPresenter(position: Int): ViewPager.OnPageChangeListener? = this

    override fun getItem(position: Int) = tabViewPagerComponentViewHolderData

    override fun getCount() = 1

    override fun getHolderType(position: Int): Class<out ComponentViewHolder<*, *>> =
        TabViewPagerComponentViewHolder::class.java

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        viewModel.currentIndex = position
        onPageChangeListener?.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    data class TabViewPagerComponentViewModel(
        val pageTitles: List<String>,
        val componentsList: List<Component>,
        var currentIndex: Int = 0,
        @DimenRes var tabLayoutSideMargins: Int = 0,
        @DrawableRes var background: Int = 0,
        @DrawableRes var tabLayoutBackground: Int = 0,
        @ColorRes var tabLayoutTextColorNormal: Int = 0,
        @ColorRes var tabLayoutTextColorSelected: Int = 0,
        @ColorRes var tabLayoutIndicatorColor: Int = 0
    ) {
        var tabTitles: List<String>
        var componentList: List<Component>

        init {
            /**
             * The count of tabTitles or pageTitles need to be the same as componentsList
             * because there must be one component per tab.
             */
            if (pageTitles.size == componentsList.size) {
                tabTitles = pageTitles
                componentList = componentsList
            } else {
                throw IllegalArgumentException(
                        "Number of titles must match number of components")
            }
        }
    }

    interface TabViewPagerOnPageChangeListener {

        fun onPageSelected(position: Int)
    }
}
