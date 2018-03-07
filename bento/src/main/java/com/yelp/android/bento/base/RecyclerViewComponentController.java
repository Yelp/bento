package com.yelp.android.bento.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;
import com.google.common.collect.HashBiMap;
import com.yelp.android.bento.base.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.base.RecyclerViewComponentController.ViewHolderWrapper;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentViewHolder;
import com.yelp.android.bento.utils.AccordionList.Range;
import com.yelp.android.util.MathUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/** Implementation of {@link ComponentController} for {@link RecyclerView}s. */
public class RecyclerViewComponentController extends RecyclerView.Adapter<ViewHolderWrapper>
        implements ComponentController {

    private final ComponentGroup mComponentGroup;
    private final Map<Component, Set<Class<? extends ComponentViewHolder>>>
            mComponentViewHolderSetMap;
    private final Map<Class<? extends ComponentViewHolder>, Integer> mViewTypeReferenceCounts;
    private final AtomicInteger mUniqueViewType;
    private final HashBiMap<Class<? extends ComponentViewHolder>, Integer> mViewTypeMap;
    private final RecyclerView mRecyclerView;
    private ComponentVisibilityListener mComponentVisibilityListener;
    private GridLayoutManager mLayoutManager;
    private int mNumColumns;

    /**
     * Creates a new {@link RecyclerViewComponentController} and automatically attaches itself to
     * the {@link RecyclerView}. In order to make the columns, this component controller will set
     * the {@link RecyclerView}'s {@link android.support.v7.widget.RecyclerView.LayoutManager}. Do
     * not do it manually.
     */
    public RecyclerViewComponentController(RecyclerView recyclerView) {
        mComponentGroup = new ComponentGroup();
        mComponentGroup.registerComponentDataObserver(
                new ComponentDataObserver() {
                    @Override
                    public void onChanged() {
                        notifyDataSetChanged();
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        notifyItemRangeChanged(positionStart, itemCount);
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        notifyItemRangeInserted(positionStart, itemCount);
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        notifyItemRangeRemoved(positionStart, itemCount);
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemMoved(int fromPosition, int toPosition) {
                        notifyItemMoved(fromPosition, toPosition);
                        setupComponentSpans();
                    }
                });
        mComponentGroup.registerComponentGroupObserver(
                new ComponentGroupDataObserver() {
                    @Override
                    public void onChanged() {
                        // Do nothing.
                    }

                    @Override
                    public void onComponentRemoved(Component component) {
                        cleanupComponent(component);
                    }
                });

        mComponentViewHolderSetMap = new HashMap<>();
        mViewTypeReferenceCounts = new HashMap<>();
        mUniqueViewType = new AtomicInteger();
        mViewTypeMap = HashBiMap.create();
        mRecyclerView = recyclerView;

        mRecyclerView.setAdapter(this);
        setupComponentSpans();
        mComponentVisibilityListener = new ComponentVisibilityListener();
        recyclerView.addOnScrollListener(mComponentVisibilityListener);
    }

    @SuppressWarnings("unchecked") // Unchecked Component generics.
    @Override
    public ViewHolderWrapper onCreateViewHolder(ViewGroup parent, int viewType) {
        ComponentViewHolder viewHolder = constructViewHolder(mViewTypeMap.inverse().get(viewType));
        return new ViewHolderWrapper(viewHolder.inflate(parent), viewHolder);
    }

    @SuppressWarnings("unchecked") // Unchecked Component generics.
    @Override
    public void onBindViewHolder(ViewHolderWrapper holder, int position) {
        holder.bind(
                mComponentGroup.getPresenter(position),
                position,
                mComponentGroup.getItem(position));
    }

    @Override
    public int getItemCount() {
        return mComponentGroup.getSpan();
    }

    @Override
    public int getItemViewType(int position) {
        return getViewTypeFromComponent(position);
    }

    @Override
    public int getSpan() {
        return mComponentGroup.getSpan();
    }

    @Override
    public int getSize() {
        return mComponentGroup.getSize();
    }

    @NonNull
    @Override
    public Component get(int index) {
        return mComponentGroup.get(index);
    }

    @Override
    public boolean contains(Component component) {
        return mComponentGroup.contains(component);
    }

    @Override
    public int indexOf(Component component) {
        return mComponentGroup.indexOf(component);
    }

    @Override
    public Range rangeOf(Component component) {
        return mComponentGroup.rangeOf(component);
    }

    @Override
    public RecyclerViewComponentController addComponent(@NonNull Component component) {
        mComponentGroup.addComponent(component);
        mComponentVisibilityListener.onComponentAdded(component);
        return this;
    }

    @Override
    public ComponentController addComponent(@NonNull ComponentGroup componentGroup) {
        mComponentGroup.addComponent(componentGroup);
        mComponentVisibilityListener.onComponentAdded(componentGroup);
        return this;
    }

    @Override
    public RecyclerViewComponentController addComponent(
            int index, @NonNull final Component component) {
        mComponentGroup.addComponent(index, component);
        mComponentVisibilityListener.onComponentAdded(component);
        return this;
    }

    @Override
    public ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup) {
        mComponentGroup.addComponent(index, componentGroup);
        mComponentVisibilityListener.onComponentAdded(componentGroup);
        return this;
    }

    @Override
    public RecyclerViewComponentController addAll(
            @NonNull Collection<? extends Component> components) {
        mComponentGroup.addAll(components);
        for (Component component : components) {
            mComponentVisibilityListener.onComponentAdded(component);
        }
        return this;
    }

    @Override
    public RecyclerViewComponentController setComponent(int index, @NonNull Component component) {
        mComponentGroup.setComponent(index, component);
        return this;
    }

    @Override
    public ComponentController setComponent(int index, @NonNull ComponentGroup componentGroup) {
        mComponentGroup.setComponent(index, componentGroup);
        return this;
    }

    @NonNull
    @Override
    public Component remove(int index) {
        return mComponentGroup.remove(index);
    }

    @Override
    public boolean remove(@NonNull Component component) {
        return mComponentGroup.remove(component);
    }

    @Override
    public void clear() {
        mComponentGroup.clear();
        mRecyclerView.removeOnScrollListener(mComponentVisibilityListener);
        mComponentVisibilityListener = new ComponentVisibilityListener();
        mRecyclerView.addOnScrollListener(mComponentVisibilityListener);
    }

    /**
     * Gets a view type from a {@link Component} for the specified position. This will return a
     * unique integer for each unique view type and the same integer for the same view types.
     */
    @SuppressWarnings("unchecked") // Unchecked Component generics.
    private int getViewTypeFromComponent(int position) {
        Class<? extends ComponentViewHolder> holderType =
                mComponentGroup.getItemHolderType(position);
        Component component = mComponentGroup.componentAt(position);

        if (!mViewTypeMap.containsKey(holderType)) {
            mViewTypeMap.put(holderType, mUniqueViewType.getAndIncrement());
            Set<Class<? extends ComponentViewHolder>> viewHolderSet =
                    mComponentViewHolderSetMap.get(component);
            if (viewHolderSet == null) {
                viewHolderSet = new HashSet<>();
                mComponentViewHolderSetMap.put(component, viewHolderSet);
            }
            viewHolderSet.add(holderType);

            if (!mViewTypeReferenceCounts.containsKey(holderType)) {
                mViewTypeReferenceCounts.put(holderType, 0);
            }
            mViewTypeReferenceCounts.put(holderType, mViewTypeReferenceCounts.get(holderType) + 1);
        }

        return mViewTypeMap.get(holderType);
    }

    private void cleanupComponent(Component component) {
        Set<Class<? extends ComponentViewHolder>> viewHolderSet =
                mComponentViewHolderSetMap.get(component);
        if (viewHolderSet != null) {
            for (Class<? extends ComponentViewHolder> viewHolder : viewHolderSet) {
                int remainingRefs = mViewTypeReferenceCounts.get(viewHolder) - 1;
                if (remainingRefs == 0) {
                    mViewTypeMap.remove(viewHolder);
                    mViewTypeReferenceCounts.remove(viewHolder);
                } else {
                    mViewTypeReferenceCounts.put(viewHolder, remainingRefs);
                }
            }
        }

        mComponentViewHolderSetMap.remove(component);
    }

    /**
     * Uses reflections to instantiate a ComponentViewHolder of the specified type. For this reason,
     * all subclasses of ComponentViewHolder must have a no-arg constructor. <br>
     * See: {@link ComponentViewHolder}
     *
     * @throws RuntimeException if the specified view holder type could not be instantiated.
     */
    private ComponentViewHolder constructViewHolder(
            Class<? extends ComponentViewHolder> viewHolderType) {
        try {
            return viewHolderType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate view holder", e);
        }
    }

    private void setupComponentSpans() {
        // Get an array of all the component's requested columns.
        int[] columns = new int[mComponentGroup.getSize()];
        for (int i = 0; i < mComponentGroup.getSize(); i++) {
            columns[i] = mComponentGroup.get(i).getNumberColumns();
        }

        // The actual number of columns will be the lcm of all the component columns. That way, if
        // one component wants 2 and another 3, the total will be 6.
        int cols = MathUtils.lcm(columns);

        // Setup the layout manager if there is no current layout manager, or the number of columns
        // has changed.
        if (mLayoutManager == null || cols != mNumColumns) {
            mLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), cols);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        mNumColumns = cols;

        mLayoutManager.setSpanSizeLookup(
                new SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {

                        // Get the component at the position and the range of that component.
                        Component component = mComponentGroup.componentAt(position);
                        Range range = mComponentGroup.rangeOf(component);

                        // Should never happen, but AS complains about a possible NPE.
                        if (range == null) {
                            return 1;
                        }
                        // First get the span of the cell based on its position in the component
                        // Then calculate the column width factor based on the number of columns in
                        // the recyclerview. In the 2 and 3 column example, there are 6 total
                        // columns.
                        // The span of a 2 column cell would be 1, but we need to multiply by 6/2=3
                        // to get the true span across the recycler view.
                        return component.getSpanSizeLookup().getSpanSize(position - range.mLower)
                                * (mNumColumns / component.getNumberColumns());
                    }
                });
    }

    /**
     * Wrapper class for ViewHolders that allows {@link ComponentViewHolder}s to have an empty
     * constructor and perform view inflation post-instantiation. (This is necessary because
     * RecyclerView.ViewHolder forces an already-inflated view to be passed into the constructor).
     *
     * @param <T> The type of data the wrapped {@link ComponentViewHolder} uses.
     */
    static class ViewHolderWrapper<P, T> extends RecyclerView.ViewHolder {

        private ComponentViewHolder<P, T> mViewHolder;

        ViewHolderWrapper(View itemView, ComponentViewHolder<P, T> viewHolder) {
            super(itemView);
            mViewHolder = viewHolder;
        }

        void bind(P presenter, int position, T element) {
            mViewHolder.bind(presenter, element);
        }
    }

    /**
     * When added to the recycler view, it will notify all components as long as they are visible on
     * the screen and the recycler view is being scrolled.
     */
    private class ComponentVisibilityListener extends OnScrollListener {

        private int mPreviousFirst = mLayoutManager.findFirstVisibleItemPosition();
        private int mPreviousLast = mLayoutManager.findLastVisibleItemPosition();

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
            int lastVisible = mLayoutManager.findLastVisibleItemPosition();

            // If the user hasn't scrolled far enough to show or hide any views, there's nothing for
            // us to do. Bail out.
            if ((firstVisible == mPreviousFirst && lastVisible == mPreviousLast)
                    || (firstVisible == RecyclerView.NO_POSITION
                            || lastVisible == RecyclerView.NO_POSITION)) {
                return;
            }

            // If we didn't have a first and last then this is the first time we are showing any
            // items and we should notify that they are all visible.
            if (mPreviousFirst == RecyclerView.NO_POSITION
                    && mPreviousLast == RecyclerView.NO_POSITION) {
                for (int i = firstVisible; i <= lastVisible; i++) {
                    mComponentGroup.notifyVisibilityChange(i, true);
                }
            } else {
                // We want to iterate through the entire range of both, views that used to be
                // visible and views that are now visible, so we can notify them of their visibility
                // changes.
                int start = Math.min(mPreviousFirst, firstVisible);
                int end = Math.max(mPreviousLast, lastVisible);

                for (int i = start; i <= end; i++) {

                    if (i < firstVisible || i > lastVisible) {
                        // We are checking for views which are no longer visible. There are 2 cases:
                        // i < firstVisible -> Views that are ABOVE that are no longer visible.
                        // i > lastVisible -> Views that are BELOW that are no longer visible.
                        // If we've removed components, we cannot notify them that their views are
                        // not visible.
                        if (i < mComponentGroup.getSpan()) {
                            mComponentGroup.notifyVisibilityChange(i, false);
                        }
                    } else if (i < mPreviousFirst || i > mPreviousLast) {
                        // We are checking for views which are now visible. There are 2 cases:
                        // i < mPreviousFirst -> We have scrolled UP and new views are visible.
                        // i > mPreviousLast -> We have scrolled DOWN and new views are visible.
                        mComponentGroup.notifyVisibilityChange(i, true);
                    } else {
                        // We are iterating through the views that used to be visible and are still
                        // visible. Since we've already notified the items at these positions, we
                        // can skip them to make the loop (a little) faster,
                        i = Math.min(mPreviousLast, lastVisible);
                    }
                }
            }

            mPreviousFirst = firstVisible;
            mPreviousLast = lastVisible;
        }

        /**
         * Should be called when a component is added to the controller. This will check if that
         * component is immediately visible and notify it.
         *
         * @param addedComponent the component that has been added to this controller
         */
        public void onComponentAdded(@NonNull Component addedComponent) {
            // If we didn't have a first and last then we haven't shown any components yet and we
            // can bail early. Everything will be handled by onScrolled().
            if (mPreviousFirst == RecyclerView.NO_POSITION
                    && mPreviousLast == RecyclerView.NO_POSITION) {
                return;
            }

            int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
            int lastVisible = mLayoutManager.findLastVisibleItemPosition();

            // If we don't have any visible items then there's nothing to notify.
            if (firstVisible == RecyclerView.NO_POSITION
                    || lastVisible == RecyclerView.NO_POSITION) {
                return;
            }

            Range range = mComponentGroup.rangeOf(addedComponent);
            if (range == null) {
                throw new IllegalArgumentException(
                        "Component hasn't been added to this ComponentController");
            }

            // If a component was added within the visible components, we need to notify it.
            // We want to iterate through the items that are the intersection of our visible items
            // and the items within the range of this component.
            // NOTE: Range is inclusive-exclusive so we must subtract 1 from mUpper.
            int start = Math.max(firstVisible, range.mLower);
            int end = Math.min(lastVisible, range.mUpper - 1);

            for (int i = start; i <= end; i++) {
                mComponentGroup.notifyVisibilityChange(i, true);
            }

            mPreviousFirst = firstVisible;
            mPreviousLast = lastVisible;
        }
    }
}
