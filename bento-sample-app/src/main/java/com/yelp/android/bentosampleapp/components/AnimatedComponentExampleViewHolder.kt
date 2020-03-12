package com.yelp.android.bentosampleapp.components

import android.animation.Animator
import android.animation.AnimatorInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.yelp.android.bento.componentcontrollers.SimpleComponentViewHolder
import com.yelp.android.bentosampleapp.R

class AnimatedComponentExampleViewHolder : SimpleComponentViewHolder<Unit>(R.layout.simple_component_example) {
    private lateinit var textView: TextView
    private lateinit var animation: Animation
    private lateinit var animator: Animator

    override fun onViewCreated(itemView: View) {
        textView = itemView.findViewById(R.id.text)
        animation = AnimationUtils.loadAnimation(itemView.context, R.anim.sample_animation)
        animator = AnimatorInflater.loadAnimator(itemView.context, R.animator.sample_animator)
        animator.setTarget(textView)
    }

    override fun bind(presenter: Unit) {
        textView.text = "This is an animated component."
    }

    override fun onViewAttachedToWindow() {
        animator.start()
    }

    override fun onViewDetachedFromWindow() {
        textView.clearAnimation()
        animator.pause()
    }
}
