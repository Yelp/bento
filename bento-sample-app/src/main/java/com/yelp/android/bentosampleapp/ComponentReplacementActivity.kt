package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.core.notifyingItem
import com.yelp.android.bento.utils.inflate
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.databinding.ActivityRecyclerViewBinding

private const val NUM_LABELED_COMPONENTS = 30

class ComponentReplacementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerViewBinding
    private val componentController by lazy {
        RecyclerViewComponentController(binding.recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        componentController.addComponent(ReplaceComponentButton(NUM_LABELED_COMPONENTS + 1) {
            componentController.replaceComponent(2, LabeledComponent(it.toString()))
        })
        componentController.addAll(List(NUM_LABELED_COMPONENTS) {
            LabeledComponent((it + 1).toString())
        })
    }
}

class ReplaceComponentButton(
    replacementId: Int,
    private val onComponentClicked: (replacenentId: Int) -> Unit
) : Component() {
    private var replacementId by notifyingItem(replacementId)
    override fun getPresenter(position: Int) = this

    override fun getItem(position: Int) = replacementId

    override fun getCount() = 1

    override fun getHolderType(position: Int) = ReplaceComponentViewHolder::class.java

    fun onClick() {
        onComponentClicked(replacementId)
        replacementId++
    }
}

class ReplaceComponentViewHolder : ComponentViewHolder<ReplaceComponentButton, Int>() {
    private lateinit var itemButton: Button

    override fun inflate(parent: ViewGroup) =
            parent.inflate<Button>(R.layout.item_button).also { itemButton = it }

    override fun bind(presenter: ReplaceComponentButton, element: Int) {
        itemButton.text = "Replace Second Labeled Component with $element"
        itemButton.setOnClickListener { presenter.onClick() }
    }
}
