package com.yelp.android.bento.core;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import com.yelp.android.bento.utils.Observable;

/**
 * Represents a self-contained component to be used with {@link ComponentController}.
 */
public abstract class Component {

    private final ComponentDataObservable mObservable = new ComponentDataObservable();

    @Px
    private int mStartGapSize = 0;

    @Px
    private int mEndGapSize = 0;

    @Nullable
    public abstract Object getPresenter(int position);

    @Nullable
    public abstract Object getItem(int position);

    public abstract int getCount();

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

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        mObservable.notifyItemRangeChanged(positionStart, itemCount);
    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        mObservable.notifyItemRangeInserted(positionStart, itemCount);
    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        mObservable.notifyItemRangeRemoved(positionStart, itemCount);
    }

    public final void notifyItemMoved(int fromPosition, int toPosition) {
        mObservable.notifyOnItemMoved(fromPosition, toPosition);
    }

    /**
     * @param gapSizePx The size of the gap in pixels.
     */
    public void setStartGap(@Px int gapSizePx) {
        if (gapSizePx < 0) {
            throw new IllegalArgumentException("Gap Size must >= 0");
        }

        mStartGapSize = gapSizePx;
    }

    /**
     * @param gapSizePx The size of the gap in pixels.
     */
    public void setEndGap(@Px int gapSizePx) {
        if (gapSizePx < 0) {
            throw new IllegalArgumentException("Gap Size must >= 0");
        }

        mEndGapSize = gapSizePx;
    }

    public void registerComponentDataObserver(ComponentDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterComponentDataObserver(ComponentDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    /**
     * Depending on whether the component is in a vertical or horizontal setting, the number of
     * lanes is analogous to the number of rows or columns. Override this method to increase the
     * number of lanes in the component.
     */
    public int getNumberLanes() {
        return 1;
    }

    /**
     * Returns the number of lanes at a given position. This will usually return the same as
     * {@link Component#getNumberLanes()}, but {@link ComponentGroup}s will want to return the
     * number of lanes at a given index. For example, if you have a {@link ComponentGroup} with
     * two {@link Component}s with a different number of lanes, this should return the number of
     * lanes in the component that owns the
     */
    public int getNumberLanesAtPosition(int position) {
        return getNumberLanes();
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
    public void onItemAtTop(int index) {}

    @Nullable
    final Object getPresenterInternal(int position) {
        if (hasGap(position)) {
            return null;
        }

        return getPresenter(position - getPositionOffset());
    }

    @NonNull
    final Class<? extends ComponentViewHolder> getHolderTypeInternal(int position) {
        if (hasGap(position)) {
            return GapViewHolder.class;
        }

        return getHolderType(position - getPositionOffset());
    }

    @Nullable
    final Object getItemInternal(int position) {
        if (hasGap(position)) {
            if (position == 0 && mStartGapSize != 0) {
                return mStartGapSize;
            } else if (position == getCountInternal() - 1 && mEndGapSize != 0) {
                return mEndGapSize;
            }
        }

        return getItem(position - getPositionOffset());
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    public void setSpanSizeLookup(SpanSizeLookup lookup) {
        mSpanSizeLookup = lookup;
    }

    final int getCountInternal() {
        int count = 0;
        if (mEndGapSize > 0) {
            count++;
        }
        if (mStartGapSize > 0) {
            count++;
        }
        return count + getCount();
    }

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

    public interface ComponentDataObserver {

        void onChanged();

        void onItemRangeChanged(int positionStart, int itemCount);

        void onItemRangeInserted(int positionStart, int itemCount);

        void onItemRangeRemoved(int positionStart, int itemCount);

        void onItemMoved(int fromPosition, int toPosition);
    }

    boolean hasGap(int position) {
        return mStartGapSize > 0 && position == 0
                || mEndGapSize > 0 && position == getCountInternal() - 1;
    }

    /**
     * @return The offset the position needs to be modified by to account for gaps.
     */
    int getPositionOffset() {
        return mStartGapSize > 0 ? 1 : 0;
    }
}
