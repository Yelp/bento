package com.yelp.android.bentosampleapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bentosampleapp.components.ActivityStarterViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var componentController: RecyclerViewComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        componentController = RecyclerViewComponentController(recyclerView)

        val listComponent =
                ListComponent<Context, Pair<String, Class<out AppCompatActivity>>>(
                        this,
                        ActivityStarterViewHolder::class.java
                )
        listComponent.setData(listOf(
                "Recycler View" to RecyclerViewActivity::class.java,
                "Recycler View with grid" to GridComponentsActivity::class.java,
                "Horizontal recycler View with grid" to
                        HorizontalGridComponentsActivity::class.java,
                "List View" to ListViewActivity::class.java,
                "View Pager" to ViewPagerActivity::class.java,
                "Visibility Listener" to ListVisibilityActivity::class.java,
                "Grid View Pager" to ViewPagerGridActivity::class.java
        ))
        componentController.addComponent(listComponent)
    }
}