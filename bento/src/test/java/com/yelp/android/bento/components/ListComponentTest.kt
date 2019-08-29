package com.yelp.android.bento.components

import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.yelp.android.bento.core.TestComponentViewHolder
import org.junit.Assert.assertEquals
import org.junit.Test

class ListComponentTest {

    private lateinit var listComponent: ListComponent<Nothing?, Nothing?>

    @Test
    fun removeItemGettersFiveItemsFirstItemNoDividers_FirstItemCountOne() {
        setup(5)
        listComponent.toggleDivider(false)

        assertEquals(
                "Expected to start removing at the first item",
                0,
                listComponent.getRemoveIndexStart(0))
        assertEquals("Only expected to remove one item", 1, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersFiveItemsMiddleItemNoDividers_MiddleItemCountOne() {
        setup(5)
        listComponent.toggleDivider(false)

        assertEquals(
                "Expected to start removing at the middle item",
                2,
                listComponent.getRemoveIndexStart(2))
        assertEquals("Only expected to remove one item", 1, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersFiveItemsLastItemNoDividers_LastItemCountOne() {
        setup(5)
        listComponent.toggleDivider(false)

        assertEquals(
                "Expected to start removing at the Last item",
                4,
                listComponent.getRemoveIndexStart(4))
        assertEquals("Only expected to remove one item", 1, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersSingleItemOnlyItemNoDividers_OnlyItemCountOne() {
        setup(1)
        listComponent.toggleDivider(false)

        assertEquals(
                "Expected to start removing at the only item",
                0,
                listComponent.getRemoveIndexStart(0))
        assertEquals("Only expected to remove one item", 1, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersFiveItemsFirstItemWithDividers_FirstItemCountTwo() {
        setup(5)
        listComponent.toggleDivider(true)

        assertEquals(
                "Expected to start removing at the first item",
                0,
                listComponent.getRemoveIndexStart(0))
        assertEquals("Expected to remove two items", 2, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersFiveItemsMiddleItemWithDividers_MiddleItemTimesTwoMinusOneCountTwo() {
        setup(5)
        listComponent.toggleDivider(true)

        assertEquals(
                "Expected to start removing at the first item",
                3,
                listComponent.getRemoveIndexStart(2))
        assertEquals("Expected to remove two items", 2, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersFiveItemsLastItemWithDividers_LastItemTimesTwoMinusOneCountTwo() {
        setup(5)
        listComponent.toggleDivider(true)

        assertEquals(
                "Expected to start removing at the first item",
                7,
                listComponent.getRemoveIndexStart(4))
        assertEquals("Expected to remove two items", 2, listComponent.removeItemCount)
    }

    @Test
    fun removeItemGettersSingleItemOnlyItemWithDividers_FirstItemCountOne() {
        setup(1)
        listComponent.toggleDivider(true)

        assertEquals(
                "Expected to start removing at the first item",
                0,
                listComponent.getRemoveIndexStart(0))
        assertEquals("Expected to remove one item", 1, listComponent.removeItemCount)
    }

    @Test
    fun getSpanSizeLookupTwoColumnsGapItem_ReturnsTwo() {
        setup(2, 2)
        listComponent.setStartGap(10)
        val spanSizeLookup = listComponent.spanSizeLookup
        assertEquals(2, spanSizeLookup.getSpanSize(0))
    }

    @Test
    fun getSpanSizeLookupTwoColumnsNonGapItem_ReturnsOne() {
        setup(2, 2)
        listComponent.setStartGap(10)
        val spanSizeLookup = listComponent.spanSizeLookup
        assertEquals(1, spanSizeLookup.getSpanSize(1))
    }

    @Test
    fun setSpanSizeLookupCustomReturnsTen_ReturnsLookupWithTen() {
        setup(2)
        listComponent.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 10
            }
        }

        assertEquals(10, listComponent.spanSizeLookup.getSpanSize(0))
    }

    @Test
    fun whenDividerEnabledAndOnItemVisibleCalled_CallsOnListItemVisibleForCorrectIndices() {
        val numberOfItems = 10
        setup(numberOfItems)

        val spy = spy(listComponent)
        for (i in 0 until numberOfItems) {
            spy.onItemVisible(i)
            // When the index is an odd number, onListItemVisible() should still have been called
            // only once because the item in an even number index is a divider and we should no-op
            // for dividers.
            verify(spy).onListItemVisible(i / 2)
            if (i > numberOfItems / 2) {
                verify(spy, never()).onListItemVisible(i)
            }
        }
    }

    @Test
    fun whenDividerDisabledAndOnItemVisibleCalled_CallsOnListItemVisibleForCorrectIndices() {
        val numberOfItems = 10
        setup(numberOfItems)
        listComponent.toggleDivider(false)

        val spy = spy(listComponent)
        for (i in 0 until numberOfItems) {
            spy.onItemVisible(i)
            verify(spy).onListItemVisible(i)
        }
    }

    @Test
    fun whenDividerEnabledAndOnItemNotVisibleCalled_CallsOnListItemNotVisibleForCorrectIndices() {
        val numberOfItems = 10
        setup(numberOfItems)

        val spy = spy(listComponent)
        for (i in 0 until numberOfItems) {
            spy.onItemNotVisible(i)
            // When the index is an odd number, onListItemNotVisible() should still have been called
            // only once because the item in an even number index is a divider and we should no-op
            // for dividers.
            verify(spy).onListItemNotVisible(i / 2)
            if (i > numberOfItems / 2) {
                verify(spy, never()).onListItemNotVisible(i)
            }
        }
    }

    @Test
    fun whenDividerDisabledAndOnItemNotVisibleCalled_CallsOnListItemNotVisibleForCorrectIndices() {
        val numberOfItems = 10
        setup(numberOfItems)
        listComponent.toggleDivider(false)

        val spy = spy(listComponent)
        for (i in 0 until numberOfItems) {
            spy.onItemNotVisible(i)
            verify(spy).onListItemNotVisible(i)
        }
    }

    @Test
    fun appendingDataWithDividers_NotifiesCorrectIndices() {
        setup(5)
        listComponent.toggleDivider(true)

        val listSpy = spy(listComponent)
        listSpy.appendData(listOf(null, null, null))
        // We started with 5 items and inserted 3. Since there are dividers, we need to offset both
        // the start index and the number being added (since we will also add dividers for the new
        // items)
        verify(listSpy).notifyItemRangeInserted(9, 6)
    }

    @Test
    fun appendingDataWithoutDividers_NotifiesCorrectIndices() {
        setup(5)
        listComponent.toggleDivider(false)

        val listSpy = spy(listComponent)
        listSpy.appendData(listOf(null, null, null))
        // We started with 5 items and inserted 3.
        verify(listSpy).notifyItemRangeInserted(5, 3)
    }

    /**
     * Adds fake items to the list component.
     *
     * @param count The number of items to add.
     * @param lanes The number of columns the list should have
     */
    private fun setup(count: Int, lanes: Int = 1) {
        listComponent = ListComponent(null, TestComponentViewHolder::class.java, lanes)
        listComponent.setData((1..count).map { null })
    }
}
