package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.components.OnItemMovedCallback
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.LabeledComponentViewHolder
import kotlinx.android.synthetic.main.activity_recycler_view.*

class ReorderListActivity : AppCompatActivity(), Presenter {

    private lateinit var handleComponent: ListComponent<Presenter, ReorderElement>
    private lateinit var longPressComponent: ListComponent<Unit, String>

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
        longPressComponent =
                ListComponent<Unit, String>(Unit, LabeledComponentViewHolder::class.java, 2)
        longPressComponent.setIsReorderable(true)
        longPressComponent.toggleDivider(false)
        longPressComponent.setData((0..10).map { 'a'.plus(it).toString() })
        longPressComponent.setOnItemMovedCallback(object : OnItemMovedCallback<String> {
            override fun onItemMoved(oldIndex: Int, newIndex: Int, newData: List<String>) {
                Log.i("Reordered", "New list ordering: $newData")
            }
        })
        componentController.addComponent(longPressComponent)
    }

    private fun addHandleOrderableListComponent() {
        componentController.addComponent(LabeledComponent("Drag handle to reorder"))
        handleComponent =
                ListComponent<Presenter, ReorderElement>(this, ReorderViewHolder::class.java, 2)
        handleComponent.setIsReorderable(true)
        handleComponent.toggleDivider(false)
        handleComponent.setData((0..10).map { ReorderElement('A'.plus(it).toString(), it) })
        handleComponent.setOnItemMovedCallback(object : OnItemMovedCallback<ReorderElement> {
            override fun onItemMoved(oldIndex: Int, newIndex: Int, newData: List<ReorderElement>) {
                newData.forEachIndexed { index, reorderElement -> reorderElement.index = index }
                Log.i("Reordered", "New list ordering: $newData")
            }
        })
        componentController.addComponent(handleComponent)
    }

    override fun onStartDrag(position: Int) {
        componentController.onItemPickedUp(handleComponent, position)
    }
}

interface Presenter {
    fun onStartDrag(position: Int)
}

class ReorderViewHolder : ComponentViewHolder<Presenter, ReorderElement>() {

    private lateinit var text: TextView
    private lateinit var presenter: Presenter
    private lateinit var element: ReorderElement

    override fun inflate(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.reorderable_view_holder, parent, false).apply {
                    text = findViewById(R.id.textview)
                    findViewById<View>(R.id.handle).setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            presenter.onStartDrag(element.index)
                        }
                        false
                    }
                }
    }

    override fun bind(presenter: Presenter, element: ReorderElement) {
        text.text = element.text
        this.element = element
        this.presenter = presenter
    }
}

data class ReorderElement(
        val text: String,
        var index: Int
)

fun <T> MutableList<T>.move(oldIndex: Int, newIndex: Int) {
    add(newIndex, removeAt(oldIndex))
}