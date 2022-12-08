package com.yelp.android.bento.componentcontrollers;

import static com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController.constructViewHolder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.common.collect.HashBiMap;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentGroup;
import com.yelp.android.bento.core.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.core.ComponentViewHolder;
import com.yelp.android.bento.core.ViewHolderWrapper;
import com.yelp.android.bento.utils.AccordionList.Range;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Component controller that can be used at the top-level for adding components to a view pager2.
 */
public class ViewPager2ComponentController extends RecyclerView.Adapter<ViewHolderWrapper> implements ComponentController {

    private final HashBiMap<Class<? extends ComponentViewHolder>, Integer> mViewTypeMap;
    private final Map<Component, Set<Class<? extends ComponentViewHolder>>>
            mComponentViewHolderSetMap;
    private final Map<Class<? extends ComponentViewHolder>, Integer> mViewTypeReferenceCounts;
    private ComponentGroup mComponentGroup;
    private final ViewPager2 mViewPager;

    public ViewPager2ComponentController(@NonNull ViewPager2 viewPager2) {
        setComponentGroup(new ComponentGroup());
        mComponentViewHolderSetMap = new HashMap<>();
        mViewTypeReferenceCounts = new HashMap<>();
        mViewTypeMap = HashBiMap.create();
        mViewPager = viewPager2;
        mViewPager.setAdapter(this);
    }

    @NonNull
    @Override
    public ViewHolderWrapper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ComponentViewHolder viewHolder =
                constructViewHolder(mViewTypeMap.inverse().get(viewType));
        return new ViewHolderWrapper(viewHolder.inflate(parent), viewHolder);
    }

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
    public int indexOf(@NonNull Component component) {
        return mComponentGroup.indexOf(component);
    }

    @Nullable
    @Override
    public Range rangeOf(@NonNull Component component) {
        return mComponentGroup.rangeOf(component);
    }

    @NonNull
    @Override
    public ComponentController addComponent(@NonNull Component component) {
        mComponentGroup.addComponent(component);
        return this;
    }

    @NonNull
    @Override
    public ComponentController addComponent(@NonNull ComponentGroup componentGroup) {
        mComponentGroup.addComponent(componentGroup);
        return this;
    }

    @NonNull
    @Override
    public ComponentController addComponent(int index, @NonNull Component component) {
        mComponentGroup.addComponent(index, component);
        return this;
    }

    @NonNull
    @Override
    public ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup) {
        mComponentGroup.addComponent(index, componentGroup);
        return this;
    }

    @NonNull
    @Override
    public ComponentController addAll(@NonNull Collection<? extends Component> components) {
        mComponentGroup.addAll(components);
        return this;
    }

    @NonNull
    @Override
    public ComponentController replaceComponent(int index, @NonNull Component component) {
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
    }

    @Override
    public void scrollToComponent(@NonNull Component component, boolean smoothScroll) {
        // We need to figure out which page the component belongs to. A component may be a page or
        // within a page, so we iterate through all the pages and check if the component exists on
        // that page.
        for (int i = 0; i < getSize(); i++) {
            Component page = get(i);
            if (page == component
                    || (page instanceof ComponentGroup
                    && ((ComponentGroup) page).findComponentOffset(component) != -1)) {
                mViewPager.setCurrentItem(i, smoothScroll);
            }
        }
    }

    /**
     * Not supported. Use {@link #scrollToComponent(Component, boolean)} instead.
     */
    @Override
    public void scrollToComponentWithOffset(@NonNull Component component, int offset) {
        throw new UnsupportedOperationException(
                "Scrolling with offset is not supported for "
                        + "ViewPagerComponent. Use scrollToComponent(Component, boolean) instead.");
    }

    @Override
    public int getItemViewType(int position) {
        return getViewTypeFromComponent(position);
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

    @Override
    public boolean isScrollable() {
        if (mViewPager != null) {
            return mViewPager.isVerticalScrollBarEnabled()
                    || mViewPager.isHorizontalScrollBarEnabled();
        } else {
            return false;
        }
    }

    /**
     * The scrolling capacity of a {@link ViewPager} depends on the ViewPager itself. To make a
     * ViewPager non scrollable, you must subclass the ViewPager itself.
     *
     * @param isScrollable Ignored by Bento.
     */
    @Override
    public void setScrollable(boolean isScrollable) {
    } // Do nothing.

    @VisibleForTesting
    public void setComponentGroup(ComponentGroup componentGroup) {
        mComponentGroup = componentGroup;
        mComponentGroup.registerComponentDataObserver(
                new ComponentDataObserver() {
                    @Override
                    public void onChanged() {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onItemMoved(int fromPosition, int toPosition) {
                        notifyDataSetChanged();
                    }
                });
        mComponentGroup.registerComponentGroupObserver(
                new ComponentGroupDataObserver() {
                    @Override
                    public void onChanged() {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onComponentRemoved(Component component) {
                        notifyDataSetChanged();
                    }
                });
    }
}
