package com.yelp.android.bento.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

/** Unit tests for {@link ComponentGroup}. */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ComponentGroup.class)
public class ComponentGroupTest {

    private ComponentGroup mComponentGroup;

    @Before
    public void setup() {
        mComponentGroup = new ComponentGroup();
    }

    @Test
    public void removeComponent() {
        mComponentGroup.addAll(createMockComponents(1));
        Component mockComponent = mComponentGroup.get(0);
        assertTrue(mComponentGroup.contains(mockComponent));
        assertEquals(0, mComponentGroup.indexOf(mockComponent));
        mComponentGroup.remove(mockComponent);
        assertFalse(mComponentGroup.contains(mockComponent));
        assertEquals(-1, mComponentGroup.indexOf(mockComponent));
    }

    @Test
    public void componentGroupWithGap_MapsItemsPropery() {
        Component simpleComponent =
                new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class);
        simpleComponent.setStartGap(250);
        simpleComponent.setEndGap(125);
        mComponentGroup.addComponent(simpleComponent);
        mComponentGroup.setStartGap(250);

        // Counts
        assertEquals(4, mComponentGroup.getCountInternal()); // Gap -> Gap -> SimpleComponent -> Gap
        assertEquals(3, mComponentGroup.getCount()); // Gap -> SimpleComponent -> Gap
        assertEquals(3, mComponentGroup.getSpan()); // Gap -> SimpleComponent -> Gap
        assertEquals(1, mComponentGroup.getSize()); // SimpleComponent

        // Presenters
        assertEquals(null, mComponentGroup.getPresenterInternal(0)); // Gap
        assertEquals(null, mComponentGroup.getPresenterInternal(1)); // Gap
        assertEquals(
                Integer.MAX_VALUE, mComponentGroup.getPresenterInternal(2)); // Actual component
        assertEquals(null, mComponentGroup.getPresenterInternal(3)); // Gap

        // View Holders
        assertEquals(GapViewHolder.class, mComponentGroup.getHolderTypeInternal(0)); // Gap
        assertEquals(GapViewHolder.class, mComponentGroup.getHolderTypeInternal(1)); // Gap
        assertEquals(
                SimpleComponentViewHolder.class,
                mComponentGroup.getHolderTypeInternal(2)); // Actual component
        assertEquals(GapViewHolder.class, mComponentGroup.getHolderTypeInternal(3)); // Gap

        // Items
        assertEquals(250, mComponentGroup.getItemInternal(0)); // Gap
        assertEquals(250, mComponentGroup.getItemInternal(1)); // Gap
        assertEquals(null, mComponentGroup.getItemInternal(2)); // Actual component
        assertEquals(125, mComponentGroup.getItemInternal(3)); // Gap
    }

    @Test
    public void componentGroupWithBottomGap_DoesntNotifyGaps() {
        mComponentGroup.setEndGap(250);
        Component simpleComponent =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        mComponentGroup.addComponent(simpleComponent);
        // The system will tell the component group that the gap element is visible on the screen.
        mComponentGroup.notifyVisibilityChange(1, true);

        verify(simpleComponent, times(0)).onItemVisible(0);
    }

    @Test
    public void componentGroupWithBottomGap_NotifiesChildren() {
        mComponentGroup.setEndGap(250);
        Component simpleComponent =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        mComponentGroup.addComponent(simpleComponent);
        // The system will tell the component group that the gap element is visible on the screen.
        mComponentGroup.notifyVisibilityChange(0, true);

        verify(simpleComponent, times(1)).onItemVisible(0);
    }

    @Test
    public void componentGroupWithTopGap_DoesntNotifyGaps() {
        mComponentGroup.setStartGap(250);
        Component simpleComponent =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        mComponentGroup.addComponent(simpleComponent);
        // The system will tell the component group that the gap element is visible on the screen.
        mComponentGroup.notifyVisibilityChange(0, true);

        verify(simpleComponent, times(0)).onItemVisible(0);
    }

    @Test
    public void componentGroupWithTopGap_NotifiesChildren() {
        mComponentGroup.setStartGap(250);
        Component simpleComponent =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        mComponentGroup.addComponent(simpleComponent);
        // The system will tell the component group that the gap element is visible on the screen.
        mComponentGroup.notifyVisibilityChange(1, true);

        verify(simpleComponent, times(1)).onItemVisible(0);
    }

    @Test
    public void componentGroupMultipleComponents_ProperVisibilityNotifications() {
        Component simpleComponent1 =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        simpleComponent1.setStartGap(250);
        mComponentGroup.addComponent(simpleComponent1);

        Component simpleComponent2 =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        simpleComponent2.setStartGap(250);
        mComponentGroup.addComponent(simpleComponent2);

        Component simpleComponent3 =
                spy(new SimpleComponent(Integer.MAX_VALUE, SimpleComponentViewHolder.class));
        simpleComponent3.setStartGap(250);
        mComponentGroup.addComponent(simpleComponent3);

        // Gap no notifications
        mComponentGroup.notifyVisibilityChange(0, true);
        verify(simpleComponent1, times(0)).onItemVisible(anyInt());

        // Simple component notify
        mComponentGroup.notifyVisibilityChange(1, true);
        verify(simpleComponent1, times(1)).onItemVisible(0);

        // Gap no notifications
        mComponentGroup.notifyVisibilityChange(2, true);
        verify(simpleComponent2, times(0)).onItemVisible(anyInt());

        // Simple component notify
        mComponentGroup.notifyVisibilityChange(3, true);
        verify(simpleComponent2, times(1)).onItemVisible(0);

        // Gap no notifications
        mComponentGroup.notifyVisibilityChange(4, true);
        verify(simpleComponent3, times(0)).onItemVisible(anyInt());

        // Simple component notify
        mComponentGroup.notifyVisibilityChange(5, true);
        verify(simpleComponent3, times(1)).onItemVisible(0);
    }

    @Test
    public void removePrecedingComponent_MaintainsValidIndices() {
        List<Component> mockComponents = createMockComponents(2);
        mComponentGroup.addAll(mockComponents);

        for (int i = 0; i < mockComponents.size(); i++) {
            assertTrue(mComponentGroup.contains(mockComponents.get(i)));
            assertEquals(i, mComponentGroup.indexOf(mockComponents.get(i)));
        }

        // Remove preceding component.
        Component precedingComponent = mockComponents.get(0);
        mComponentGroup.remove(precedingComponent);
        assertFalse(mComponentGroup.contains(precedingComponent));

        // Assert remaining component still at valid index.
        Component subsequentComponent = mockComponents.get(1);
        assertTrue(mComponentGroup.contains(subsequentComponent));
        assertEquals(0, mComponentGroup.indexOf(subsequentComponent));
    }

    @Test
    public void test_NotifyRangeUpdated_ComponentRemoved_CallsNotifyItemRangeRemoved() {
        ListComponent<String, String> listComponent = new ListComponent<>(null, null);
        listComponent.toggleDivider(false);
        List<String> fakeData = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
        ComponentGroup spyObserver = spy(new ComponentGroup());

        spyObserver.addComponent(listComponent);
        listComponent.setData(fakeData);

        fakeData.remove(4);
        listComponent.setData(fakeData);

        // Alright this is unintuitive, but since Bento doesn't implement proper
        // diffing we notify that all items in the existing list have been changed and that the
        // list has changed by a size of 1. We notify the size change by saying the last element has
        // been deleted.

        // notifyItemRangeRemoved is a final method. This is why we have to use PowerMock.
        verify(spyObserver, times(1)).notifyItemRangeChanged(0, 5);
        verify(spyObserver, times(1)).notifyItemRangeRemoved(5, 1);
    }

    @Test
    public void test_NotifyRangeInserted_ComponentAddedInTheMiddle_CallsNotifyItemRangeInserted() {
        List<String> fakeData = new ArrayList<>(Arrays.asList("a", "b", "c"));
        ListComponent<String, String> listComponent0 = new ListComponent<>(null, null);
        listComponent0.toggleDivider(false);
        listComponent0.setData(fakeData);
        ListComponent<String, String> listComponent1 = new ListComponent<>(null, null);
        listComponent1.toggleDivider(false);
        listComponent1.setData(fakeData);
        ComponentGroup group = new ComponentGroup();
        group.addComponent(listComponent0);
        group.addComponent(listComponent1);

        List<String> insertedData = new ArrayList<>(Arrays.asList("0", "1", "2", "3"));
        ListComponent<String, String> insertedComponent = new ListComponent<>(null, null);
        insertedComponent.toggleDivider(false);
        insertedComponent.setData(insertedData);
        ComponentGroup spyGroup = spy(group);

        spyGroup.addComponent(1, insertedComponent);
        verify(spyGroup, times(1)).notifyItemRangeInserted(3, 4);
    }

    @Test
    public void
            test_NotifyRangeInserted_ComponentAddedAtLastPosition_CallsNotifyItemRangeInserted() {
        List<String> fakeData = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        ListComponent<String, String> listComponent0 = new ListComponent<>(null, null);
        listComponent0.toggleDivider(false);
        listComponent0.setData(fakeData);
        ComponentGroup group = new ComponentGroup();
        group.addComponent(listComponent0);

        List<String> insertedData = new ArrayList<>(Arrays.asList("0", "1", "2", "3"));
        ListComponent<String, String> insertedComponent = new ListComponent<>(null, null);
        insertedComponent.toggleDivider(false);
        insertedComponent.setData(insertedData);
        ComponentGroup spyGroup = spy(group);

        spyGroup.addComponent(insertedComponent);
        verify(spyGroup, times(1)).notifyItemRangeInserted(4, 4);
    }

    @Test
    public void test_NotifyRangeInserted_ComponentAddedAtIndex0_CallsNotifyItemRangeInserted() {
        List<String> fakeData = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        ListComponent<String, String> listComponent0 = new ListComponent<>(null, null);
        listComponent0.toggleDivider(false);
        listComponent0.setData(fakeData);
        ComponentGroup group = new ComponentGroup();
        group.addComponent(listComponent0);

        List<String> insertedData = new ArrayList<>(Arrays.asList("0", "1", "2"));
        ListComponent<String, String> insertedComponent = new ListComponent<>(null, null);
        insertedComponent.toggleDivider(false);
        insertedComponent.setData(insertedData);
        ComponentGroup spyGroup = spy(group);

        spyGroup.addComponent(insertedComponent);
        verify(spyGroup, times(0)).notifyItemRangeInserted(0, 4);
    }

    @Test
    public void test_FindFirstComponentOffset() {
        ComponentGroup group = new ComponentGroup();
        List<Component> mockComponents = createMockComponents(10);
        group.addAll(mockComponents);

        Component componentToFind = mockComponents.get(0);
        int offset = group.findComponentOffset(componentToFind);
        assertEquals(0, offset);
    }

    @Test
    public void test_FindLastComponentOffset() {
        ComponentGroup group = new ComponentGroup();
        List<Component> mockComponents = createMockComponents(10);
        group.addAll(mockComponents);

        Component componentToFind = mockComponents.get(9);
        int offset = group.findComponentOffset(componentToFind);
        assertEquals(9, offset);
    }

    @Test
    public void test_FindMiddleComponentOffset() {
        ComponentGroup group = new ComponentGroup();
        List<Component> mockComponents = createMockComponents(10);
        group.addAll(mockComponents);

        Component componentToFind = mockComponents.get(4);
        int offset = group.findComponentOffset(componentToFind);
        assertEquals(4, offset);
    }

    @Test
    public void test_FindMissingComponentOffset() {
        ComponentGroup group = new ComponentGroup();
        List<Component> mockComponents = createMockComponents(10);
        group.addAll(mockComponents);

        Component componentToFind = createMockComponents(1).get(0);
        int offset = group.findComponentOffset(componentToFind);
        assertEquals(-1, offset);
    }

    @Test
    public void test_FindNestedComponentOffset() {
        ComponentGroup group = new ComponentGroup();
        List<Component> mockComponents = createMockComponents(10);
        group.addAll(mockComponents);

        ComponentGroup nestedGroup = new ComponentGroup();
        List<Component> nestedComponents = createMockComponents(10);
        nestedGroup.addAll(nestedComponents);
        group.addComponent(nestedGroup);

        Component componentToFind = nestedComponents.get(4);
        int offset = group.findComponentOffset(componentToFind);
        assertEquals(14, offset);
    }

    @Test
    public void test_GetNumberColumns_ReturnsCorrectAnswer() {
        List<Component> components = createMockComponents(3);
        when(components.get(0).getNumberLanes()).thenReturn(2);
        when(components.get(1).getNumberLanes()).thenReturn(3);
        when(components.get(2).getNumberLanes()).thenReturn(4);

        ComponentGroup listComponent = new ComponentGroup();
        listComponent.addAll(components);
        assertEquals(12, listComponent.getNumberLanes());
    }

    private List<Component> createMockComponents(int numComponents) {
        List<Component> components = new ArrayList<>(numComponents);
        for (int i = 0; i < numComponents; i++) {
            Component mock = mock(Component.class);
            when(mock.getCount()).thenReturn(1);
            when(mock.getHolderType(anyInt()))
                    .thenAnswer(
                            new Answer<Class<TestComponentViewHolder>>() {
                                @Override
                                public Class<TestComponentViewHolder> answer(
                                        InvocationOnMock invocation) {
                                    return TestComponentViewHolder.class;
                                }
                            });
            components.add(mock);
        }

        return components;
    }
}
