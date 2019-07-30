package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentController
import com.yelp.android.bento.core.Component
import com.yelp.android.bentosampleapp.components.LabeledComponent
import java.util.ArrayList

class TabViewPagerActivity: AppCompatActivity(),
        ViewPager.OnPageChangeListener{

    private lateinit var componentController: TabViewPagerComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        componentController = TabViewPagerComponentController()
        setContentView(R.layout.activity_tab_viewpager)

        val mTabLayout: TabLayout = findViewById(R.id.tab_layout)

        val mViewPager: ViewPager = findViewById(R.id.view_pager)
        mViewPager.addOnPageChangeListener(this)

        mTabLayout.setupWithViewPager(mViewPager)

        componentController.setViewPager(mViewPager)
        addTabViewPagerComponent()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        Toast.makeText(this, "Tab $position selected", Toast.LENGTH_LONG).show()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageSelected(position: Int) {}

    private fun addTabViewPagerComponent() {
        val titles = ArrayList<String>()
        val componentList = ArrayList<Component>(1)
        titles.add("Tab 1")
        componentList.add(LabeledComponent("Tab 1 content"))
        titles.add("Tab 2")
        componentList.add(LabeledComponent("Tab 2 content"))
        componentController.setPageTitles(titles)
        componentController.addAll(componentList)
    }
}