package com.yelp.android.bento.core.support

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.ListView
import com.yelp.android.bento.R
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bento.core.ComponentGroup
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.core.ComponentVisibilityListener
import com.yelp.android.bento.utils.AccordionList

private const val MAX_ITEM_TYPES_PER_ADAPTER = 4096

class ListViewComponentController(val listView: ListView) :
        ComponentController,
        AbsListView.OnScrollListener {
    private val components = ComponentGroup()
    private var adapter: Adapter = Adapter()
    private val componentVisibilityListener =
            ComponentVisibilityListener(ListViewLayoutManagerHelper(listView), components)
    private var isRecreating: Boolean = false

    init {
        components.registerComponentGroupObserver(ComponentObserver())
        components.registerComponentDataObserver(componentVisibilityListener)
        listView.setOnScrollListener(this)
        listView.adapter = adapter
    }

    // Component controller
    override fun getSpan(): Int = components.span

    override fun getSize(): Int = components.size

    override fun get(index: Int) = components[index]

    override fun contains(component: Component) = component in components

    override fun indexOf(component: Component) = components.indexOf(component)

    override fun rangeOf(component: Component): AccordionList.Range? {
        return components.rangeOf(component)
    }

    override fun addComponent(component: Component): ComponentController {
        components.addComponent(component)
        componentVisibilityListener.onComponentAdded(component)
        return this
    }

    override fun addComponent(componentGroup: ComponentGroup): ComponentController {
        components.addComponent(componentGroup)
        componentVisibilityListener.onComponentAdded(componentGroup)
        return this
    }

    override fun addComponent(index: Int, component: Component): ComponentController {
        components.addComponent(index, component)
        componentVisibilityListener.onComponentAdded(component)
        return this
    }

    override fun addComponent(index: Int, componentGroup: ComponentGroup): ComponentController {
        components.addComponent(index, componentGroup)
        componentVisibilityListener.onComponentAdded(componentGroup)
        return this
    }

    override fun addAll(components: MutableCollection<out Component>): ComponentController {
        this.components.addAll(components)
        components.forEach { componentVisibilityListener.onComponentAdded(it) }
        return this
    }

    override fun setComponent(index: Int, component: Component): ComponentController {
        components.setComponent(index, component)
        return this
    }

    override fun setComponent(index: Int, componentGroup: ComponentGroup): ComponentController {
        components.setComponent(index, componentGroup)
        return this
    }

    override fun remove(index: Int): Component {
        return components.remove(index)
    }

    override fun remove(component: Component): Boolean {
        return components.remove(component)
    }

    override fun clear() {
        components.clear()
        componentVisibilityListener.clear()
        recreate()
    }

    private fun recreate() {
        isRecreating = true
        val onSaveInstanceState = listView.onSaveInstanceState()
        adapter = Adapter().also {
            listView.adapter = adapter
            listView.clearDisappearingChildren()
            listView.onRestoreInstanceState(onSaveInstanceState)
        }
        isRecreating = false
    }

    private inner class Adapter : BaseAdapter() {
        private val itemViewTypes = mutableListOf<Any>()
        internal val itemTypes = mutableMapOf<Int, Int>()

        // List adapter
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            when {
                convertView != null -> {
                    @Suppress("UNCHECKED_CAST")
                    val holder: ComponentViewHolder<Any?, Any?> =
                            convertView.getTag(R.id.bento_list_view_holder)
                                    as ComponentViewHolder<Any?, Any?>
                    holder.bind(components.getPresenter(position), components.getItem(position))
                    return convertView
                }
                else -> {
                    val holderType: Class<out ComponentViewHolder<Any?, Any?>> =
                            components.getHolderType(position)
                    val holder = holderType.newInstance()
                    val view = if (holder is ListViewComponentViewHolder) {
                        holder.inflate(components.getPresenter(position) as ListAdapterComponent.Wrapper,
                                parent)
                    } else {
                        holder.inflate(parent).also {
                            holder.bind(components.getPresenter(position),
                                    components.getItem(position))
                        }
                    }

                    view.setTag(R.id.bento_list_view_holder, holder)
                    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(v: View) {
                            holder.onViewAttachedToWindow()
                        }

                        override fun onViewDetachedFromWindow(v: View) {
                            holder.onViewDetachedFromWindow()
                        }
                    })
                    return view
                }
            }
        }

        /**
         * Arbitrary high number. The [ListView] creates internally an array of the same size.
         */
        override fun getViewTypeCount(): Int = MAX_ITEM_TYPES_PER_ADAPTER

        override fun getItemViewType(position: Int): Int {
            if (position in itemTypes) return itemTypes.getValue(position)

            val component = components.componentAt(position)
            val holderType = if (component is ListAdapterComponent) {
                val innerPosition = position - (components.rangeOf(component)?.mLower
                        ?: return ListAdapter.IGNORE_ITEM_VIEW_TYPE)
                component.getViewType(innerPosition)
            } else {
                components.getHolderType(position)
            }

            return when (holderType) {
                ListAdapter.IGNORE_ITEM_VIEW_TYPE -> ListAdapter.IGNORE_ITEM_VIEW_TYPE
                in itemViewTypes -> itemViewTypes.indexOf(holderType)
                else -> {
                    itemViewTypes.add(holderType)
                    // If we are reaching the limit of types per adapter, we can't keep increasing
                    // the number. We should instead ignore the item view type, but recreate a
                    // fresh adapter with a count to 0.
                    if (itemViewTypes.size >= MAX_ITEM_TYPES_PER_ADAPTER) {
                        if (!isRecreating) {
                            listView.post { recreate() }
                            isRecreating = true
                        }
                        ListAdapter.IGNORE_ITEM_VIEW_TYPE
                    } else {
                        itemViewTypes.size - 1
                    }
                }
            }.also {
                itemTypes[position] = it
            }
        }

        override fun getItem(position: Int) = components.getItem(position)

        override fun hasStableIds() = false

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = components.span
    }

    // Scroll listener
    override fun onScroll(
            view: AbsListView,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
    ) {
        componentVisibilityListener.onScrolled()
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {} // Do nothing

    private inner class ComponentObserver : ComponentGroup.ComponentGroupDataObserver {
        override fun onChanged() {
            adapter.itemTypes.clear()
            adapter.notifyDataSetChanged()
        }

        override fun onComponentRemoved(component: Component) = onChanged()
    }

    private class ListViewLayoutManagerHelper(val listView: ListView) :
            ComponentVisibilityListener.LayoutManagerHelper {
        override fun findFirstVisibleItemPosition() = listView.firstVisiblePosition
        override fun findLastVisibleItemPosition() = listView.lastVisiblePosition
    }
}