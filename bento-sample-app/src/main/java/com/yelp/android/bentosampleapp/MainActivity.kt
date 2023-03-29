package com.yelp.android.bentosampleapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.utils.BentoSettings
import com.yelp.android.bentosampleapp.components.ActivityStarterViewHolder
import com.yelp.android.bentosampleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var componentController: RecyclerViewComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BentoSettings.asyncInflationEnabled = true
        componentController = RecyclerViewComponentController(binding.recyclerView)

        val listComponent =
                ListComponent<Context, Pair<String, Class<out AppCompatActivity>>>(
                        this,
                        ActivityStarterViewHolder::class.java
                )
        listComponent.setData(listOf(
                "View Pager2" to ViewPager2Activity::class.java,
                "Recycler View" to RecyclerViewActivity::class.java,
                "Recycler View with grid" to GridComponentsActivity::class.java,
                "Horizontal recycler View with grid" to
                        HorizontalGridComponentsActivity::class.java,
                "List View" to ListViewActivity::class.java,
                "View Pager" to ViewPagerActivity::class.java,
                "Visibility Listener" to ListVisibilityActivity::class.java,
                "Grid View Pager" to ViewPagerGridActivity::class.java,
                "Component Replacement" to ComponentReplacementActivity::class.java,
                "Reorder Items" to ReorderListActivity::class.java,
                "Tab View Pager" to TabViewPagerActivity::class.java,
                "Toggle Scroll in RecyclerView" to ToggleScrollInRecyclerViewActivity::class.java,
                "Checking re-use of items" to RecyclingActivity::class.java
        ))
        componentController.addComponent(listComponent)
    }
}
