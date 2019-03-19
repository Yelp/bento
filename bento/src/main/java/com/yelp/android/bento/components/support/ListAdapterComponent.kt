package com.yelp.android.bento.components.support

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentViewHolder

/**
 * A component that displays the content of a traditional ListView using a ListAdapter. Useful for
 * adding Bento support to screens that are still using ListViews. With this component, you can
 * start converting views in the original list view at the top or bottom, and render the rest of the
 * list that hasn't been converted yet as a ListAdapterComponent.
 *
 * NOTE: This should only be used with a [ListViewComponentController]
 */
class ListAdapterComponent(private val listAdapter: ListAdapter) : Component() {
    private val presenters: LoadingCache<Int, Wrapper> =
            CacheBuilder.newBuilder().build(object : CacheLoader<Int, Wrapper>() {
                override fun load(position: Int) = Wrapper(listAdapter, position)
            })

    init {
        listAdapter.registerDataSetObserver(Observer())
    }

    override fun getPresenter(position: Int): Wrapper = presenters[position]

    override fun getItem(position: Int): Any? = listAdapter.getItem(position)

    override fun getCount(): Int = listAdapter.count

    override fun getHolderType(position: Int) = ListAdapterHolderType::class.java

    /**
     * A [ListAdapter] classically recycles all items, except those whose type is
     * [ListAdapter.IGNORE_ITEM_VIEW_TYPE]. We need to expose this behavior, so that the
     * [ListAdapterComponent] can notify the the [android.widget.ListView].
     */
    fun getViewType(position: Int): Any {
        val itemViewType = listAdapter.getItemViewType(position)
        return if (itemViewType == ListAdapter.IGNORE_ITEM_VIEW_TYPE) {
            ListAdapter.IGNORE_ITEM_VIEW_TYPE
        } else {
            ViewType(listAdapter, itemViewType)
        }
    }

    /** @see ListAdapter.isEnabled */
    fun isEnabled(position: Int) = listAdapter.isEnabled(position)

    class ListAdapterHolderType : ListViewComponentViewHolder<Any?>() {
        lateinit var parent: ViewGroup
        lateinit var view: View

        override fun inflate(presenter: Wrapper, parent: ViewGroup): View {
            this.parent = parent
            return presenter.listAdapter.getView(presenter.position, null, parent)
                    .also { view = it }
        }

        override fun bind(presenter: Wrapper, element: Any?) {
            presenter.listAdapter.getView(presenter.position, view, parent)
        }
    }

    data class Wrapper(val listAdapter: ListAdapter, val position: Int)

    data class ViewType(val adapter: ListAdapter, val itemViewType: Int)

    private inner class Observer : DataSetObserver() {
        override fun onChanged() {
            presenters.invalidateAll()
            notifyDataChanged()
        }

        override fun onInvalidated() = onChanged()
    }
}

abstract class ListViewComponentViewHolder<T> : ComponentViewHolder<ListAdapterComponent.Wrapper, T>() {
    abstract fun inflate(presenter: ListAdapterComponent.Wrapper, parent: ViewGroup): View

    final override fun inflate(parent: ViewGroup): View {
        throw UnsupportedOperationException("Unsupported. You should only use a " +
                "ListAdapterComponent from within a ListViewComponentController")
    }
}
