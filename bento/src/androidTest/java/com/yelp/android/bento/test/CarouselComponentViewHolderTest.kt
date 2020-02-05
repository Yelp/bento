package com.yelp.android.bento.test

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.platform.app.InstrumentationRegistry
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.CarouselComponentViewHolder
import com.yelp.android.bento.components.CarouselViewModel
import com.yelp.android.bento.components.SimpleComponent
import com.yelp.android.bento.core.ComponentGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CarouselComponentViewHolderTest : ComponentViewHolderTestCase<Unit?, CarouselViewModel>() {

    @Test
    fun providingRecycledPool_SharesPool() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(CarouselComponentViewHolder::class.java, null, CarouselViewModel(group, pool))
        val holder = getHolder<CarouselComponentViewHolder>()

        assertEquals(pool, holder.recyclerView.recycledViewPool)
    }

    @Test
    fun carousel_ReceivesSharePool() {
        val context = mActivityTestRule.activity
        val recyclerView = RecyclerView(context)
        val pool = recyclerView.recycledViewPool

        lateinit var controller: RecyclerViewComponentController
        runOnMainSync {
            controller = RecyclerViewComponentController(recyclerView)
        }
        val carousel = CarouselComponent()

        assertNull(carousel.getItem(0).sharedPool)
        controller.addComponent(carousel)
        assertEquals(pool, carousel.getItem(0).sharedPool)
    }

    @Test
    fun nestedCarousel_ReceivesSharePool() {
        val context = mActivityTestRule.activity
        val recyclerView = RecyclerView(context)
        val pool = recyclerView.recycledViewPool

        lateinit var controller: RecyclerViewComponentController
        runOnMainSync {
            controller = RecyclerViewComponentController(recyclerView)
        }
        val carousels = (1..3).map { CarouselComponent() }

        val group = ComponentGroup().addAll(listOf(
                ComponentGroup().addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                carousels[0],
                ComponentGroup().addAll((1..10).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
                        .addComponent(carousels[1])
                        .addAll((1..5).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                ComponentGroup().addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                carousels[2]
        ))

        carousels.forEachIndexed { index, carousel ->
            assertNull("At carousel: $index", carousel.getItem(0).sharedPool)
        }
        controller.addComponent(group)
        carousels.forEachIndexed { index, carousel ->
            assertEquals("At carousel: $index", pool, carousel.getItem(0).sharedPool)
        }
    }

    @Test
    fun savedScrollPositionWithOffset_IsRestoredOnBind() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(
                CarouselComponentViewHolder::class.java,
                null,
                CarouselViewModel(group, pool, scrollPosition = 3, scrollPositionOffset = -200)
        )
        val (recyclerView, element) = getHolder<CarouselComponentViewHolder>().let {
            Pair(it.recyclerView, it.element)
        }

        ViewAttachedToWindowIdlingResource(recyclerView).registerIdleTransitionCallback {
            val firstVisibleItemPosition =
                    (recyclerView.layoutManager as? LinearLayoutManager)
                            ?.findFirstVisibleItemPosition()

            val firstVisibleItemOffset = recyclerView.getChildAt(0).left - recyclerView.paddingLeft

            assertEquals(element.scrollPosition, firstVisibleItemPosition)
            assertEquals(element.scrollPositionOffset, firstVisibleItemOffset)
        }
    }

    @Test
    fun scrollingCarousel_SavesScrollPosition() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(CarouselComponentViewHolder::class.java, null, CarouselViewModel(group, pool))
        val holder = getHolder<CarouselComponentViewHolder>()

        val scrollToPosition = 3
        holder.recyclerView.layoutManager?.smoothScrollToPosition(
                holder.recyclerView, null, scrollToPosition)

        RecyclerViewScrollStateIdlingResource(holder.recyclerView).registerIdleTransitionCallback {
            assertEquals(scrollToPosition, holder.element.scrollPosition)
            assertEquals(0, holder.element.scrollPositionOffset)
        }
    }

    @Test
    fun scrollingEmptyCarousel_SavesScrollPosition() {
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(CarouselComponentViewHolder::class.java, null, CarouselViewModel(ComponentGroup(), pool))
        val holder = getHolder<CarouselComponentViewHolder>()

        val scrollToPosition = 3
        holder.recyclerView.layoutManager?.smoothScrollToPosition(
                holder.recyclerView, null, scrollToPosition)

        RecyclerViewScrollStateIdlingResource(holder.recyclerView).registerIdleTransitionCallback {
            assertEquals(0, holder.element.scrollPosition)
            assertEquals(0, holder.element.scrollPositionOffset)
        }
    }

    @Test
    fun scrollCarousel_withItemVisibilityListener_getsCalledForEveryItem() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        var visibleCount = 0
        group.registerItemVisibilityListener { index, isVisible ->
            if (isVisible) visibleCount++
        }
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(CarouselComponentViewHolder::class.java, null, CarouselViewModel(group, pool))
        val holder = getHolder<CarouselComponentViewHolder>()

        val scrollToPosition = 20
        holder.recyclerView.layoutManager?.smoothScrollToPosition(
                holder.recyclerView, null, scrollToPosition)

        RecyclerViewScrollStateIdlingResource(holder.recyclerView).registerIdleTransitionCallback {
            assertEquals(20, visibleCount)
        }
    }

    private fun runOnMainSync(block: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
    }
}

private class RecyclerViewScrollStateIdlingResource(recyclerView: RecyclerView) : IdlingResource {

    private var mIdle = false

    private lateinit var mResourceCallback: ResourceCallback

    init {
        recyclerView.addOnScrollListener(IdleScrollListener())
    }

    override fun getName() = "RecyclerViewScrollStateIdlingResource"

    override fun isIdleNow(): Boolean {
        return mIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        mResourceCallback = resourceCallback
    }

    private inner class IdleScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == SCROLL_STATE_IDLE) {
                mIdle = true
                mResourceCallback.onTransitionToIdle()
            }
        }
    }
}

private class ViewAttachedToWindowIdlingResource(view: View) : IdlingResource {

    private var mIdle = false

    private lateinit var mResourceCallback: ResourceCallback

    init {
        view.addOnAttachStateChangeListener(OnAttachListener())
    }

    override fun getName() = "ViewAttachedToWindowIdlingResource"

    override fun isIdleNow(): Boolean {
        return mIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        mResourceCallback = resourceCallback
    }

    private inner class OnAttachListener : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            // Do nothing.
        }

        override fun onViewAttachedToWindow(v: View?) {
            mIdle = true
            mResourceCallback.onTransitionToIdle()
        }
    }
}