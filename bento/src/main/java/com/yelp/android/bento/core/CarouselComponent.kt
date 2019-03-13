package com.yelp.android.bento.core

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.R
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
    private var sharedPool: RecyclerView.RecycledViewPool? = null

    final override fun getItem(position: Int) = CarouselViewModel(group, sharedPool)

    final override fun getPresenter(position: Int) = null

    final override fun getCount() = 1

    final override fun getHolderType(position: Int) = carouselViewHolder

    final override fun sharePool(pool: RecyclerView.RecycledViewPool) {
        sharedPool = pool
    }

    fun addComponent(component: Component) {
        group.addComponent(component)
    }

    fun addComponent(index: Int, component: Component) {
        group.addComponent(index, component)
    }

    fun addAll(components: Collection<Component>) = components.forEach(::addComponent)
}

/**
 * ComponentViewHolder for a carousel used by [CarouselComponent]. This class is open to allow
 * subclasses to customize the nested [RecyclerView].
 */
open class CarouselComponentViewHolder : ComponentViewHolder<Unit?, CarouselViewModel>() {

    lateinit var recyclerView: RecyclerView
    lateinit var controller: RecyclerViewComponentController
    private var attachedPool = false

    final override fun inflate(parent: ViewGroup): View {
        return createRecyclerView(parent).apply {
            recyclerView = this
            controller = RecyclerViewComponentController(this, RecyclerView.HORIZONTAL)
            isNestedScrollingEnabled = false
        }
    }

    final override fun bind(presenter: Unit?, element: CarouselViewModel) {
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
    }

    /**
     * Will be called to inflate a [RecyclerView]. This method only exists to allow subclasses to
     * customize the RecyclerView. e.g. set padding, margins, etc.
     * Note: Since this class is intended to be used in a horizontal carousel, the RecyclerView
     * should have a height of wrap_content.
     */
    open fun createRecyclerView(parent: ViewGroup): RecyclerView {
        return parent.inflate(R.layout.recycler_view)
    }
}

data class CarouselViewModel(
        val group: ComponentGroup,
        val sharedPool: RecyclerView.RecycledViewPool?
)