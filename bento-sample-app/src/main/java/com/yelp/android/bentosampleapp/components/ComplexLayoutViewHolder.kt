package com.yelp.android.bentosampleapp.components

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.utils.inflate
import com.yelp.android.bentosampleapp.R

class ComplexLayoutViewHolder : ComponentViewHolder<Unit, String>() {
    lateinit var name: TextView
    lateinit var location: TextView
    lateinit var cuisine: TextView
    lateinit var rating: TextView
    lateinit var review1: TextView
    lateinit var review2: TextView

    override fun inflate(parent: ViewGroup): View =
            parent.inflate<View>(R.layout.complex_layout).also { view ->
                name = view.findViewById(R.id.name)
                location = view.findViewById(R.id.location)
                cuisine = view.findViewById(R.id.cuisine)
                rating = view.findViewById(R.id.rating)
                review1 = view.findViewById(R.id.review1)
                review2 = view.findViewById(R.id.review2)
            }

    override fun bind(presenter: Unit, element: String) {
        name.text = "Margaritas"
        location.text = "Redwood City"
        cuisine.text = "Mexican"
        rating.text = "$element Stars"
        review1.text = "Great Food!"
        review2.text = "Great Drinks!"
    }
}