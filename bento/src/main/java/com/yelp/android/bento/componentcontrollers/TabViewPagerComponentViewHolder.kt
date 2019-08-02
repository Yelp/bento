package com.yelp.android.bento.componentcontrollers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.yelp.android.bento.R
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder.TabViewPagerComponentViewHolderData
import com.yelp.android.bento.components.TabViewPagerComponent.TabViewPagerComponentViewModel
import com.yelp.android.bento.core.ComponentViewHolder

class TabViewPagerComponentViewHolder : ComponentViewHolder<OnPageChangeListener, TabViewPagerComponentViewHolderData>() {

    private lateinit var mContext: Context
    private lateinit var mContainer: View
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun inflate(parent: ViewGroup): View {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bento_component_tab_view_pager, parent, false)
        mContainer = view.findViewById(R.id.tab_view_pager_parent)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = Integer.MAX_VALUE
        return view
    }

    override fun bind(
            presenter: OnPageChangeListener,
            element: TabViewPagerComponentViewHolderData
    ) {
        element.viewPagerComponentController.setViewPager(viewPager)

        viewPager.removeOnPageChangeListener(presenter)
        viewPager.addOnPageChangeListener(presenter)

        if (element.viewModel.background != 0) {
            mContainer.setBackgroundResource(element.viewModel.background)
        }
        if (element.viewModel.tabLayoutBackground != 0) {
            tabLayout.setBackgroundResource(element.viewModel.tabLayoutBackground)
        }
        if (element.viewModel.tabLayoutIndicatorColor != 0) {
            tabLayout.setSelectedTabIndicatorColor(
                    mContext.resources.getColor(element.viewModel.tabLayoutIndicatorColor))
        }
        if (element.viewModel.tabLayoutTextColorNormal != 0 && element.viewModel.tabLayoutTextColorSelected != 0) {
            tabLayout.setTabTextColors(
                    mContext.resources.getColor(element.viewModel.tabLayoutTextColorNormal),
                    mContext.resources
                            .getColor(element.viewModel.tabLayoutTextColorSelected))
        }
        if (element.viewModel.tabLayoutSideMargins != 0) {
            val tabLayoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            tabLayoutParams.setMargins(
                    mContext.resources
                            .getDimension(element.viewModel.tabLayoutSideMargins).toInt(),
                    0,
                    mContext.resources
                            .getDimension(element.viewModel.tabLayoutSideMargins).toInt(),
                    0)
            tabLayout.layoutParams = tabLayoutParams
        }

        viewPager.currentItem = element.viewModel.currentIndex
    }

    class TabViewPagerComponentViewHolderData(
            var viewModel: TabViewPagerComponentViewModel,
            val viewPagerComponentController: TabViewPagerComponentController
    )
}
