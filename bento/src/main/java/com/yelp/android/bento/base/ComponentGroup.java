package com.yelp.android.bento.base;

import android.support.annotation.NonNull;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentViewHolder;
import com.yelp.android.bento.utils.AccordionList;
import com.yelp.android.bento.utils.AccordionList.Range;
import com.yelp.android.bento.utils.AccordionList.RangedValue;
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
public class ComponentGroup extends Component implements ComponentController {

    private final AccordionList<Component> mComponentAccordionList = new AccordionList<>();
    private final Map<Component, Integer> mComponentIndexMap = new HashMap<>();
    private final Map<Component, ComponentDataObserver> mComponentDataObserverMap = new HashMap<>();

    private final ComponentGroupObservable mObservable = new ComponentGroupObservable();

    @Override
    public int getSpan() {
        return mComponentAccordionList.span().getSize();
    }

    @Override
    public int getSize() {
        return mComponentAccordionList.size();
    }

    @NonNull
    @Override
    public Component get(int index) {
        return mComponentAccordionList.get(index).mValue;
    }

    /** Returns the {@link Component} associated with the range this location belongs to. */
    public Component componentAt(int position) {
        return mComponentAccordionList.valueAt(position);
    }

    @Override
    public boolean contains(Component component) {
        return mComponentIndexMap.containsKey(component);
    }

    @Override
    public int indexOf(Component component) {
        Integer index = mComponentIndexMap.get(component);
        return index == null ? -1 : index;
    }

    @Override
    public Range rangeOf(Component component) {
        Integer index = mComponentIndexMap.get(component);
        return index == null ? null : mComponentAccordionList.get(index).mRange;
    }

    @Override
    public ComponentGroup addComponent(@NonNull Component component) {
        return addComponent(getSize(), component);
    }

    @Override
    public ComponentGroup addComponent(@NonNull ComponentGroup componentGroup) {
        return addComponent(getSize(), componentGroup);
    }

    @Override
    public ComponentGroup addComponent(int index, @NonNull final Component component) {
        if (mComponentIndexMap.containsKey(component)) {
            throw new IllegalArgumentException("Component " + component + " already added.");
        }

        int originalSize = getItemCount();
        addComponentAndUpdateIndices(index, component);

        ComponentDataObserver componentDataObserver = new ChildComponentDataObserver(component);
        component.registerComponentDataObserver(componentDataObserver);
        mComponentDataObserverMap.put(component, componentDataObserver);

        notifyItemRangeInserted(originalSize, component.getItemCount());
        mObservable.notifyOnChanged();
        return this;
    }

    @Override
    public ComponentGroup addComponent(int index, @NonNull ComponentGroup componentGroup) {
        return addComponent(index, (Component) componentGroup);
    }

    @Override
    public ComponentGroup addAll(@NonNull Collection<? extends Component> components) {
        for (Component comp : components) {
            addComponent(comp);
        }

        return this;
    }

    @Override
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

        int newSize = component.getItemCount();
        mComponentAccordionList.set(index, component, newSize);
        mComponentIndexMap.put(component, index);

        notifyRangeUpdated(oldRange, newSize);
        cleanupComponent(oldComponent);
        mObservable.notifyOnChanged();
        return this;
    }

    @Override
    public ComponentGroup setComponent(int index, @NonNull ComponentGroup componentGroup) {
        return setComponent(index, (Component) componentGroup);
    }

    @NonNull
    @Override
    public Component remove(int index) {
        Component component = get(index);
        remove(index, component);
        mObservable.notifyOnChanged();
        return component;
    }

    @Override
    public boolean remove(@NonNull Component component) {
        return contains(component) && remove(indexOf(component), component);
    }

    @Override
    public void clear() {
        mComponentAccordionList.clear();
        for (Component component : new ArrayList<>(mComponentIndexMap.keySet())) {
            cleanupComponent(component);
        }
        notifyDataChanged();
        mObservable.notifyOnChanged();
    }

    @SuppressWarnings("unchecked") // Unchecked Component generics.
    public Class<? extends ComponentViewHolder> getItemHolderType(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getItemHolderType(position - compPair.mRange.mLower);
    }

    @Override
    public Object getPresenter(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getPresenter(position - compPair.mRange.mLower);
    }

    @Override
    public Object getItem(int position) {
        RangedValue<Component> compPair = mComponentAccordionList.rangedValueAt(position);
        Component component = mComponentAccordionList.valueAt(position);
        return component.getItem(position - compPair.mRange.mLower);
    }

    @Override
    public int getItemCount() {
        return mComponentAccordionList.span().mUpper;
    }

    /** @inheritDoc */
    @Override
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
        Component component = componentAt(i);
        int index = i - rangeOf(component).mLower;

        if (visible) {
            component.onItemVisible(index);
        } else {
            component.onItemNotVisible(index);
        }
    }

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
        mComponentAccordionList.add(index, component, component.getItemCount());
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
            int newSize = mComponent.getItemCount();
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
