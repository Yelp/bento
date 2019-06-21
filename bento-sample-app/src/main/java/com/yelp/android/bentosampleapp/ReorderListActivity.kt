package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.components.OnItemMovedCallback
import com.yelp.android.bento.core.ComponentGroup
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.utils.inflate
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.LabeledComponentViewHolder
import kotlinx.android.synthetic.main.activity_recycler_view.*

class ReorderListActivity : AppCompatActivity(), Presenter {

    private val componentController by lazy {
        RecyclerViewComponentController(recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recycler_view)

        addUnorderableListComponent()
        addLongPressOrderableListComponent()
        addHandleOrderableListComponent()
    }

    private fun addUnorderableListComponent() {
        componentController.addComponent(LabeledComponent("No reorder"))
        val list = ListComponent<Unit, String>(Unit, LabeledComponentViewHolder::class.java, 2)
        list.toggleDivider(false)
        list.setData((0..10).map { it.toString() })
        componentController.addComponent(list)
    }

    private fun addLongPressOrderableListComponent() {
        componentController.addComponent(LabeledComponent("Long press to reorder"))
        val longPressComponent =
                ListComponent<Unit, String>(Unit, LabeledComponentViewHolder::class.java, 2)
        longPressComponent.setIsReorderable(true)
        longPressComponent.toggleDivider(false)
        longPressComponent.setData((0..10).map { 'a'.plus(it).toString() })
        longPressComponent.setOnItemMovedCallback(object : OnItemMovedCallback<String> {
            override fun onItemMoved(oldIndex: Int, newIndex: Int) {
                Log.i("Reordered", "Item at $oldIndex moved to $newIndex")
            }
        })
        componentController.addComponent(longPressComponent)
    }

    private fun addHandleOrderableListComponent() {
        val handleComponent =
                ListComponent<Presenter, String>(this, ReorderViewHolder::class.java, 2)
        handleComponent.setIsReorderable(true)
        handleComponent.toggleDivider(false)
        handleComponent.setData((0..10).map { 'A'.plus(it).toString() })
        handleComponent.setOnItemMovedCallback(object : OnItemMovedCallback<String> {
            override fun onItemMoved(oldIndex: Int, newIndex: Int) {
                Log.i("Reordered", "Item at $oldIndex moved to $newIndex")
            }
        })
        val componentGroup = ComponentGroup().apply {
            addComponent(LabeledComponent("Drag handle to reorder"))
            addComponent(handleComponent)
        }
        componentController.addComponent(componentGroup)
    }

    override fun onStartDrag(viewHolder: ComponentViewHolder<Presenter, String>) {
        componentController.onItemPickedUp(viewHolder)
    }
}

interface Presenter {
    fun onStartDrag(viewHolder: ComponentViewHolder<Presenter, String>)
}

class ReorderViewHolder : ComponentViewHolder<Presenter, String>() {

    private lateinit var text: TextView
    private lateinit var presenter: Presenter

    override fun inflate(parent: ViewGroup): View {
        return parent.inflate<View>(R.layout.reorderable_view_holder).also {
                    text = it.findViewById(R.id.textview)
                    it.findViewById<View>(R.id.handle).setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            presenter.onStartDrag(this)
                        }
                        false
                    }
                }
    }

    override fun bind(presenter: Presenter, element: String) {
        text.text = element
        this.presenter = presenter
    }
}
