package com.yelp.android.bento.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import com.yelp.android.bento.R;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.ComponentViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Component} for displaying homogeneous lists of data all using the same presenter object
 * and {@link ComponentViewHolder} with support for showing dividers.
 *
 * @param <P> Presenter to attach for each list item.
 * @param <T> {@link ComponentViewHolder} type to use for each list item.
 */
public class ListComponent<P, T> extends Component {

    private final List<T> mData = new ArrayList<>();
    private final P mPresenter;
    private final Class<? extends ComponentViewHolder> mListItemViewHolder;
    private boolean mShouldShowDivider = true;
    private Class<? extends DividerViewHolder> mDividerViewHolder = DefaultDividerViewHolder.class;
    private int mNumberLanes;
    private OnItemMovedCallback<T> mOnItemMovedCallback = null;
    private boolean isReorderable = false;

    /**
     * @param presenter The presenter used for {@link ListComponent} interactions.
     * @param listItemViewHolder The view holder used for each item in the list.
     */
    public ListComponent(
            @Nullable P presenter,
            @NonNull Class<? extends ComponentViewHolder<P, T>> listItemViewHolder) {
        this(presenter, listItemViewHolder, 1);
    }

    /**
     * @param presenter The presenter used for {@link ListComponent} interactions.
     * @param listItemViewHolder The view holder used for each item in the list.
     * @param numberLanes The number of cross-axis lanes in the list if we want to make a grid-like
     *                    component.
     */
    public ListComponent(
            @Nullable P presenter,
            @NonNull Class<? extends ComponentViewHolder<P, T>> listItemViewHolder,
            int numberLanes) {
        mPresenter = presenter;
        mListItemViewHolder = listItemViewHolder;
        mNumberLanes = numberLanes;
    }

    @Override
    public int getNumberLanes() {
        return mNumberLanes;
    }

    /**
     * Updates the data items used in the list to create views.
     *
     * @param data The new data list to use.
     */
    public void setData(@NonNull List<T> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataChanged();
    }

    /**
     * Adds more list items to the end of the list by adding more data items.
     *
     * @param data The new data list items to add.
     */
    public void appendData(@NonNull List<T> data) {
        int oldSize = mData.size();
        int sizeChange = data.size();
        mData.addAll(data);
        notifyItemRangeInserted(oldSize, sizeChange);
    }

    /**
     * Removes the provided data items from the list.
     *
     * @param data The data item to remove from the list.
     */
    public void removeData(@NonNull T data) {
        int index = mData.indexOf(data);
        // Check if the object indeed is in the list.
        if (index != -1) {
            mData.remove(index);
            notifyItemRangeRemoved(getRemoveIndexStart(index), getRemoveItemCount());
        }
    }

    /**
     * @param shouldShowDivider True if we want to show a divider between list items.
     */
    public void toggleDivider(boolean shouldShowDivider) {
        mShouldShowDivider = shouldShowDivider;
        notifyDataChanged();
    }

    /** @inheritDoc */
    @Override
    public final void onItemVisible(int index) {
        super.onItemVisible(index);
        if (!mShouldShowDivider) {
            onListItemVisible(index);
        } else if (index % 2 == 0) {
            // Call onListItemVisible() only for the actual items but not for the dividers.
            onListItemVisible(index / 2);
        }
    }

    /**
     * Override this method when you want to take an action when a view in this component is (at
     * least partially) visible on the screen. Contrary to onItemVisible(), this method only
     * pertains to the items and not the dividers.
     * @param index The index of the item in mData. Not including dividers.
     */
    public void onListItemVisible(int index) {
    }

    /** @inheritDoc */
    @Override
    public final void onItemNotVisible(int index) {
        super.onItemNotVisible(index);
        if (!mShouldShowDivider) {
            onListItemNotVisible(index);
        } else if (index % 2 == 0) {
            // Call onListItemNotVisible() only for the actual items but not for the dividers.
            onListItemNotVisible(index / 2);
        }
    }

    /**
     * Override this method when you want to take an action when a view in this component is no
     * longer visible on the screen. Contrary to onItemNotVisible(), this method only pertains to
     * the items and not the dividers.
     * @param index The index of the item in mData. Not including dividers.
     */
    public void onListItemNotVisible(int index) {
    }

    /**
     * @param dividerViewHolder The {@link DividerViewHolder} to use for dividers in the list.
     */
    public void setDividerViewHolder(
            @NonNull Class<? extends DividerViewHolder> dividerViewHolder) {
        mDividerViewHolder = dividerViewHolder;
        notifyDataChanged();
    }

    /**
     * When removing an item, we must also remove the divider if there is one. In general, we remove
     * the divider that is above the item. The exception to this is when we remove the first item as
     * there is no divider above it. In this case, remove the divider below it. Unless, of course,
     * it is the only item in the list. Then just remove the item. This method calculate the
     * starting index based on the item index in the list of data, not including dividers.
     *
     * @param index The index of the item in mData. Not including dividers.
     * @return An int representing the starting point of the data to remove.
     * @see #getRemoveItemCount()
     */
    @VisibleForTesting
    int getRemoveIndexStart(int index) {
        // If there is no divider or it is the first item, just return the index.
        // If there is divider, multiply by 2 to account for all other dividers.
        return mShouldShowDivider && index != 0 ? (index * 2) - 1 : index;
    }

    /**
     * Gets the number of items to remove. See {@link ListComponent#getRemoveIndexStart(int)} for
     * more information.
     *
     * @return An int representing the number of items to delete. Either 1 or 2 if we need to remove
     * a divider.
     */
    @VisibleForTesting
    int getRemoveItemCount() {
        return mShouldShowDivider && mData.size() != 1 ? 2 : 1;
    }

    /**
     * Overridable method that is called each time a list data item is retrieved by {@link
     * #getItem}.
     *
     * @param position Index of the data item.
     */
    @CallSuper
    protected void onGetListItem(int position) {
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        if (mShouldShowDivider) {
            return isListItem(position) ? getListItem(position / 2) : null;
        }

        return getListItem(position);
    }

    @Nullable
    @Override
    public P getPresenter(int position) {
        return mPresenter;
    }

    @Override
    public int getCount() {
        return mShouldShowDivider ? getTotalSizeWithSeparators(mData.size()) : mData.size();
    }

    @NonNull
    @Override
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        return isListItem(position) ? mListItemViewHolder : mDividerViewHolder;
    }

    @NonNull
    @Override
    public SpanSizeLookup getSpanSizeLookup() {
        if (mSpanSizeLookup == null) {
            setSpanSizeLookup(super.getSpanSizeLookup());
        }
        return mSpanSizeLookup;
    }

    /**
     * Sets the {@link SpanSizeLookup} to use when getting the widths of the cells. This method will
     * take padding into account.
     *
     * @param spanSizeLookup The new {@link SpanSizeLookup} to add.
     */
    @Override
    public void setSpanSizeLookup(@NonNull final SpanSizeLookup spanSizeLookup) {
        mSpanSizeLookup = new SpanSizeLookup() {
            // If there is a gap in the list component, we want it to span the entire width.
            // Otherwise, return the requested span size lookup.
            @Override
            public int getSpanSize(int position) {
                if (hasGap(position)) {
                    return getNumberLanes();
                }
                return spanSizeLookup.getSpanSize(position - getPositionOffset());
            }
        };
    }

    @Override
    public void onItemsMoved(int oldIndex, int newIndex) {
        super.onItemsMoved(oldIndex, newIndex);

        mData.add(newIndex, mData.remove(oldIndex));

        if (mOnItemMovedCallback != null) {
            mOnItemMovedCallback.onItemMoved(oldIndex, newIndex, mData);
        }
    }

    public void setOnItemMovedCallback(OnItemMovedCallback<T> callback) {
        mOnItemMovedCallback = callback;
    }

    @NonNull
    private T getListItem(int position) {
        onGetListItem(position);
        return mData.get(position);
    }

    @Override
    public boolean isReorderable() {
        return isReorderable;
    }

    /**
     * Sets whether or not the list is reorderable.
     *
     * @param isReorderable If true, the list can be reordered. Otherwise false.
     * @see Component#isReorderable()
     */
    public void setIsReorderable(boolean isReorderable) {
        this.isReorderable = isReorderable;
    }

    /**
     * @param position The position of the item in the list, including dividers.
     * @return True when the item at the provided position is not a list divider.
     */
    private boolean isListItem(int position) {
        return !mShouldShowDivider || position % 2 == 0;
    }

    /**
     * @param size The number of items in the list excluding dividers.
     * @return The number of items in the list including dividers.
     */
    private int getTotalSizeWithSeparators(int size) {
        return size == 0 ? 0 : size * 2 - 1;
    }

    @SuppressWarnings("WeakerAccess") // Required to be public for instantiation by reflection
    public abstract static class DividerViewHolder extends ComponentViewHolder {

        @Override
        public final void bind(@Nullable Object presenter, @Nullable Object element) {
            // Force do nothing.
        }
    }

    @SuppressWarnings("WeakerAccess") // Required to be public for instantiation by reflection
    public static class DefaultDividerViewHolder extends DividerViewHolder {

        @NonNull
        @Override
        public View inflate(@NonNull ViewGroup parent) {
            return LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bento_list_divider_default, parent, false);
        }
    }
}
