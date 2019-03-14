package com.yelp.android.bento.core;

import static junit.framework.Assert.assertEquals;

import com.yelp.android.bento.components.ListComponent;
import org.junit.Before;
import org.junit.Test;

public class ComponentTest {

    private ListComponent<Void, Object> mListComponent;

    @Before
    public void setup() {
        mListComponent = new ListComponent<>(null, null);
    }

    @Test
    public void test_GetCountNoGaps_RightCount() {
        assertEquals(0, mListComponent.getCount());
    }

    @Test
    public void test_HasBottomGap_RightCount() {
        mListComponent.setEndGap(10);
        assertEquals(1, mListComponent.getCountInternal());
    }

    @Test
    public void test_HasTopGap_RightCount() {
        mListComponent.setStartGap(10);
        assertEquals(1, mListComponent.getCountInternal());
    }

    @Test
    public void test_HasBothGaps_RightCount() {
        mListComponent.setStartGap(10);
        mListComponent.setEndGap(10);
        assertEquals(2, mListComponent.getCountInternal());
    }

    @Test
    public void test_GetItemBottomGap_RightItem() {
        mListComponent.setEndGap(10);
        assertEquals(10, mListComponent.getItemInternal(0));
    }

    @Test
    public void test_GetItemTopGap_RightItem() {
        mListComponent.setStartGap(10);
        assertEquals(10, mListComponent.getItemInternal(0));
    }

    @Test
    public void test_GetItemBothGaps_RightItem() {
        mListComponent.setStartGap(10);
        mListComponent.setEndGap(20);
        assertEquals(10, mListComponent.getItemInternal(0));
        assertEquals(20, mListComponent.getItemInternal(1));
    }
}
