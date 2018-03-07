package com.yelp.android.bento.base;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ListComponentTest {

    private ListComponent<Void, Object> mListComponent;

    @Before
    public void setup() {
        mListComponent = new ListComponent<>(null, null);
    }

    @Test
    public void test_RemoveItemGettersFiveItemsFirstItemNoDividers_FirstItemCountOne() {
        mListComponent.toggleDivider(false);
        addItems(5);

        assertEquals(
                "Expected to start removing at the first item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersFiveItemsMiddleItemNoDividers_MiddleItemCountOne() {
        mListComponent.toggleDivider(false);
        addItems(5);

        assertEquals(
                "Expected to start removing at the middle item",
                2,
                mListComponent.getRemoveIndexStart(2));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersFiveItemsLastItemNoDividers_LastItemCountOne() {
        mListComponent.toggleDivider(false);
        addItems(5);

        assertEquals(
                "Expected to start removing at the Last item",
                4,
                mListComponent.getRemoveIndexStart(4));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersSingleItemOnlyItemNoDividers_OnlyItemCountOne() {
        mListComponent.toggleDivider(false);
        addItems(1);

        assertEquals(
                "Expected to start removing at the only item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Only expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersFiveItemsFirstItemWithDividers_FirstItemCountTwo() {
        mListComponent.toggleDivider(true);
        addItems(5);

        assertEquals(
                "Expected to start removing at the first item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Expected to remove two items", 2, mListComponent.getRemoveItemCount());
    }

    @Test
    public void
            test_RemoveItemGettersFiveItemsMiddleItemWithDividers_MiddleItemTimesTwoMinusOneCountTwo() {
        mListComponent.toggleDivider(true);
        addItems(5);

        assertEquals(
                "Expected to start removing at the first item",
                3,
                mListComponent.getRemoveIndexStart(2));
        assertEquals("Expected to remove two items", 2, mListComponent.getRemoveItemCount());
    }

    @Test
    public void
            test_RemoveItemGettersFiveItemsLastItemWithDividers_LastItemTimesTwoMinusOneCountTwo() {
        mListComponent.toggleDivider(true);
        addItems(5);

        assertEquals(
                "Expected to start removing at the first item",
                7,
                mListComponent.getRemoveIndexStart(4));
        assertEquals("Expected to remove two items", 2, mListComponent.getRemoveItemCount());
    }

    @Test
    public void test_RemoveItemGettersSingleItemOnlyItemWithDividers_FirstItemCountOne() {
        mListComponent.toggleDivider(true);
        addItems(1);

        assertEquals(
                "Expected to start removing at the first item",
                0,
                mListComponent.getRemoveIndexStart(0));
        assertEquals("Expected to remove one item", 1, mListComponent.getRemoveItemCount());
    }

    /**
     * Adds fake items to the list component.
     * @param count The number of items to add.
     */
    private void addItems(int count) {
        List<Object> fakeData = new ArrayList<>(count);

        while (count > 0) {
            count--;
            fakeData.add(new Object());
        }
        mListComponent.appendData(fakeData);
    }
}
