package com.yelp.android.bento.core

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AsyncInflationBridgeTest {

    private lateinit var asyncInflationBridge: AsyncInflationBridge

    private val scope: CoroutineScope = TestCoroutineScope()
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context: Context = ApplicationProvider.getApplicationContext()
        val recyclerView = spy(RecyclerView(context)).apply {
            layoutManager = LinearLayoutManager(context)
        }
        asyncInflationBridge = spy(AsyncInflationBridge(
                recyclerView,
                asyncInflaterDispatcher = testDispatcher,
                defaultBridgeDispatcher = testDispatcher
        ))
        whenever(asyncInflationBridge.coroutineScope).doReturn(scope)
    }

    @Test
    fun asyncInflateViewsForComponent_oneComponentWithCount1_returnsExpectedResults() {
        val component = ComponentGroupTest.createMockComponents(1)[0]
        asyncInflationBridge.asyncInflateViewsForComponent(component) { }
        val viewHolder = asyncInflationBridge.getViewHolder(TestComponentViewHolder::class.java)
        assertNotNull(viewHolder)
        val view = asyncInflationBridge.getView(viewHolder!!) // Deffo not null.
        assertNotNull(view)
    }

    @Test
    fun asyncInflateViewsForComponent_oneComponentWithCount2_returnsExpectedResults() {
        val component = ComponentGroupTest.createMockComponents(1)[0]
        whenever(component.count).doReturn(2)
        asyncInflationBridge.asyncInflateViewsForComponent(component) { }
        val viewHolder = asyncInflationBridge.getViewHolder(TestComponentViewHolder::class.java)
        assertNotNull(viewHolder)
        val view = asyncInflationBridge.getView(viewHolder!!) // Deffo not null.
        assertNotNull(view)

        val viewHolder2 = asyncInflationBridge.getViewHolder(TestComponentViewHolder::class.java)
        assertNotEquals(viewHolder, viewHolder2)
        val view2 = asyncInflationBridge.getView(viewHolder2!!)
        assertNotEquals(view, view2)
    }

    @Test
    fun asyncInflateViewsForComponent_twoComponentsWithCount1_produces4Views() {
        val component1 = ComponentGroupTest.createMockComponents(2)[0]
        val component2 = ComponentGroupTest.createMockComponents(2)[1]

        asyncInflationBridge.asyncInflateViewsForComponent(component1) { }
        asyncInflationBridge.asyncInflateViewsForComponent(component2) { }

        val viewHolder = asyncInflationBridge.getViewHolder(TestComponentViewHolder::class.java)
        val viewHolder1 = asyncInflationBridge.getViewHolder(TestComponentViewHolder::class.java)
        assertNotEquals(viewHolder, viewHolder1)

        val view = asyncInflationBridge.getView(viewHolder!!)
        val view2 = asyncInflationBridge.getView(viewHolder1!!)
        assertNotEquals(view, view2)
    }
}
