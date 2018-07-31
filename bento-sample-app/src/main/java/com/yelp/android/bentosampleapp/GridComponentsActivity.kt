package com.yelp.android.bentosampleapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.RecyclerViewComponentController
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder

class GridComponentsActivity : AppCompatActivity() {

    companion object {
        fun intentFor(context: Context): Intent {
            return Intent(context, GridComponentsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val componentController = RecyclerViewComponentController(recyclerView)

        componentController.addComponent(createListComponent())
    }

    private fun createListComponent(): ListComponent<out Any?, String> {
        return ListComponent(null, ListComponentExampleViewHolder::class.java) apply {
            toggleDivider(false)
            setData((0..9).map { "Item:$it" })
            setTopGap(50)
            setBottomGap(50)
            numberColumns = 3
        }
    }
}
