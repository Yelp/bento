package com.yelp.android.bento.componentcontrollers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.yelp.android.bento.R
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder.TabViewPagerComponentViewHolderData
import com.yelp.android.bento.components.TabViewPagerComponent
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.utils.inflate

class TabViewPagerComponentViewHolder : ComponentViewHolder<OnPageChangeListener, TabViewPagerComponentViewHolderData>() {

    private lateinit var context: Context
    private lateinit var container: View
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun inflate(parent: ViewGroup): View {
        context = parent.context
        return parent.inflate<View>(R.layout.bento_component_tab_view_pager).apply {
            container = findViewById(R.id.tab_view_pager_parent)
            tabLayout = findViewById(R.id.tab_layout)
            viewPager = findViewById(R.id.view_pager)

            tabLayout.setupWithViewPager(viewPager)
            viewPager.offscreenPageLimit = Integer.MAX_VALUE
        }
    }

    override fun bind(
            presenter: OnPageChangeListener,
            element: TabViewPagerComponentViewHolderData
    ) {
        element.viewPagerComponentController.setViewPager(viewPager)

        viewPager.removeOnPageChangeListener(presenter)
        viewPager.addOnPageChangeListener(presenter)

        if (element.viewModel.background != 0) {
            container.setBackgroundResource(element.viewModel.background)
        }
        if (element.viewModel.tabLayoutBackground != 0) {
            tabLayout.setBackgroundResource(element.viewModel.tabLayoutBackground)
        }
        if (element.viewModel.tabLayoutIndicatorColor != 0) {
            tabLayout.setSelectedTabIndicatorColor(
                    context.resources.getColor(element.viewModel.tabLayoutIndicatorColor))
        }
        if (element.viewModel.tabLayoutTextColorNormal != 0 && element.viewModel.tabLayoutTextColorSelected != 0) {
            tabLayout.setTabTextColors(
                    context.resources.getColor(element.viewModel.tabLayoutTextColorNormal),
                    context.resources
                            .getColor(element.viewModel.tabLayoutTextColorSelected))
        }
        if (element.viewModel.tabLayoutSideMargins != 0) {
            val tabLayoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            tabLayoutParams.setMargins(
                    context.resources
                            .getDimension(element.viewModel.tabLayoutSideMargins).toInt(),
                    0,
                    context.resources
                            .getDimension(element.viewModel.tabLayoutSideMargins).toInt(),
                    0)
            tabLayout.layoutParams = tabLayoutParams
        }

        viewPager.currentItem = element.viewModel.currentIndex
    }

    data class TabViewPagerComponentViewHolderData(
            val viewModel: TabViewPagerComponent.TabViewPagerComponentViewModel,
            val viewPagerComponentController: TabViewPagerComponentController
    )
}
