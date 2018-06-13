package com.yelp.android.bentosampleapp.components

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.yelp.android.bento.core.SimpleComponentViewHolder
import com.yelp.android.bentosampleapp.R

class AnimatedComponentExampleViewHolder : SimpleComponentViewHolder<Unit>(R.layout.simple_component_example) {
    private lateinit var textView: TextView
    private lateinit var animation: Animation

    override fun onViewCreated(itemView: View) {
        textView = itemView.findViewById(R.id.text)
        animation = AnimationUtils.loadAnimation(itemView.context, R.anim.sample_animation)
    }

    override fun bind(presenter: Unit, element: Void?) {
        super.bind(presenter, element)
        textView.text = "This is an animated component."
    }

    override fun onViewAttachedToWindow() {
        textView.startAnimation(animation)
    }

    override fun onViewDetachedFromWindow() {
        textView.clearAnimation()
        animation.apply {
            cancel()
            reset()
        }
    }
}