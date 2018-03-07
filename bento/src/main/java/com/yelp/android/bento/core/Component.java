package com.yelp.android.bento.core;

import android.support.annotation.CallSuper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import com.yelp.android.bento.utils.Observable;

/** Represents a self-contained component to be used with {@link ComponentController}. */
public abstract class Component {

    private final ComponentDataObservable mObservable = new ComponentDataObservable();

    private int mColumns = 1;

    public abstract Object getPresenter(int position);

    public abstract Object getItem(int position);

    public abstract int getItemCount();

    public abstract Class<? extends ComponentViewHolder> getItemHolderType(int position);

    /** Notify observers that the {@link Component} data has changed. */
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

    public void registerComponentDataObserver(ComponentDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterComponentDataObserver(ComponentDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    public void setNumberColumns(int columns) {
        mColumns = columns;
    }

    public int getNumberColumns() {
        return mColumns;
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        };
    }

    /**
     * Override this method when you want to take an action when a view in this component is (at
     * least partially) visible on the screen.
     *
     * @param index Index of item that is now visible on screen.
     */
    @CallSuper
    public void onItemVisible(int index) {}

    /**
     * Override this method when you want to take an action when a view in this component is no
     * longer visible on the screen.
     *
     * @param index Index of item that is no longer visible on screen.
     */
    @CallSuper
    public void onItemNotVisible(int index) {}

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
}
