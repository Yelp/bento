package com.yelp.android.bento.components;

import static junit.framework.Assert.assertEquals;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ListComponentTest {

    private ListComponent<Void, Object> mListComponent;

    @Test
    public void test_RemoveItemGettersFiveItemsFirstItemNoDividers_FirstItemCountOne() {
        setup(5);
        mListComponent.toggleDivider(false);

        assertEquals(
                "Expected to start removing at the first item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersFiveItemsMiddleItemNoDividers_MiddleItemCountOne() {
        setup(5);
        mListComponent.toggleDivider(false);

        assertEquals(
                "Expected to start removing at the middle item",
                2,
                mListComponent.getRemoveIndexStart(2));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersFiveItemsLastItemNoDividers_LastItemCountOne() {
        setup(5);
        mListComponent.toggleDivider(false);

        assertEquals(
                "Expected to start removing at the Last item",
                4,
                mListComponent.getRemoveIndexStart(4));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersSingleItemOnlyItemNoDividers_OnlyItemCountOne() {
        setup(1);
        mListComponent.toggleDivider(false);

        assertEquals(
                "Expected to start removing at the only item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersFiveItemsFirstItemWithDividers_FirstItemCountTwo() {
        setup(5);
        mListComponent.toggleDivider(true);

        assertEquals(
                "Expected to start removing at the first item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Expected to remove two items", 2, mListComponent.getRemoveItemCount());
    }

    @Test
    public void
            test_RemoveItemGettersFiveItemsMiddleItemWithDividers_MiddleItemTimesTwoMinusOneCountTwo() {
        setup(5);
        mListComponent.toggleDivider(true);

        assertEquals(
                "Expected to start removing at the first item",
                3,
                mListComponent.getRemoveIndexStart(2));
        assertEquals("Expected to remove two items", 2, mListComponent.getRemoveItemCount());
    }

    @Test
    public void
            test_RemoveItemGettersFiveItemsLastItemWithDividers_LastItemTimesTwoMinusOneCountTwo() {
        setup(5);
        mListComponent.toggleDivider(true);

        assertEquals(
                "Expected to start removing at the first item",
                7,
                mListComponent.getRemoveIndexStart(4));
        assertEquals("Expected to remove two items", 2, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersSingleItemOnlyItemWithDividers_FirstItemCountOne() {
        setup(1);
        mListComponent.toggleDivider(true);

        assertEquals(
                "Expected to start removing at the first item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_GetSpanSizeLookupTwoColumnsGapItem_ReturnsTwo() {
        setup(2, 2);
        mListComponent.setStartGap(10);
        GridLayoutManager.SpanSizeLookup spanSizeLookup = mListComponent.getSpanSizeLookup();
        assertEquals(2, spanSizeLookup.getSpanSize(0));
    }

    @Test
    public void test_GetSpanSizeLookupTwoColumnsNonGapItem_ReturnsOne() {
        setup(2, 2);
        mListComponent.setStartGap(10);
        GridLayoutManager.SpanSizeLookup spanSizeLookup = mListComponent.getSpanSizeLookup();
        assertEquals(1, spanSizeLookup.getSpanSize(1));
    }

    @Test
    public void test_SetSpanSizeLookupCustomReturnsTen_ReturnsLookupWithTen() {
        setup(2);
        SpanSizeLookup customLookup = new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 10;
            }
        };

        mListComponent.setSpanSizeLookup(customLookup);

        assertEquals(10, mListComponent.getSpanSizeLookup().getSpanSize(0));
    }

    /**
     * Adds fake items to the list component.
     * @param count The number of items to add.
     * @param lanes The number of columns the list should have
     */
    private void setup(int count, int lanes) {
        mListComponent = new ListComponent<>(null, null, lanes);
        List<Object> fakeData = new ArrayList<>(count);

        while (count > 0) {
            count--;
            fakeData.add(new Object());
        }
        mListComponent.appendData(fakeData);
    }

    private void setup(int count) {
        setup(count, 1);
    }
}
