package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bentosampleapp.components.VisibilityListererListComponent
import com.yelp.android.bentosampleapp.databinding.ActivityMainBinding

class ListVisibilityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var componentController: RecyclerViewComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        componentController = RecyclerViewComponentController(binding.recyclerView)
        componentController.addComponent(VisibilityListererListComponent())
    }
}
