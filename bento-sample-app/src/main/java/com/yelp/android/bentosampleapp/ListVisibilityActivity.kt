package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bentosampleapp.components.VisibilityListererListComponent
import kotlinx.android.synthetic.main.activity_main.*

class ListVisibilityActivity : AppCompatActivity() {
    private lateinit var componentController: RecyclerViewComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        componentController = RecyclerViewComponentController(recyclerView)
        componentController.addComponent(VisibilityListererListComponent())
    }
}
