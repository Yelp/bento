package com.yelp.android.bento.core;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import androidx.viewpager.widget.ViewPager;

import com.yelp.android.bento.core.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.core.Component.ComponentDataObserver;

import org.junit.Before;
import org.junit.Test;

/** Tests for {@link ViewPagerComponentController}. */
public class ViewPagerComponentControllerTest {

    private ComponentGroup mComponentGroup;
    private ViewPagerComponentController mComponentController;

    @Before
    public void setup() {
        mComponentGroup = spy(ComponentGroup.class);
        doNothing()
                .when(mComponentGroup)
                .registerComponentDataObserver(any(ComponentDataObserver.class));
        doNothing()
                .when(mComponentGroup)
                .registerComponentGroupObserver(any(ComponentGroupDataObserver.class));
        mComponentController = new ViewPagerComponentController();
        mComponentController.setComponentGroup(mComponentGroup);
    }

    @Test
    public void test_SettingViewPagerAdapter() {
        ViewPager mockViewPager = mock(ViewPager.class);
        when(mockViewPager.getAdapter()).thenReturn(null);
        mComponentController.setViewPager(mockViewPager);
    }

    @Test
    public void test_AddComponent_AddsComponent() {
        Component mockComponent = mock(Component.class);
        mComponentController.addComponent(mockComponent);
        assertEquals(1, mComponentController.getSize());
        assertEquals(mockComponent, mComponentController.get(0));
        assertEquals(0, mComponentController.indexOf(mockComponent));
    }

    @Test
    public void test_AddComponentAtIndex_AddsComponent() {
        Component mockComponentAtIndexOne = mock(Component.class);
        mComponentController.addComponent(mock(Component.class));
        mComponentController.addComponent(1, mockComponentAtIndexOne);
        assertEquals(2, mComponentController.getSize());
        assertEquals(mockComponentAtIndexOne, mComponentController.get(1));
        assertThat(mockComponentAtIndexOne, not(equalTo(mComponentController.get(0))));
        assertEquals(1, mComponentController.indexOf(mockComponentAtIndexOne));
    }

    @Test
    public void test_AddComponentGroup_AddsComponentGroup() {
        ComponentGroup mockComponentGroup = mock(ComponentGroup.class);
        mComponentController.addComponent(mockComponentGroup);
        assertEquals(1, mComponentController.getSize());
        assertEquals(mockComponentGroup, mComponentController.get(0));
    }

    @Test
    public void test_AddComponentGroupAtIndex_AddsComponentGroup() {
        Component mockComponentGroupAtIndexOne = mock(ComponentGroup.class);
        mComponentController.addComponent(mock(Component.class));
        mComponentController.addComponent(1, mockComponentGroupAtIndexOne);
        assertEquals(2, mComponentController.getSize());
        assertEquals(mockComponentGroupAtIndexOne, mComponentController.get(1));
        assertThat(mockComponentGroupAtIndexOne, not(equalTo(mComponentController.get(0))));
    }

    @Test
    public void test_RemoveComponent_RemovesComponent() {
        Component mockComponent = mock(Component.class);
        mComponentController.addComponent(mockComponent);
        assertEquals(1, mComponentController.getSize());
        assertEquals(mockComponent, mComponentController.get(0));

        mComponentController.remove(mockComponent);
        assertEquals(0, mComponentController.getSize());
    }

    @Test
    public void test_RemoveComponentAtIndex_RemovesComponent() {
        Component mockComponent = mock(Component.class);
        mComponentController.addComponent(mock(Component.class));
        mComponentController.addComponent(mockComponent);
        assertEquals(2, mComponentController.getSize());
        assertThat(mockComponent, not(equalTo(mComponentController.get(0))));
        assertEquals(mockComponent, mComponentController.get(1));

        mComponentController.remove(1);
        assertEquals(1, mComponentController.getSize());
        assertFalse(mComponentController.contains(mockComponent));
    }

    @Test
    public void test_Clear_RemovesAllComponents() {
        // Add a few components first.
        Component mockComponent = mock(Component.class);
        mComponentController.addComponent(mockComponent);
        mComponentController.addComponent(mock(Component.class));

        assertEquals(2, mComponentController.getSize());
        assertEquals(0, mComponentController.indexOf(mockComponent));

        mComponentController.clear();
        assertEquals(0, mComponentController.getSize());
        assertEquals(-1, mComponentController.indexOf(mockComponent));
    }

    @Test
    public void test_GetCount_ReflectsGetSize() {
        assertEquals(0, mComponentController.getSize());
        assertEquals(0, mComponentController.getCount());

        mComponentController.addComponent(mock(Component.class));
        mComponentController.addComponent(mock(Component.class));

        assertEquals(2, mComponentController.getSize());
        assertEquals(2, mComponentController.getCount());
    }
}
