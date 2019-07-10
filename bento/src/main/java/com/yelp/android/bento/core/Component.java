package com.yelp.android.bento.core;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import com.yelp.android.bento.utils.Observable;

/**
 * The building block of user interfaces in the Bento framework. Represents a self-contained
 * component to be used with a {@link ComponentController}.
 *
 * <p> A component is made up of a series of internal items that are rendered to the screen as
 * views. The number of internal items is specified by the {@link #getCount()} method. Typically,
 * unless a component repeats an identical view many times in a row or has multiple lanes, the
 * number of internal items in a component should be limited to just one. This makes it easier to
 * reason about a user interface as a series of modular components instead of a complicated set of
 * internal items whose state must be managed by the component.
 */
public abstract class Component {

    private final ComponentDataObservable mObservable = new ComponentDataObservable();

    @Px
    private int mStartGapSize = 0;

    @Px
    private int mEndGapSize = 0;

    /**
     * Gets the object that is the brains of the internal item at the specified position. The
     * presenter will be passed in to the bind method in the
     * {@link ComponentViewHolder#bind(Object, Object)} that handles any logic associated with user
     * interactions in the view.
     *
     * <p> Typically it ends up being the component class itself.
     *
     * @param position The position of the internal item in the component.
     * @return The presenter associated with the internal item at the specified position.
     */
    @Nullable
    public abstract Object getPresenter(int position);

    /**
     * Gets the data item that will be bound to the view at the specified position. The data item
     * (or "element") will be passed in to the bind method in the
     * {@link ComponentViewHolder#bind(Object, Object)}.
     *
     * @param position The position of the internal item in the component.
     * @return The data item associated with the internal item at the specified position.
     */
    @Nullable
    public abstract Object getItem(int position);

    /**
     * The count represents the number of internal items in a component. Each internal item in a
     * component can have a different data item, presenter and view holder type used to create
     * its associated view.
     *
     * <p> Bento uses this count to render a component. It will call {@link #getItem(int)},
     * {@link #getPresenter(int)} and {@link #getHolderType(int)} however many times the count
     * specifies.
     *
     * @return The count of internal items in the component. If zero, then the component will not
     * be rendered. Currently this is the recommended way of hiding components without removing
     * them from the list.
     */
    public abstract int getCount();

    /**
     * Gets the view holder class used to bind the data item and the presenter logic to the view at
     * the specified position. The view class is instantiated and called internally by the Bento
     * framework.
     *
     * @param position The position of the internal item in the component.
     * @return The view holder class associated with the internal item at the specified position.
     */
    @NonNull
    public abstract Class<? extends ComponentViewHolder> getHolderType(int position);

    protected SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            if (hasGap(position)) {
                return getNumberLanes();
            }
            return 1;
        }
    };

    /**
     * Notify observers that the {@link Component} data has changed.
     */
    public final void notifyDataChanged() {
        mObservable.notifyChanged();
    }

    /**
     * Notify observers that a number of internal items in the {@link Component} data has changed.
     */
    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        mObservable.notifyItemRangeChanged(positionStart, itemCount);
    }

    /**
     * Notify observers that an internal item in the {@link Component} data has been inserted.
     */
    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        mObservable.notifyItemRangeInserted(positionStart, itemCount);
    }

    /**
     * Notify observers that an internal item in the {@link Component} data has been removed.
     */
    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        mObservable.notifyItemRangeRemoved(positionStart, itemCount);
    }

    /**
     * Notify observers that an internal item in the {@link Component} data has been moved.
     */
    public final void notifyItemMoved(int fromPosition, int toPosition) {
        mObservable.notifyOnItemMoved(fromPosition, toPosition);
    }

    /**
     * @param gapSizePx The size of the gap that comes before the component (either horizontally or
     *                  vertically) measured in pixels.
     */
    public void setStartGap(@Px int gapSizePx) {
        if (gapSizePx < 0) {
            throw new IllegalArgumentException("Gap Size must >= 0");
        }

        mStartGapSize = gapSizePx;
    }

    /**
     * @param gapSizePx The size of the gap that comes after the component (either horizontally or
     *                  vertically) measured in pixels.
     */
    public void setEndGap(@Px int gapSizePx) {
        if (gapSizePx < 0) {
            throw new IllegalArgumentException("Gap Size must >= 0");
        }

        mEndGapSize = gapSizePx;
    }

    /**
     * Registers a {@link ComponentDataObserver} to start observing changes to the internal items in
     * the {@link Component}.
     *
     * @param observer The component data observer that will react to changes to internal items in
     *                 the {@link Component}.
     */
    public void registerComponentDataObserver(@NonNull ComponentDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    /**
     * Un-Registers a {@link ComponentDataObserver} to stop observing changes to the internal items in
     * the {@link Component}.
     *
     * @param observer The component data observer that is currently reacting to changes to
     *                 internal items in the {@link Component} and should stop.
     */
    public void unregisterComponentDataObserver(@NonNull ComponentDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    /**
     * Depending on whether the component is in a vertical or horizontal setting, the number of
     * lanes is analogous to the number of rows or columns the component has. A component that has
     * multiple lanes can display each of its internal items as a view that is a fraction of the
     * width/height of the screen. Useful for creating grid-like components.
     *
     * <p>Override this method to increase the number of lanes in the component.
     *
     * @return The number of lanes the component is divided into.
     */
    public int getNumberLanes() {
        return 1;
    }

    /**
     * Override this method when you want to take an action when a view in this component is (at
     * least partially) visible on the screen.
     * <p><p>
     * See {@link ComponentVisibilityListener} for more info.
     *
     * @param index Index of item that is now visible on screen.
     */
    @CallSuper
    public void onItemVisible(int index) {
    }

    /**
     * Override this method when you want to take an action when a view in this component is no
     * longer visible on the screen.
     * <p><p>
     * See {@link ComponentVisibilityListener} for more info.
     *
     * @param index Index of item that is no longer visible on screen.
     */
    @CallSuper
    public void onItemNotVisible(int index) {
    }

    /**
     * Override this method when you want to take action when a view in this component is now
     * scrolled to the top of the screen.
     *
     * @param index The index of the top visible item.
     */
    @CallSuper
    public void onItemAtTop(int index) {
    }

    /**
     * Similar to {@link #getPresenter(int)} but also accounts for Bento framework items such as
     * gaps.
     *
     * @param position The position to retrieve the presenter at.
     * @return The presenter for the internal item at the specified position, including gap
     * components.
     */
    @Nullable
    final Object getPresenterInternal(int position) {
        if (hasGap(position)) {
            return null;
        }

        return getPresenter(position - getPositionOffset());
    }

    /**
     * Similar to {@link #getHolderType(int)} but also accounts for Bento framework items such as
     * gaps.
     *
     * @param position The position to retrieve the view holder class at.
     * @return The view holder class for the internal item at the specified position, including gap
     * components.
     */
    @NonNull
    public final Class<? extends ComponentViewHolder> getHolderTypeInternal(int position) {
        if (hasGap(position)) {
            return GapViewHolder.class;
        }

        return getHolderType(position - getPositionOffset());
    }

    /**
     * Similar to {@link #getItem(int)} but also accounts for Bento framework items such as
     * gaps.
     *
     * @param position The position to retrieve the data item at.
     * @return The view holder class for the internal item at the specified position, including gap
     * components.
     */
    @Nullable
    protected final Object getItemInternal(int position) {
        if (hasGap(position)) {
            if (position == 0 && mStartGapSize != 0) {
                return mStartGapSize;
            } else if (position == getCountInternal() - 1 && mEndGapSize != 0) {
                return mEndGapSize;
            }
        }

        return getItem(position - getPositionOffset());
    }

    /**
     * @return The span size lookup that manages components with multiple lanes.
     */
    @NonNull
    public SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    /**
     * @param lookup The new lookup that will manage components with multiple lanes
     */
    public void setSpanSizeLookup(@NonNull SpanSizeLookup lookup) {
        mSpanSizeLookup = lookup;
    }

    /**
     * Override this method to handle reordering of items.
     *
     * @param oldIndex The index the item was originally in.
     * @param newIndex The index the item was moved to.
     * @see Component#canPickUpItem(int) (boolean)
     * @see Component#canDropItem(Component, int, int)
     */
    public void onItemsMoved(int oldIndex, int newIndex) {
    }

    /**
     * Checks if an item from one component can be dropped in this component at a given index.
     * @param fromComponent The component that the dragged item is coming from.
     * @param fromIndex The index the item is currently at in the fromComponent.
     * @param toIndex The index where the user is attempting to drop the item in this component.
     * @return true if this component will allow the other component to drop the item at this index.
     */
    public boolean canDropItem(Component fromComponent, int fromIndex, int toIndex) {
        return fromComponent == this;
    }

    public boolean canPickUpItem(int index) {
        return false;
    }

    /**
     * @return The count of all internal items in the component, including Bento framework items
     * like gap items.
     */
    protected final int getCountInternal() {
        int count = 0;
        if (mEndGapSize > 0) {
            count++;
        }
        if (mStartGapSize > 0) {
            count++;
        }
        return count + getCount();
    }

    /**
     * A class responsible for notifying the observers of a component when changes to the
     * component's items have occurred.
     */
    private static class ComponentDataObservable extends Observable<ComponentDataObserver> {

        public void notifyChanged() {
            // Iterate in reverse to avoid problems if an observer detaches itself when onChanged()
            // is called.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeChanged(positionStart, itemCount);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeRemoved(positionStart, itemCount);
            }
        }

        public void notifyOnItemMoved(int fromPosition, int toPosition) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemMoved(fromPosition, toPosition);
            }
        }
    }

    /**
     * An interface for objects that want to register to the changes of the internal items of a
     * {@link Component}. Use this when you want to do something in reaction to internal component
     * changes.
     */
    public interface ComponentDataObserver {

        void onChanged();

        void onItemRangeChanged(int positionStart, int itemCount);

        void onItemRangeInserted(int positionStart, int itemCount);

        void onItemRangeRemoved(int positionStart, int itemCount);

        void onItemMoved(int fromPosition, int toPosition);
    }

    /**
     * @param position The position of the internal item in the component.
     * @return True if the internal item in the component is a gap item.
     */
    protected boolean hasGap(int position) {
        return mStartGapSize > 0 && position == 0
                || mEndGapSize > 0 && position == getCountInternal() - 1;
    }

    /**
     * @return The offset the position needs to be modified by to account for gaps.
     */
    protected int getPositionOffset() {
        return mStartGapSize > 0 ? 1 : 0;
    }
}
