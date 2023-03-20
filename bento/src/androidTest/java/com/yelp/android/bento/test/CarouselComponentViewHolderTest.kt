package com.yelp.android.bento.test

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

private const val ITEM_POSITION = 3
private const val SCROLL_OFFSET = 20

class CarouselComponentViewHolderTest : ComponentViewHolderTestCase<Unit?, CarouselViewModel>() {

    @Test
    fun providingRecycledPool_SharesPool() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        val pool = RecyclerView.RecycledViewPool()

        bindViewHolder(
            CarouselComponentViewHolder::class.java,
            null,
            CarouselViewModel(group, pool)
        )
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

        val group = ComponentGroup().addAll(
            listOf(
                ComponentGroup().addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                carousels[0],
                ComponentGroup().addAll((1..10).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
                    .addComponent(carousels[1])
                    .addAll((1..5).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                ComponentGroup().addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) }),
                carousels[2]
            )
        )

        carousels.forEachIndexed { index, carousel ->
            assertNull("At carousel: $index", carousel.getItem(0).sharedPool)
        }
        controller.addComponent(group)
        carousels.forEachIndexed { index, carousel ->
            assertEquals("At carousel: $index", pool, carousel.getItem(0).sharedPool)
        }
    }

    @Test
    fun savedLinearLayoutManager_IsRestoredOnBind() {
        val group = ComponentGroup()
        group.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        val pool = RecyclerView.RecycledViewPool()

        // Setup a carousel, scroll, then detach from window to store LayoutManager's info.
        bindViewHolder(
            CarouselComponentViewHolder::class.java,
            null,
            CarouselViewModel(group, pool)
        )
        val (initialRecyclerView, initialElement) = getHolder<CarouselComponentViewHolder>().let {
            Pair(it.recyclerView, it.element)
        }

        IdlingRegistry.getInstance()
            .register(RecyclerViewScrollStateIdlingResource(initialRecyclerView))
        runOnMainSync {
            (initialRecyclerView.layoutManager as? LinearLayoutManager)?.smoothScrollToPosition(
                initialRecyclerView,
                null,
                ITEM_POSITION
            )
        }
        Espresso.onIdle()
        initialRecyclerView.scrollBy(SCROLL_OFFSET, 0)

        getHolder<CarouselComponentViewHolder>().onViewDetachedFromWindow()
        val layoutManagerState = initialElement?.layoutManagerState
        assertNotNull(layoutManagerState)

        // Rebind a ViewHolder, passing the stored layoutManagerState before verifying that
        // the position and offset are properly applied.
        bindViewHolder(
            CarouselComponentViewHolder::class.java,
            null,
            CarouselViewModel(group, pool, layoutManagerState)
        )
        val testedRecyclerView = getHolder<CarouselComponentViewHolder>().recyclerView

        Espresso.onIdle()

        val firstVisibleItemPosition =
            (testedRecyclerView.layoutManager as? LinearLayoutManager)
                ?.findFirstVisibleItemPosition()

        val firstVisibleItemOffset =
            testedRecyclerView.getChildAt(0).left - testedRecyclerView.paddingLeft

        assertEquals(ITEM_POSITION, firstVisibleItemPosition)
        assertEquals(-SCROLL_OFFSET, firstVisibleItemOffset)
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

        bindViewHolder(
            CarouselComponentViewHolder::class.java,
            null,
            CarouselViewModel(group, pool)
        )
        val holder = getHolder<CarouselComponentViewHolder>()

        val scrollToPosition = 20
        holder.recyclerView.layoutManager?.smoothScrollToPosition(
            holder.recyclerView, null, scrollToPosition
        )

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
        override fun onViewDetachedFromWindow(v: View) {
            // Do nothing.
        }

        override fun onViewAttachedToWindow(v: View) {
            mIdle = true
            mResourceCallback.onTransitionToIdle()
        }
    }
}
