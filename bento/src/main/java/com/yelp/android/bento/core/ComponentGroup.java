package com.yelp.android.bento.core;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * A {@link Component} comprising of zero or more ordered child {@link Component}s. Useful for
 * maintaining a group of related components in close proximity to each other in the {@link
 * ComponentController}.
 */
public class ComponentGroup extends Component {

    /**
     * The list that specifies the ranges that each component in the group occupies in the
     * underlying total order of internal component items.
     */
    private final AccordionList<Component> mComponentAccordionList = new AccordionList<>();

    /** A map from a Component to its Index in the order of the ComponentGroup. */
    private final Map<Component, Integer> mComponentIndexMap = new HashMap<>();

    /** A map from a Component to its corresponding {@link ComponentDataObserver}. */
    private final Map<Component, ComponentDataObserver> mComponentDataObserverMap = new HashMap<>();

    private final ComponentGroupObservable mObservable = new ComponentGroupObservable();
    private final NotifyChecker mNotifyChecker = new NotifyChecker();

    public ComponentGroup() {
        mSpanSizeLookup =
                new SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (hasGap(position)) {
                            return getNumberLanes();
                        }
                        RangedValue<Component> rangedValue =
                                mComponentAccordionList.rangedValueAt(position);
                        return rangedValue
                                .mValue
                                .getSpanSizeLookup()
                                .getSpanSize(position - rangedValue.mRange.mLower);
                    }
                };
    }

    /**
     * @return The total number of internal items across all components in the {@link
     *     ComponentGroup}.
     */
    public int getSpan() {
        return mComponentAccordionList.span().getSize();
    }

    /** @return The total number of components in the {@link ComponentGroup}. */
    public int getSize() {
        return mComponentAccordionList.size();
    }

    /**
     * @param index The index at which to retrieve the component in the {@link ComponentGroup}.
     * @return The component in the {@link ComponentGroup} at the specified index.
     */
    @NonNull
    public Component get(int index) {
        return mComponentAccordionList.get(index).mValue;
    }

    /**
     * @param position The position of the internal components item across all components in the
     *     {@link ComponentGroup}.
     * @return The {@link Component} associated with the range this position belongs to.
     */
    @NonNull
    public Component componentAt(int position) {
        return mComponentAccordionList.valueAt(position);
    }

    /**
     * @param component The {@link Component} to search for in the {@link ComponentGroup}.
     * @return True if the {@link ComponentGroup} contains the provided {@link Component}.
     */
    public boolean contains(@NonNull Component component) {
        return mComponentIndexMap.containsKey(component);
    }

    /**
     * @param component The {@link Component} to search for in the {@link ComponentGroup}.
     * @return The index of the {@link Component} if it is contained in the {@link ComponentGroup}
     *     or -1 otherwise.
     */
    public int indexOf(@NonNull Component component) {
        Integer index = mComponentIndexMap.get(component);
        return index == null ? -1 : index;
    }

    /**
     * @param component The {@link Component} to retrieve the range of in the {@link ComponentGroup}
     * @return The {@link Range} of the internal items associated with the provided {@link
     *     Component}.
     */
    @Nullable
    public Range rangeOf(@NonNull Component component) {
        Integer index = mComponentIndexMap.get(component);
        return index == null ? null : mComponentAccordionList.get(index).mRange;
    }

    /**
     * Adds the provided {@link Component} to the end of the {@link ComponentGroup}.
     *
     * @param component The {@link Component} to add to the {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the {@link Component} was added to.
     */
    @NonNull
    public ComponentGroup addComponent(@NonNull Component component) {
        return addComponent(getSize(), component);
    }

    /**
     * Adds the provided {@link ComponentGroup} to the end of the {@link ComponentGroup}.
     *
     * @param componentGroup The {@link ComponentGroup} to add to this {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the provided {@link ComponentGroup} was added to.
     */
    @NonNull
    public ComponentGroup addComponent(@NonNull ComponentGroup componentGroup) {
        return addComponent(getSize(), componentGroup);
    }

    /**
     * Adds a {@link Component} at the specified index to the {@link ComponentGroup}. Will throw an
     * exception if the {@link ComponentGroup} already contains the provided {@link Component}. Also
     * does the hard work of updating the data structures that track the positions and ranges of
     * components in the {@link ComponentGroup}.
     *
     * @param index The index at which the {@link Component} should be added to the {@link
     *     ComponentGroup}.
     * @param component The {@link Component} to add in the {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the {@link Component} was added to.
     */
    @NonNull
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

    /**
     * Adds a {@link Component} at the specified index to the {@link ComponentGroup}.
     *
     * @param index The index at which the {@link ComponentGroup} should be added to the {@link
     *     ComponentGroup}.
     * @param componentGroup The {@link ComponentGroup} to add in the {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the provided {@link ComponentGroup} was added to.
     */
    @NonNull
    public ComponentGroup addComponent(int index, @NonNull ComponentGroup componentGroup) {
        return addComponent(index, (Component) componentGroup);
    }

    /**
     * Adds all {@link Component}s to the end of the {@link ComponentGroup}.
     *
     * @param components The {@link Component}s to add to the {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the {@link Component}s were added to.
     */
    @NonNull
    public ComponentGroup addAll(@NonNull Collection<? extends Component> components) {
        for (Component comp : components) {
            addComponent(comp);
        }

        return this;
    }

    /**
     * Replaces the old {@link Component} at the specified index in the {@link ComponentGroup} with
     * the newly provided {@link Component}.
     *
     * @param index The index at which the {@link Component} should be replace in the {@link
     *     ComponentGroup}.
     * @param component The new {@link Component} to add to the {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the replacement took place in.
     */
    @NonNull
    public ComponentGroup replaceComponent(int index, @NonNull Component component) {
        if (mComponentIndexMap.containsKey(component)) {
            throw new IllegalArgumentException("Component " + component + " already added.");
        }
        addComponent(index, component);
        remove(mComponentAccordionList.get(index + 1).mValue);
        return this;
    }

    /**
     * Replaces the old {@link Component} at the specified index in the {@link ComponentGroup} with
     * the newly provided {@link ComponentGroup}.
     *
     * @param index The index at which the {@link Component} should be replace in the {@link
     *     ComponentGroup}.
     * @param componentGroup The new {@link ComponentGroup} to add to the {@link ComponentGroup}.
     * @return The {@link ComponentGroup} that the replacement took place in.
     */
    @NonNull
    public ComponentGroup replaceComponent(int index, @NonNull ComponentGroup componentGroup) {
        return replaceComponent(index, (Component) componentGroup);
    }

    /**
     * Removes and returns the {@link Component} at the provided index.
     *
     * @param index The index at which to remove the {@link Component} from the {@link
     *     ComponentGroup}.
     * @return The {@link Component} that was removed from the {@link ComponentGroup}
     */
    @NonNull
    public Component remove(int index) {
        Component component = get(index);
        remove(index, component);
        mObservable.notifyOnChanged();
        return component;
    }

    /**
     * Removes the provided {@link Component} from the {@link ComponentGroup}.
     *
     * @param component The {@link Component} to remove from the {@link ComponentGroup}
     * @return The {@link Component} that was removed from the {@link ComponentGroup}
     */
    public boolean remove(@NonNull Component component) {
        return contains(component) && remove(indexOf(component), component);
    }

    /** Removes all {@link Component}s from the {@link ComponentGroup}. */
    public void clear() {
        mComponentAccordionList.clear();
        for (Component component : new ArrayList<>(mComponentIndexMap.keySet())) {
            cleanupComponent(component);
        }
        notifyDataChanged();
        mObservable.notifyOnChanged();
    }

    /**
     * @param position The position of the internal item in the {@link Component} of this {@link
     *     ComponentGroup}.
     * @return The view holder type for the internal component item at the provided position.
     */
    @NonNull
    @SuppressWarnings("unchecked") // Unchecked Component generics.
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getHolderTypeInternal(position - compPair.mRange.mLower);
    }

    /**
     * @param position The position of the internal item in the {@link Component} of this {@link
     *     ComponentGroup}.
     * @return The view holder type for the internal component item at the provided position.
     */
    @Nullable
    @Override
    public Object getPresenter(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getPresenterInternal(position - compPair.mRange.mLower);
    }

    /** @return The total count for each component in this component group. */
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

    /**
     * Registers a {@link ComponentGroupDataObserver} to start observing changes to the {@link
     * Component}s in the {@link ComponentGroup}.
     *
     * @param observer The component group data observer that will react to changes to {@link
     *     Component}s in the {@link ComponentGroup}.
     */
    public void registerComponentGroupObserver(@NonNull ComponentGroupDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    /**
     * Un-Registers a {@link ComponentGroupDataObserver} in order to stop observing changes to the
     * {@link Component}s in the {@link ComponentGroup}.
     *
     * @param observer The component group data observer that is currently reacting to changes to in
     *     the {@link Component}s of the {@link ComponentGroup} and should stop.
     */
    public void unregisterComponentGroupObserver(@NonNull ComponentGroupDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    /**
     * @param position The position of the internal item in the {@link Component} of the {@link
     *     ComponentGroup}.
     * @return The internal data item at the specified position.
     */
    @Override
    public Object getItem(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        int positionInComponent = position - compPair.mRange.mLower;
        Object itemInternal = component.getItemInternal(positionInComponent);
        mNotifyChecker.save(component, positionInComponent, itemInternal);
        return itemInternal;
    }

    /**
     * @return The total number of lanes this component group is divided into based on the number of
     *     lanes in its child components.
     */
    @Override
    public final int getNumberLanes() {
        int[] childLanes = new int[mComponentAccordionList.size()];
        for (int i = 0; i < mComponentAccordionList.size(); i++) {
            childLanes[i] = mComponentAccordionList.get(i).mValue.getNumberLanes();
            if (childLanes[i] < 1) {
                throw new IllegalStateException(
                        "A component returned a number of lanes less than one. All components must have at least one lane. "
                                + mComponentAccordionList.get(i).mValue.toString());
            }
        }
        return MathUtils.lcm(childLanes);
    }

    @NonNull
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
     * Finds and returns the component at lowest level (leaf) that encompasses the index.
     *
     * @param index The index to search for.
     * @return The lowest component in the tree.
     */
    public Component findComponentWithIndex(int index) {
        return findRangedComponentWithIndex(index).mValue;
    }

    /**
     * Returns both the component and the absolute range within the controller.
     *
     * @param index The index to search for.
     * @return Both a component and an absolute range over the entire controller.
     */
    public RangedValue<Component> findRangedComponentWithIndex(int index) {
        if (hasGap(index)) {
            return new RangedValue<Component>(this, new Range(0, getCount()));
        }

        RangedValue<Component> rangedValue = mComponentAccordionList.rangedValueAt(index);

        if (rangedValue.mValue instanceof ComponentGroup) {
            ComponentGroup group = (ComponentGroup) rangedValue.mValue;
            RangedValue<Component> childRange =
                    group.findRangedComponentWithIndex(index - rangedValue.mRange.mLower);

            return new RangedValue<>(
                    childRange.mValue,
                    new Range(
                            rangedValue.mRange.mLower + childRange.mRange.mLower,
                            rangedValue.mRange.mLower + childRange.mRange.mUpper));
        } else {
            return rangedValue;
        }
    }

    /**
     * Called when the first visible item changes to another item as a result of scrolling.
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
     * <p>
     *
     * <p>NOTE: this is notifying the view is visible on screen, not that its Visibility property is
     * set to VISIBLE.
     *
     * @param i The index of the view in the adapter whose visibility has changed.
     * @param visible Whether the view is now visible or not
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
     * Because Bento doesn't implement proper diffing
     * (https://developer.android.com/reference/android/support/v7/util/DiffUtil.html)
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
    private void notifyRangeUpdated(@NonNull Range originalRange, int newSize) {
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

    /**
     * Adds the provided {@link Component} to the {@link ComponentGroup} at the specified index and
     * does the hard work of updating internal indices we use to order {@link Component}s within the
     * the {@link ComponentGroup}.
     *
     * @param index The index at which to add the {@link Component}.
     * @param component The {@link Component} to add to this {@link ComponentGroup}.
     */
    private void addComponentAndUpdateIndices(int index, @NonNull Component component) {
        // Add and update indices
        mComponentAccordionList.add(index, component, component.getCountInternal());
        mComponentIndexMap.put(component, index);
        for (int i = index + 1; i < mComponentAccordionList.size(); i++) {
            mComponentIndexMap.put(mComponentAccordionList.get(i).mValue, i);
        }
    }

    /**
     * Adds the provided {@link Component} to the {@link ComponentGroup} at the specified index and
     * does the hard work of updating internal indices we use to order {@link Component}s within the
     * the {@link ComponentGroup}.
     *
     * @param index The index of the component to be removed.
     * @param component The component to be removed from this ComponentGroup.
     * @return
     */
    private boolean remove(int index, @Nullable Component component) {
        Range range = mComponentAccordionList.get(index).mRange;
        mComponentAccordionList.remove(index);
        notifyItemRangeRemoved(range.mLower, range.getSize());
        if (component != null) {
            cleanupComponent(component);
        }
        return component != null;
    }

    /**
     * A method to "clean up" after a component has been removed. - Removes all observers from the
     * provided {@link Component}. - Updates the indices of all components in the component index
     * map to reflect that the component is removed. - Notifies the {@link ComponentGroupObservable}
     * that the component is removed.
     *
     * @param component The component that has been removed.
     */
    private void cleanupComponent(@NonNull Component component) {
        component.unregisterComponentDataObserver(mComponentDataObserverMap.get(component));
        mComponentDataObserverMap.remove(component);

        int removalIndex = mComponentIndexMap.remove(component);
        for (Entry<Component, Integer> entry : mComponentIndexMap.entrySet()) {
            if (entry.getValue() > removalIndex) {
                entry.setValue(entry.getValue() - 1);
            }
        }
        mNotifyChecker.remove(component);

        mObservable.notifyOnComponentRemoved(component);
    }

    /**
     * An observer that listens for changes to a Components's internals and then updates the {@link
     * AccordionList} so that we can keep track of the position of each internal item in the
     * ComponentGroup to which the Component belongs.
     */
    private class ChildComponentDataObserver implements ComponentDataObserver {

        private final Component mComponent;

        private ChildComponentDataObserver(@NonNull Component component) {
            mComponent = component;
        }

        @Override
        public void onChanged() {
            mNotifyChecker.onChanged(mComponent);
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;
            int newSize = mComponent.getCountInternal();
            mComponentAccordionList.set(listPosition, mComponent, newSize);

            notifyRangeUpdated(originalRange, newSize);
            mObservable.notifyOnChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mNotifyChecker.onItemRangeChanged(mComponent, positionStart, itemCount);
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;

            notifyItemRangeChanged(originalRange.mLower + positionStart, itemCount);
            mObservable.notifyOnChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mNotifyChecker.onItemRangeInserted(mComponent, positionStart, itemCount);
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
            mNotifyChecker.onItemRangeRemoved(mComponent, positionStart, itemCount);
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
            mNotifyChecker.onItemMoved(mComponent, fromPosition, toPosition);
            int listPosition = mComponentIndexMap.get(mComponent);
            Range originalRange = mComponentAccordionList.get(listPosition).mRange;

            notifyItemMoved(originalRange.mLower + fromPosition, originalRange.mLower + toPosition);
            mObservable.notifyOnChanged();
        }
    }

    /** An observable for clients that want to subscribe to a {@link ComponentGroup}'s changes. */
    private static class ComponentGroupObservable extends Observable<ComponentGroupDataObserver> {

        void notifyOnChanged() {
            // Iterate in reverse to avoid problems if an observer detaches itself when onChanged()
            // is called.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }

        void notifyOnComponentRemoved(@NonNull Component component) {
            // Iterate in reverse to avoid problems if an observer detaches itself when onChanged()
            // is called.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onComponentRemoved(component);
            }
        }
    }

    /** An interface for clients that want to observe a {@link ComponentGroup}'s changes. */
    public interface ComponentGroupDataObserver {
        /**
         * Called whenever there have been changes that affect the children of the ComponentGroup
         * and after the changes have been propagated.
         */
        void onChanged();

        /** Called whenever a {@link Component} is removed. */
        void onComponentRemoved(@NonNull Component component);
    }
}
