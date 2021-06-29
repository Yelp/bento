package com.yelp.android.bentosampleapp.components

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bentosampleapp.R

class ActivityStarterViewHolder : ComponentViewHolder<Context, Pair<String, Class<out AppCompatActivity>>>() {
    lateinit var button: Button

    override fun inflate(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
                .also { button = it as Button }
        return view
    }

    override fun bind(presenter: Context, element: Pair<String, Class<out AppCompatActivity>>) {
        button.text = element.first
        button.setOnClickListener {
            presenter.startActivity(Intent(presenter, element.second))
        }
    }
}
