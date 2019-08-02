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
    private val mViewPagerComponentController: TabViewPagerComponentController =
            TabViewPagerComponentController()
    private val mTabViewPagerComponentViewHolderData: TabViewPagerComponentViewHolderData

    init {
        mViewPagerComponentController.pageTitles = viewModel.pageTitles
        mViewPagerComponentController.addAll(viewModel.componentList)
        mTabViewPagerComponentViewHolderData =
                TabViewPagerComponentViewHolderData(viewModel, mViewPagerComponentController)
    }

    override fun getPresenter(position: Int): ViewPager.OnPageChangeListener? {
        return this
    }

    override fun getItem(position: Int): TabViewPagerComponentViewHolderData? {
        return mTabViewPagerComponentViewHolderData
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getHolderType(position: Int): Class<out ComponentViewHolder<*, *>> {
        return TabViewPagerComponentViewHolder::class.java
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        viewModel.currentIndex = position
        onPageChangeListener?.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    class TabViewPagerComponentViewModel(
            pageTitles: List<String>, componentsList: List<Component>
    ) {
        var pageTitles: List<String>
        var componentList: List<Component>
        var currentIndex: Int = 0

        @DimenRes var tabLayoutSideMargins: Int = 0
        @DrawableRes var background: Int = 0
        @DrawableRes var tabLayoutBackground: Int = 0
        @ColorRes var tabLayoutTextColorNormal: Int = 0
        @ColorRes var tabLayoutTextColorSelected: Int = 0
        @ColorRes var tabLayoutIndicatorColor: Int = 0

        init {
            if (pageTitles.size == componentsList.size) {
                this.pageTitles = pageTitles
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
