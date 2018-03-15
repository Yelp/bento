package com.yelp.android.bento.core;

import com.yelp.android.bento.core.ListComponent;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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
        mListComponent.setBottomGap(10);
        assertEquals(1, mListComponent.getCountInternal());
    }

    @Test
    public void test_HasTopGap_RightCount() {
        mListComponent.setTopGap(10);
        assertEquals(1, mListComponent.getCountInternal());
    }

    @Test
    public void test_HasBothGaps_RightCount() {
        mListComponent.setTopGap(10);
        mListComponent.setBottomGap(10);
        assertEquals(2, mListComponent.getCountInternal());
    }

    @Test
    public void test_GetItemBottomGap_RightItem() {
        mListComponent.setBottomGap(10);
        assertEquals(10, mListComponent.getItemInternal(0));
    }

    @Test
    public void test_GetItemTopGap_RightItem() {
        mListComponent.setTopGap(10);
        assertEquals(10, mListComponent.getItemInternal(0));
    }

    @Test
    public void test_GetItemBothGaps_RightItem() {
        mListComponent.setTopGap(10);
        mListComponent.setBottomGap(20);
        assertEquals(10, mListComponent.getItemInternal(0));
        assertEquals(20, mListComponent.getItemInternal(1));
    }
}
