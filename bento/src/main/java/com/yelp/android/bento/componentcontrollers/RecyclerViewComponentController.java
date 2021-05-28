package com.yelp.android.bento.componentcontrollers;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.Orientation;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.common.collect.HashBiMap;
import com.yelp.android.bento.core.AsyncCompat;
import com.yelp.android.bento.core.BentoLayoutManager;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentControllerX;
import com.yelp.android.bento.core.ComponentGroup;
import com.yelp.android.bento.core.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.core.ComponentViewHolder;
import com.yelp.android.bento.core.ComponentVisibilityListener;
import com.yelp.android.bento.core.ComponentVisibilityListener.LayoutManagerHelper;
import com.yelp.android.bento.core.LayoutPreInflater;
import com.yelp.android.bento.core.ListItemTouchCallback;
import com.yelp.android.bento.core.OnItemMovedPositionListener;
import com.yelp.android.bento.utils.AccordionList.Range;
import com.yelp.android.bento.utils.AccordionList.RangedValue;
import com.yelp.android.bento.utils.Sequenceable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kotlin.sequences.Sequence;

import org.jetbrains.annotations.Nullable;

import static com.yelp.android.bento.core.LayoutPreInflaterKt.LOG_TAG;

/**
 * Implementation of {@link ComponentController} for {@link RecyclerView}s.
 */
public class RecyclerViewComponentController
        implements ComponentController, OnItemMovedPositionListener {

    private final RecyclerView.Adapter<ViewHolderWrapper> mRecyclerViewAdapter;
    private final ComponentGroup mComponentGroup;
    private final Map<Component, Set<Class<? extends ComponentViewHolder>>>
            mComponentViewHolderSetMap;
    private final Map<Class<? extends ComponentViewHolder>, Integer> mViewTypeReferenceCounts;
    private final HashBiMap<Class<? extends ComponentViewHolder>, Integer> mViewTypeMap;
    private final RecyclerView mRecyclerView;
    private final RecyclerView.RecycledViewPool mRecycledViewPool;
    private ComponentVisibilityListener mComponentVisibilityListener;
    private OnScrollListener mOnScrollListener;
    private AdapterDataObserver mAdapterDataObserver;
    private final BentoLayoutManager mLayoutManager;
    private final LinearSmoothScroller mSmoothScroller;
    private final ItemTouchHelper mItemTouchHelper;
    public LayoutPreInflater preInflater;

    @RecyclerView.Orientation
    private int mOrientation;

    /**
     * Creates a new {@link RecyclerViewComponentController} and automatically attaches itself to
     * the {@link RecyclerView}. In order to make the lanes, this component controller will set the
     * {@link RecyclerView}'s {@link RecyclerView.LayoutManager}. Do not do it manually.
     */
    public RecyclerViewComponentController(@NonNull RecyclerView recyclerView) {
        this(recyclerView, RecyclerView.VERTICAL, null);
    }


    /**
     * Creates a new {@link RecyclerViewComponentController} and automatically attaches itself to
     * the {@link RecyclerView} with preinflater.
     */
    public RecyclerViewComponentController(@NonNull RecyclerView recyclerView, LayoutPreInflater preInflater) {
        this(recyclerView, RecyclerView.VERTICAL, preInflater);
    }


    /**
     * Creates a new {@link RecyclerViewComponentController} and automatically attaches itself to
     * the {@link RecyclerView} with preinflater.
     */
    public RecyclerViewComponentController(@NonNull RecyclerView recyclerView, @Orientation int orientation) {
        this(recyclerView, orientation, null);
    }

    /**
     * Creates a new {@link RecyclerViewComponentController} and automatically attaches itself to
     * the {@link RecyclerView}. In order to make the lanes (columns / rows), this component
     * controller will set the {@link RecyclerView}'s {@link RecyclerView.LayoutManager}. Do not do
     * it manually.
     */
    public RecyclerViewComponentController(
            @NonNull RecyclerView recyclerView, @Orientation int orientation,
            LayoutPreInflater preInflater) {
        this.preInflater = preInflater;
        mOrientation = orientation;
        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mComponentGroup = new ComponentGroup();
        mComponentGroup.registerComponentDataObserver(
                new ComponentDataObserver() {
                    @Override
                    public void onChanged() {
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        mRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount);
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        mRecyclerViewAdapter.notifyItemRangeInserted(positionStart, itemCount);
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        mRecyclerViewAdapter.notifyItemRangeRemoved(positionStart, itemCount);
                        setupComponentSpans();
                    }

                    @Override
                    public void onItemMoved(int fromPosition, int toPosition) {
                        mRecyclerViewAdapter.notifyItemMoved(fromPosition, toPosition);
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
        mViewTypeMap = HashBiMap.create();
        mRecyclerView = recyclerView;
        mLayoutManager =
                new BentoLayoutManager(recyclerView.getContext(), mComponentGroup, mOrientation);
        mSmoothScroller =
                new LinearSmoothScroller(mRecyclerView.getContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };

        mItemTouchHelper = new ItemTouchHelper(new ListItemTouchCallback(mComponentGroup, this));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecycledViewPool = mRecyclerView.getRecycledViewPool();
        setupComponentSpans();
        addVisibilityListeners();
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

    @NonNull
    @Override
    public RecyclerViewComponentController addComponent(@NonNull Component component) {
        if (preInflater == null) {
            mComponentGroup.addComponent(component);
            shareViewPool(component);
            mComponentVisibilityListener.onComponentAdded(component);
            return this;
        }
        preInflater.inflateAll(component, () -> {
            mComponentGroup.addComponent(component);
            shareViewPool(component);
            mComponentVisibilityListener.onComponentAdded(component);
            return null;
        });
        return this;
    }

    @NonNull
    @Override
    public ComponentController addComponent(@NonNull ComponentGroup componentGroup) {
        if (preInflater == null) {
            mComponentGroup.addComponent(componentGroup);
            shareViewPool(componentGroup);
            mComponentVisibilityListener.onComponentAdded(componentGroup);
            return this;
        }
        preInflater.inflateAll(componentGroup, () -> {
            mComponentGroup.addComponent(componentGroup);
            shareViewPool(componentGroup);
            mComponentVisibilityListener.onComponentAdded(componentGroup);
            return null;
        });
        return this;
    }

    @NonNull
    @Override
    public RecyclerViewComponentController addComponent(
            int index, @NonNull final Component component) {
        if (preInflater == null) {
            mComponentGroup.addComponent(index, component);
            shareViewPool(component);
            mComponentVisibilityListener.onComponentAdded(component);
            return this;
        }
        preInflater.inflateAll(component, () -> {
            mComponentGroup.addComponent(index, component);
            shareViewPool(component);
            mComponentVisibilityListener.onComponentAdded(component);
            return null;
        });
        return this;
    }

    @NonNull
    @Override
    public ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup) {
        if (preInflater == null) {
            mComponentGroup.addComponent(index, componentGroup);
            shareViewPool(componentGroup);
            mComponentVisibilityListener.onComponentAdded(componentGroup);
            return this;
        }
        preInflater.inflateAll(componentGroup, () -> {
            mComponentGroup.addComponent(index, componentGroup);
            shareViewPool(componentGroup);
            mComponentVisibilityListener.onComponentAdded(componentGroup);
            return null;
        });
        return this;
    }

    @NonNull
    @Override
    public RecyclerViewComponentController addAll(
            @NonNull Collection<? extends Component> components) {
        mComponentGroup.addAll(components);
        for (Component component : components) {
            shareViewPool(component);
            mComponentVisibilityListener.onComponentAdded(component);
        }
        return this;
    }

    @NonNull
    @Override
    public RecyclerViewComponentController replaceComponent(
            int index, @NonNull Component component) {
        mComponentGroup.replaceComponent(index, component);
        return this;
    }

    @NonNull
    @Override
    public ComponentController replaceComponent(int index, @NonNull ComponentGroup componentGroup) {
        mComponentGroup.replaceComponent(index, componentGroup);
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
        removeVisibilityListeners();
        addVisibilityListeners();
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

    @Override
    public void onItemMovedPosition(int fromAbsoluteIndex, int toAbsoluteIndex) {
        RangedValue<Component> componentMoved =
                mComponentGroup.findRangedComponentWithIndex(fromAbsoluteIndex);

        int fromIndex = fromAbsoluteIndex - componentMoved.mRange.mLower;
        int toIndex = toAbsoluteIndex - componentMoved.mRange.mLower;
        componentMoved.mValue.onItemsMoved(fromIndex, toIndex);

        // Bind is not called again, so we need to go through and properly set all the positions.
        int currentIndex =
                Math.max(
                        Math.min(fromAbsoluteIndex, toAbsoluteIndex),
                        mLayoutManager.findFirstVisibleItemPosition());
        int highIndex =
                Math.min(
                        Math.max(fromAbsoluteIndex, toAbsoluteIndex),
                        mLayoutManager.findLastVisibleItemPosition());
        while (currentIndex <= highIndex) {
            ViewHolderWrapper holder =
                    ((ViewHolderWrapper)
                            mRecyclerView.findViewHolderForAdapterPosition(currentIndex));
            if (holder != null) {
                holder.mViewHolder.setAbsolutePosition(currentIndex);
            }
            currentIndex++;
        }
    }

    @Override
    public boolean isScrollable() {
        return mLayoutManager.isScrollEnabled();
    }

    @Override
    public void setScrollable(boolean isScrollable) {
        mLayoutManager.setScrollEnabled(isScrollable);
    }

    public void onRecyclerViewDetachedFromWindow() {
        mComponentVisibilityListener.onComponentGroupVisibilityChanged(false);
    }

    public void onRecyclerViewAttachedToWindow() {
        mComponentVisibilityListener.onComponentGroupVisibilityChanged(true);
    }

    public void onItemPickedUp(ComponentViewHolder viewHolder) {
        ViewHolder holder =
                mRecyclerView.findViewHolderForLayoutPosition(viewHolder.getAbsolutePosition());
        if (holder != null) {
            mItemTouchHelper.startDrag(holder);
        }
    }

    private void addVisibilityListeners() {
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
        mAdapterDataObserver =
                new AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        int firstVisibleItemPosition =
                                mLayoutManager.findFirstVisibleItemPosition();

                        if (firstVisibleItemPosition == positionStart
                                && firstVisibleItemPosition == 0) {
                            mRecyclerView.post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            mLayoutManager.scrollToPosition(0);
                                        }
                                    });
                        }
                    }
                };

        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mComponentGroup.registerComponentDataObserver(mComponentVisibilityListener);
        mRecyclerViewAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    private void removeVisibilityListeners() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mComponentGroup.unregisterComponentDataObserver(mComponentVisibilityListener);
        mRecyclerViewAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
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
            mViewTypeMap.put(holderType, holderType.hashCode());
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

    /**
     * Run after a component is removed from the controller. It cleans up different references to
     * the component in view type maps.
     *
     * @param component The component that was removed.
     */
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
     * Allows you to share the view pool of the RecyclerView this component controller is managing
     * with another component (such as a carousel component).
     *
     * @param component The component that you want to use the RecyclerView's pool.
     */
    private void shareViewPool(@NonNull Component component) {
        if (component instanceof SharesViewPool) {
            ((SharesViewPool) component).sharePool(mRecycledViewPool);
        } else if (component instanceof ComponentGroup) {
            ComponentGroup group = (ComponentGroup) component;
            for (int i = 0; i < group.getSize(); i++) {
                shareViewPool(group.get(i));
            }
        }
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
        mLayoutManager.setSpanCount(mComponentGroup.getNumberLanes());
    }

    /**
     * Rather than allowing the RecyclerViewComponentController to extend the
     * RecyclerView.Adapter<ViewHolderWrapper> and exposing all of its public final methods that we
     * can't control, we use this class to offer our own API for RecyclerView adapter operations. We
     * do this so we can avoid clients calling things such as notifyDataSetChanged() which causes an
     * entire invalidation of the contents of the ComponentController and underlying Adapter.
     * Clients should only use methods that partially invalidate the contents for performance
     * reasons. In the rare case where we do want to invalidate the entire list, this is still
     * possible by passing a start index of 0 and end index the size of the list using the other
     * methods.
     */
    private final class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolderWrapper>
            implements Sequenceable {
        @NonNull
        @SuppressWarnings("unchecked") // Unchecked Component generics.
        @Override
        public ViewHolderWrapper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ComponentViewHolder viewHolder = constructViewHolder(mViewTypeMap.inverse().get(viewType));
            if (preInflater == null) {
                return new ViewHolderWrapper(viewHolder.inflate(parent), viewHolder);
            }
            View view = null;
            if (viewHolder instanceof AsyncCompat) {
                int resId = ((AsyncCompat) viewHolder).getLayoutId();
                view = preInflater.getView(resId);
            }
            if (view == null) {
                if (!(viewHolder instanceof AsyncCompat)) {
                    Log.e(LOG_TAG, "onCreateViewHolder: " + "preInflater view null. Inflating on main thread. Not AsyncCompat.");
                } else {
                    Log.e(LOG_TAG, "onCreateViewHolder: " + "preInflater view null. Inflating on main thread.");
                }
                return new ViewHolderWrapper(viewHolder.inflate(parent), viewHolder);
            }
            Log.e(LOG_TAG, "onCreateViewHolder: " + "Used preinflated view: " + view);
            viewHolder.onViewCreated(view);
            return new ViewHolderWrapper(view, viewHolder);
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

        @Nullable
        @Override
        public Sequence<Object> asItemSequence() {
            return ComponentControllerX.asItemSequence(RecyclerViewComponentController.this);
        }
    }

    /**
     * Wrapper class for ViewHolders that allows {@link ComponentViewHolder}s to have an empty
     * constructor and perform view inflation post-instantiation. (This is necessary because
     * RecyclerView.ViewHolder forces an already-inflated view to be passed into the constructor).
     *
     * @param <T> The type of data the wrapped {@link ComponentViewHolder} uses.
     */
    protected static class ViewHolderWrapper<P, T> extends RecyclerView.ViewHolder {

        private ComponentViewHolder<P, T> mViewHolder;

        ViewHolderWrapper(View itemView, ComponentViewHolder<P, T> viewHolder) {
            super(itemView);
            mViewHolder = viewHolder;
        }

        void bind(P presenter, int position, T element) {
            mViewHolder.setAbsolutePosition(position);
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

    public interface SharesViewPool {
        void sharePool(@NonNull RecyclerView.RecycledViewPool pool);
    }
}
