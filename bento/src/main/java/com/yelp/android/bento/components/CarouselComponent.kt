package com.yelp.android.bento.components

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.R
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.core.AsyncInflationStrategy.SMART
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentGroup
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bento.utils.inflate

/**
 * A component that handles a nested carousel of components. This component exposes an API similar
 * to [ComponentGroup] that will be used to provide the contents of the carousel. Consumers of this
 * class should add [Component]s in the same manner as a ComponentGroup.
 *
 * This component is best used inside a [RecyclerViewComponentController] as it will share the view
 * pool with the parent (and any [CarouselComponent] siblings).
 *
 * **NOTE:** Although this component supports any arbitrary child components, it is required that
 * you adhere to one of the following:
 * 1. The child view holders are all uniform in height.
 * 2. The tallest view holder is in the first component.
 * 3. If you know the tallest height ahead of time, you can create a [SimpleComponent] with a
 * [SimpleComponentViewHolder] that has a height, but a width of 0 and set that as the first
 * component in this carousel.
 *
 * This is due to an issue with RecyclerView's wrap_content and would require significant effort to
 * fix.
 */
open class CarouselComponent(
    private val carouselViewHolder: Class<out CarouselComponentViewHolder> = CarouselComponentViewHolder::class.java
) : Component(), RecyclerViewComponentController.SharesViewPool {

    private val group = ComponentGroup()
    private val viewModel = CarouselViewModel(group)

    final override fun getItem(position: Int) = viewModel

    final override fun getPresenter(position: Int) = null

    final override fun getCount() = 1

    final override fun getHolderType(position: Int) = carouselViewHolder

    final override fun sharePool(pool: RecyclerView.RecycledViewPool) {
        viewModel.sharedPool = pool
    }

    fun addComponent(component: Component) {
        group.addComponent(component)
    }

    fun addComponent(index: Int, component: Component) {
        group.addComponent(index, component)
    }

    fun addAll(components: Collection<Component>) = components.forEach(::addComponent)

    fun remove(index: Int) = group.remove(index)

    fun remove(component: Component) = group.remove(component)

    fun clear() = group.clear()
}

/**
 * ComponentViewHolder for a carousel used by [CarouselComponent]. This class is open to allow
 * subclasses to customize the nested [RecyclerView].
 */
open class CarouselComponentViewHolder : ComponentViewHolder<Unit?, CarouselViewModel>() {

    lateinit var recyclerView: RecyclerView
    private lateinit var controller: RecyclerViewComponentController
    var element: CarouselViewModel? = null
    private var attachedPool = false

    final override fun inflate(parent: ViewGroup): View {
        return createRecyclerView(parent).apply {
            recyclerView = this
            controller = RecyclerViewComponentController(recyclerView, RecyclerView.HORIZONTAL, true).apply {
                setAsyncCacheKey("carousel")
                setAsyncStrategy(SMART)
            }
            isNestedScrollingEnabled = false
            (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
                this.recycleChildrenOnDetach = true
            }
        }
    }

    final override fun bind(presenter: Unit?, element: CarouselViewModel) {
        this.element = element
        val (group, pool) = element
        if (!attachedPool) {
            pool?.let { recyclerView.setRecycledViewPool(it) }
            attachedPool = true
        }
        if (controller.size > 0) {
            if (controller[0] != group) {
                controller.remove(controller[0])
                controller.addComponent(group)
            }
        } else {
            controller.addComponent(group)
        }

        restoreScrollPosition()
    }

    /**
     * Will be called to inflate a [RecyclerView]. This method only exists to allow subclasses to
     * customize the RecyclerView. e.g. set padding, margins, etc.
     * Note: Since this class is intended to be used in a horizontal carousel, the RecyclerView
     * should have a height of wrap_content.
     */
    open fun createRecyclerView(parent: ViewGroup): RecyclerView {
        return parent.inflate(R.layout.bento_recycler_view)
    }

    /**
     * Saves the scroll position of the carousel to the view model so that it can be restored when
     * the carousel recyclerView itself is recycled in the larger component list. Scroll position
     * is saved when the scroll state of the carousel transitions to
     * [RecyclerView.SCROLL_STATE_IDLE].
     */
    private fun saveScrollPosition() {
        if (recyclerView.childCount < 1) {
            element?.layoutManagerState = null
            return
        }
        (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
            element?.layoutManagerState = onSaveInstanceState()
        }
    }

    private fun restoreScrollPosition() {
        val element = element ?: return
        val state = element.layoutManagerState ?: return
        (recyclerView.layoutManager as? LinearLayoutManager)?.onRestoreInstanceState(state)
    }

    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        saveScrollPosition()
        controller.onRecyclerViewDetachedFromWindow()
    }

    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        controller.onRecyclerViewAttachedToWindow()
        restoreScrollPosition()
    }
}

data class CarouselViewModel(
    val group: ComponentGroup,
    var sharedPool: RecyclerView.RecycledViewPool? = null,
    var layoutManagerState: Parcelable? = null
)
