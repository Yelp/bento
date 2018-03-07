package com.yelp.android.bento.base;

import com.yelp.android.bento.core.Component;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    public void test_RemoveComponent() {
        mComponentGroup.addAll(createMockComponents(1));
        Component mockComponent = mComponentGroup.get(0);
        assertTrue(mComponentGroup.contains(mockComponent));
        assertEquals(0, mComponentGroup.indexOf(mockComponent));
        mComponentGroup.remove(mockComponent);
        assertFalse(mComponentGroup.contains(mockComponent));
        assertEquals(-1, mComponentGroup.indexOf(mockComponent));
    }

    @Test
    public void test_RemovePrecedingComponent_MaintainsValidIndices() {
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
        // notifyItemRangeRemoved is a final method. This is why we have to use PowerMock.
        verify(spyObserver, times(1)).notifyItemRangeRemoved(4, 1);
    }

    private List<Component> createMockComponents(int numComponents) {
        List<Component> components = new ArrayList<>(numComponents);
        for (int i = 0; i < numComponents; i++) {
            components.add(mock(Component.class));
        }
        return components;
    }
}
