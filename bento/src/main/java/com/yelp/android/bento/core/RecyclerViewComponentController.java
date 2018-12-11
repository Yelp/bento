package com.yelp.android.bento.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.HashBiMap;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.core.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.core.ComponentVisibilityListener.LayoutManagerHelper;
import com.yelp.android.bento.core.RecyclerViewComponentController.ViewHolderWrapper;
import com.yelp.android.bento.utils.AccordionList.Range;

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
    private OnScrollListener mOnScrollListener;
    private GridLayoutManager mLayoutManager;
    private int mNumColumns;
    private LinearSmoothScroller mSmoothScroller;

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
        mSmoothScroller =
                new LinearSmoothScroller(mRecyclerView.getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };

        mRecyclerView.setAdapter(this);
        setupComponentSpans();
        addVisibilityListener();
    }

    @NonNull
    @SuppressWarnings("unchecked") // Unchecked Component generics.
    @Override
    public ViewHolderWrapper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ComponentViewHolder viewHolder = constructViewHolder(mViewTypeMap.inverse().get(viewType));
        return new ViewHolderWrapper(viewHolder.inflate(parent), viewHolder);
    }

    @SuppressWarnings("unchecked") // Unchecked Component generics.
    @Override
    public void onBindViewHolder(@NonNull ViewHolderWrapper holder, int position) {
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
    public void onViewAttachedToWindow(@NonNull ViewHolderWrapper holder) {
        holder.mViewHolder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolderWrapper holder) {
        holder.mViewHolder.onViewDetachedFromWindow();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolderWrapper holder) {
        holder.mViewHolder.onViewRecycled();
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
    public boolean contains(@NonNull Component component) {
        return mComponentGroup.contains(component);
    }

    @Override
    public int indexOf(@NonNull Component component) {
        return mComponentGroup.indexOf(component);
    }

    @Override
    public Range rangeOf(@NonNull Component component) {
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
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mComponentGroup.unregisterComponentDataObserver(mComponentVisibilityListener);
        addVisibilityListener();
    }

    @Override
    public void scrollToComponent(@NonNull Component component, boolean smoothScroll) {
        int componentIndex = mComponentGroup.findComponentOffset(component);
        if (componentIndex != -1) {
            if (smoothScroll) {
                mSmoothScroller.setTargetPosition(componentIndex);
                mLayoutManager.startSmoothScroll(mSmoothScroller);
            } else {
                mLayoutManager.scrollToPositionWithOffset(componentIndex, 0);
            }
        }
    }

    @Override
    public void scrollToComponentWithOffset(@NonNull Component component, int offset) {
        int componentIndex = mComponentGroup.findComponentOffset(component);
        if (componentIndex != -1) {
            mLayoutManager.scrollToPositionWithOffset(componentIndex, offset);
        }
    }

    private void addVisibilityListener() {
        mComponentVisibilityListener =
                new ComponentVisibilityListener(
                        new RecyclerViewLayoutManagerHelper(mLayoutManager), mComponentGroup);
        mOnScrollListener =
                new OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        // Do nothing.
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        mComponentVisibilityListener.onScrolled();
                    }
                };
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mComponentGroup.registerComponentDataObserver(mComponentVisibilityListener);
    }

    /**
     * Gets a view type from a {@link Component} for the specified position. This will return a
     * unique integer for each unique view type and the same integer for the same view types.
     */
    @SuppressWarnings("unchecked") // Unchecked Component generics.
    private int getViewTypeFromComponent(int position) {
        Class<? extends ComponentViewHolder> holderType =
                mComponentGroup.getHolderTypeInternal(position);
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
        int cols = mComponentGroup.getNumberColumns();

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
                                * (mNumColumns / component.getNumberColumnsAtPosition(position - range.mLower));
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

    private static class RecyclerViewLayoutManagerHelper implements LayoutManagerHelper {

        private final GridLayoutManager mLayoutManager;

        private RecyclerViewLayoutManagerHelper(GridLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
        }

        @Override
        public int findFirstVisibleItemPosition() {
            return mLayoutManager.findFirstVisibleItemPosition();
        }

        @Override
        public int findLastVisibleItemPosition() {
            return mLayoutManager.findLastVisibleItemPosition();
        }
    }
}
