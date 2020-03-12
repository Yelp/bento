package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentController
import com.yelp.android.bentosampleapp.components.LabeledComponent

class TabViewPagerActivity : AppCompatActivity(),
        ViewPager.OnPageChangeListener {

    private lateinit var componentController: TabViewPagerComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bento_component_tab_view_pager)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        val mViewPager: ViewPager = findViewById(R.id.view_pager)
        mViewPager.addOnPageChangeListener(this)

        tabLayout.setupWithViewPager(mViewPager)

        componentController = TabViewPagerComponentController()
        componentController.setViewPager(mViewPager)
        addTabViewPagerComponent()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        Toast.makeText(this, "Tab $position selected", Toast.LENGTH_SHORT).show()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageSelected(position: Int) {}

    private fun addTabViewPagerComponent() {
        val titles = listOf("Tab1", "Tab2")
        componentController.pageTitles = titles
        componentController.addAll(titles.map { LabeledComponent("$it content") })
    }
}
