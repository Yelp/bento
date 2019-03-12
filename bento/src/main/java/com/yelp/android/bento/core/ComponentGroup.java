package com.yelp.android.bento.core;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import com.yelp.android.bento.utils.AccordionList;
import com.yelp.android.bento.utils.AccordionList.Range;
import com.yelp.android.bento.utils.AccordionList.RangedValue;
import com.yelp.android.bento.utils.MathUtils;
import com.yelp.android.bento.utils.Observable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A {@link Component} comprising of zero or more ordered child {@link Component}s and managed by
 * implementing {@link ComponentController}.
 */
public class ComponentGroup extends Component {

    private final AccordionList<Component> mComponentAccordionList = new AccordionList<>();
    private final Map<Component, Integer> mComponentIndexMap = new HashMap<>();
    private final Map<Component, ComponentDataObserver> mComponentDataObserverMap = new HashMap<>();

    private final ComponentGroupObservable mObservable = new ComponentGroupObservable();

    public ComponentGroup() {
        mSpanSizeLookup = new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                RangedValue<Component> rangedValue = mComponentAccordionList.rangedValueAt(position);
                return rangedValue.mValue
                        .getSpanSizeLookup()
                        .getSpanSize(position - rangedValue.mRange.mLower);
            }
        };
    }

    public int getSpan() {
        return mComponentAccordionList.span().getSize();
    }

    public int getSize() {
        return mComponentAccordionList.size();
    }

    @NonNull
    public Component get(int index) {
        return mComponentAccordionList.get(index).mValue;
    }

    /** Returns the {@link Component} associated with the range this location belongs to. */
    public Component componentAt(int position) {
        return mComponentAccordionList.valueAt(position);
    }

    public boolean contains(@NonNull Component component) {
        return mComponentIndexMap.containsKey(component);
    }

    public int indexOf(@NonNull Component component) {
        Integer index = mComponentIndexMap.get(component);
        return index == null ? -1 : index;
    }

    public Range rangeOf(@NonNull Component component) {
        Integer index = mComponentIndexMap.get(component);
        return index == null ? null : mComponentAccordionList.get(index).mRange;
    }

    public ComponentGroup addComponent(@NonNull Component component) {
        return addComponent(getSize(), component);
    }

    public ComponentGroup addComponent(@NonNull ComponentGroup componentGroup) {
        return addComponent(getSize(), componentGroup);
    }

    public ComponentGroup addComponent(int index, @NonNull final Component component) {
        if (mComponentIndexMap.containsKey(component)) {
            throw new IllegalArgumentException("Component " + component + " already added.");
        }

        final int insertionStartIndex;
        if (mComponentAccordionList.size() > index) {
            RangedValue<Component> rangedValue = mComponentAccordionList.get(index);
            insertionStartIndex = rangedValue.mRange.mLower;
        } else {
            insertionStartIndex = getCountInternal();
        }
        addComponentAndUpdateIndices(index, component);

        ComponentDataObserver componentDataObserver = new ChildComponentDataObserver(component);
        component.registerComponentDataObserver(componentDataObserver);
        mComponentDataObserverMap.put(component, componentDataObserver);

        notifyItemRangeInserted(insertionStartIndex, component.getCountInternal());
        mObservable.notifyOnChanged();
        return this;
    }

    public ComponentGroup addComponent(int index, @NonNull ComponentGroup componentGroup) {
        return addComponent(index, (Component) componentGroup);
    }

    public ComponentGroup addAll(@NonNull Collection<? extends Component> components) {
        for (Component comp : components) {
            addComponent(comp);
        }

        return this;
    }

    public ComponentGroup setComponent(int index, @NonNull Component component) {
        if (mComponentIndexMap.containsKey(component)) {
            throw new IllegalArgumentException("Component " + component + " already added.");
        }

        RangedValue<Component> originalComponentRangedValue = mComponentAccordionList.get(index);
        Range oldRange = originalComponentRangedValue.mRange;
        Component oldComponent = originalComponentRangedValue.mValue;
        addComponentAndUpdateIndices(index, component);

        ComponentDataObserver componentDataObserver = new ChildComponentDataObserver(component);
        component.registerComponentDataObserver(componentDataObserver);
        mComponentDataObserverMap.put(component, componentDataObserver);

        int newSize = component.getCountInternal();
        mComponentAccordionList.set(index, component, newSize);
        mComponentIndexMap.put(component, index);

        notifyRangeUpdated(oldRange, newSize);
        cleanupComponent(oldComponent);
        mObservable.notifyOnChanged();
        return this;
    }

    public ComponentGroup setComponent(int index, @NonNull ComponentGroup componentGroup) {
        return setComponent(index, (Component) componentGroup);
    }

    @NonNull
    public Component remove(int index) {
        Component component = get(index);
        remove(index, component);
        mObservable.notifyOnChanged();
        return component;
    }

    public boolean remove(@NonNull Component component) {
        return contains(component) && remove(indexOf(component), component);
    }

    public void clear() {
        mComponentAccordionList.clear();
        for (Component component : new ArrayList<>(mComponentIndexMap.keySet())) {
            cleanupComponent(component);
        }
        notifyDataChanged();
        mObservable.notifyOnChanged();
    }

    @NonNull
    @SuppressWarnings("unchecked") // Unchecked Component generics.
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getHolderTypeInternal(position - compPair.mRange.mLower);
    }

    @Override
    public Object getPresenter(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getPresenterInternal(position - compPair.mRange.mLower);
    }

    @Override
    public int getCount() {
        return mComponentAccordionList.span().mUpper;
    }

    /** @inheritDoc */
    @Override
    @CallSuper
    public void onItemVisible(int index) {
        super.onItemVisible(index);
        notifyVisibilityChange(index, true);
    }

    /** @inheritDoc */
    @Override
    public void onItemNotVisible(int index) {
        super.onItemNotVisible(index);
        notifyVisibilityChange(index, false);
    }

    public void registerComponentGroupObserver(ComponentGroupDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterComponentGroupObserver(ComponentGroupDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    @Override
    public Object getItem(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getItemInternal(position - compPair.mRange.mLower);
    }

    @Override
    public final int getNumberLanes() {
        int[] childLanes = new int[mComponentAccordionList.size()];
        for (int i = 0; i < mComponentAccordionList.size(); i ++) {
            childLanes[i] = mComponentAccordionList.get(i).mValue.getNumberLanes();
            if (childLanes[i] < 1) {
                throw new IllegalStateException("A component returned a number of lanes less than one. All components must have at least one lane. " + mComponentAccordionList.get(i).mValue.toString());
            }
        }
        return MathUtils.lcm(childLanes);
    }

    /**
     * At a given position, we want to determine the number of lanes the component it is in has.
     * @param position The position to lookup.
     * @return The number of lanes the owner of the position has.
     */
    @Override
    public final int getNumberLanesAtPosition(int position) {
        Component componentAtPos = componentAt(position);
        return componentAtPos.getNumberLanesAtPosition(position - rangeOf(componentAtPos).mLower);
    }

    @Override
    public SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    /**
     * Finds the offset of the specified component if it belongs in this ComponentGroup's hierarchy.
     * That is, we will perform a depth-first search through all Components contained in this group
     * and return the offset of the requested Component. Offset here refers to the number of items
     * declared by all Components appearing before the specified Component. If this group is the
     * root, then this value can directly be used as the index of the first view of the Component in
     * an adapter.
     *
     * @param component the component to search for
     * @return the offset of the component, or -1 if the component does not belong in this group or
     *     any of its children.
     */
    public int findComponentOffset(@NonNull Component component) {
        int offset = 0;
        if (component == this) {
            return 0;
        }

        for (int i = 0; i < getSize(); i++) {
            Component candidate = get(i);
            if (candidate == component) {
                return offset;
            }
            if (candidate instanceof ComponentGroup) {
                int maybeIndex = ((ComponentGroup) candidate).findComponentOffset(component);
                if (maybeIndex != -1) {
                    return offset + maybeIndex;
                }
            }
            offset = rangeOf(candidate).mUpper;
        }
        return -1;
    }

    /**
     * Called when the first visible item changes as a result of scrolling.
     *
     * @param i The position of the new first item visible.
     */
    /* package */ void notifyFirstItemVisibilityChanged(int i) {
        if (hasGap(i)) {
            return;
        }

        Component component = componentAt(i - getPositionOffset());
        int index = i - rangeOf(component).mLower - getPositionOffset();

        if (component.hasGap(index)) {
            return;
        }

        component.onItemAtTop(index);
    }

    /**
     * Finds the component which has the view at the specified index and notifies it that the view
     * is now either visible or not.
     *
     * <p>NOTE: this is notifying the view is visible on screen, not that its Visibility property is
     * set to VISIBLE.
     *
     * @param i the index of the view in the adapter whose visibility has changed.
     * @param visible whether the view is now visible or not
     */
    /* package */ void notifyVisibilityChange(int i, boolean visible) {

        // We explicitly don't notify visibility of gaps.
        if (hasGap(i)) {
            return;
        }

        Component component = componentAt(i - getPositionOffset());
        int index = i - rangeOf(component).mLower - getPositionOffset();

        if (component.hasGap(index)) {
            return;
        }

        if (visible) {
            component.onItemVisible(index - component.getPositionOffset());
        } else {
            component.onItemNotVisible(index - component.getPositionOffset());
        }
    }

    /**
     *
     *
     * <pre>
     * Alright this is unintuitive, but since Bento doesn't implement proper
     * diffing (https://developer.android.com/reference/android/support/v7/util/DiffUtil.html)
     * we notify that all items in the existing list have been changed and that the
     * size of the list has changed. We notify the size change by saying the last x element have
     * been added or deleted.
     *
     * If we had [a, b, c]
     *
     * and we removed b
     *
     * we would notifyItemRangeChanged(0, 2)
     *          notifyItemRangeRemoved(2, 1)
     *
     * instead of the expected notifyItemRangeChanged(0, 2)
     *                         notifyItemRangeRemoved(1, 1)
     *
     * even though the b was removed and not the c. This works fine in terms of correctness. The
     * RecyclerView will refresh the right items on the screen. However, this does cause Bento
     * to do change animations instead of removal animations.
     * </pre>
     */
    private void notifyRangeUpdated(Range originalRange, int newSize) {
        int oldSize = originalRange.getSize();
        int sizeChange = newSize - oldSize;
        if (sizeChange == 0) {
            notifyItemRangeChanged(originalRange.mLower, newSize);
        } else if (sizeChange > 0) {
            notifyItemRangeChanged(originalRange.mLower, oldSize);
            notifyItemRangeInserted(originalRange.mLower + oldSize, sizeChange);
        } else if (sizeChange < 0) {
            notifyItemRangeChanged(originalRange.mLower, newSize);
            notifyItemRangeRemoved(originalRange.mLower + newSize, Math.abs(sizeChange));
        }
    }

    private void addComponentAndUpdateIndices(int index, @NonNull Component component) {
        // Add and update indices
        mComponentAccordionList.add(index, component, component.getCountInternal());
        mComponentIndexMap.put(component, index);
        for (int i = index + 1; i < mComponentAccordionList.size(); i++) {
            mComponentIndexMap.put(mComponentAccordionList.get(i).mValue, i);
        }
    }

    private boolean remove(int index, Component component) {
        Range range = mComponentAccordionList.get(index).mRange;
        mComponentAccordionList.remove(index);
        notifyItemRangeRemoved(range.mLower, range.getSize());
        if (component != null) {
            cleanupComponent(component);
        }
        return component != null;
    }

    private void cleanupComponent(Component component) {
        component.unregisterComponentDataObserver(mComponentDataObserverMap.get(component));
        mComponentDataObserverMap.remove(component);

        int removalIndex = mComponentIndexMap.remove(component);
        for (Entry<Component, Integer> entry : mComponentIndexMap.entrySet()) {
            if (entry.getValue() > removalIndex) {
                entry.setValue(entry.getValue() - 1);
            }
        }

        mObservable.notifyOnComponentRemoved(component);
    }

    private class ChildComponentDataObserver implements ComponentDataObserver {

        private final Component mComponent;

        private ChildComponentDataObserver(Component component) {
            mComponent = component;
        }

        @Override
        public void onChanged() {
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;
            int newSize = mComponent.getCountInternal();
            mComponentAccordionList.set(listPosition, mComponent, newSize);

            notifyRangeUpdated(originalRange, newSize);
            mObservable.notifyOnChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;

            notifyItemRangeChanged(originalRange.mLower + positionStart, itemCount);
            mObservable.notifyOnChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;
            mComponentAccordionList.set(
                    listPosition,
                    mComponentAccordionList.get(listPosition).mValue,
                    originalRange.getSize() + itemCount);

            notifyItemRangeInserted(originalRange.mLower + positionStart, itemCount);
            mObservable.notifyOnChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;
            mComponentAccordionList.set(
                    listPosition,
                    mComponentAccordionList.get(listPosition).mValue,
                    originalRange.getSize() - itemCount);

            notifyItemRangeRemoved(originalRange.mLower + positionStart, itemCount);
            mObservable.notifyOnChanged();
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;

            notifyItemMoved(originalRange.mLower + fromPosition, originalRange.mLower + toPosition);
            mObservable.notifyOnChanged();
        }
    }

    private static class ComponentGroupObservable extends Observable<ComponentGroupDataObserver> {

        void notifyOnChanged() {
            // Iterate in reverse to avoid problems if an observer detaches itself when onChanged()
            // is called.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }

        void notifyOnComponentRemoved(Component component) {
            // Iterate in reverse to avoid problems if an observer detaches itself when onChanged()
            // is called.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onComponentRemoved(component);
            }
        }
    }

    public interface ComponentGroupDataObserver {

        /**
         * Called whenever there have been changes that affect the children of the {@link
         * ComponentGroup} and after the changes have been propagated.
         */
        void onChanged();

        /** Called whenever a {@link Component} is removed. */
        void onComponentRemoved(Component component);
    }
}
