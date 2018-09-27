package com.yelp.android.bentosampleapp

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.RecyclerViewComponentController
import com.yelp.android.bentosampleapp.components.ActivityStarterViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var componentController: RecyclerViewComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        componentController = RecyclerViewComponentController(recyclerView)

        val listComponent = ListComponent<Context, Pair<String, Class<out AppCompatActivity>>>(
                this,
                ActivityStarterViewHolder::class.java
        )
        listComponent.setData(listOf(
                "Recycler View" to RecyclerViewActivity::class.java,
                "Recycler View with grid" to GridComponentsActivity::class.java,
                "List View" to ListViewActivity::class.java,
                "View Pager" to ViewPagerActivity::class.java,
                "Visibility Listener" to ListVisibilityActivity::class.java
        ))
        componentController.addComponent(listComponent)
    }
}