package com.yelp.android.bentosampleapp.components

import android.view.View
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolderAsyncCompat
import com.yelp.android.bentosampleapp.R

class ComplexLayoutViewHolder : ComponentViewHolderAsyncCompat<Unit, String>() {

    lateinit var name: TextView
    lateinit var location: TextView
    lateinit var cuisine: TextView
    lateinit var rating: TextView
    lateinit var review1: TextView
    lateinit var review2: TextView

    override val layoutId = R.layout.complex_layout

    override fun onViewCreated(view: View) {
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

