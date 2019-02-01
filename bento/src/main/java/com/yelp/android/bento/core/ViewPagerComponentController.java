package com.yelp.android.bento.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.core.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.utils.AccordionList.Range;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Component controller that can be used at the top-level for adding components to a view pager. */
public class ViewPagerComponentController extends PagerAdapter implements ComponentController {

    private Map<Component, ComponentController> mComponentPageMap;
    private Map<Component, View> mComponentViewMap;

    private ComponentGroup mComponentGroup;
    private ViewPager mViewPager;

    public ViewPagerComponentController() {
        setComponentGroup(new ComponentGroup());
        mComponentViewMap = new HashMap<>();
        mComponentPageMap = new HashMap<>();
        mViewPager = null;
    }

    /**
     * Allows us to mock the component controller for tests. Because the component controller is
     * also a PagerAdapter, tests outside of androidTest need to have certain methods mocked.
     */
    public static ViewPagerComponentController create() {
        return new ViewPagerComponentController();
    }

    /** The view for this ViewPagerComponentController should only be set once. */
    public void setViewPager(@NonNull ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.setAdapter(this);
    }

    @Override
    public int getCount() {
        return mComponentGroup.getSize();
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

    @Override
    public ComponentController addComponent(@NonNull Component component) {
        mComponentGroup.addComponent(component);
        return this;
    }

    @Override
    public ComponentController addComponent(@NonNull ComponentGroup componentGroup) {
        mComponentGroup.addComponent(componentGroup);
        return this;
    }

    @Override
    public ComponentController addComponent(int index, @NonNull Component component) {
        mComponentGroup.addComponent(index, component);
        return this;
    }

    @Override
    public ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup) {
        mComponentGroup.addComponent(index, componentGroup);
        return this;
    }

    @Override
    public ComponentController addAll(@NonNull Collection<? extends Component> components) {
        mComponentGroup.addAll(components);
        return this;
    }

    @Override
    public ComponentController setComponent(int index, @NonNull Component component) {
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
    }

    @Override
    public void scrollToComponent(@NonNull Component component, boolean smoothScroll) {
        // We need to figure out which page the component belongs to. A component may be a page or
        // within a page, so we iterate through all the pages and check if the component exists on
        // that page.
        for (int i = 0; i < getCount(); i++) {
            Component page = get(i);
            if (page == component
                    || (page instanceof ComponentGroup
                            && ((ComponentGroup) page).findComponentOffset(component) != -1)) {
                mViewPager.setCurrentItem(i, smoothScroll);
            }
        }
    }

    /** Not supported. Use {@link #scrollToComponent(Component, boolean)} instead. */
    @Override
    public void scrollToComponentWithOffset(@NonNull Component component, int offset) {
        throw new UnsupportedOperationException(
                "Scrolling with offset is not supported for "
                        + "ViewPagerComponent. Use scrollToComponent(Component, boolean) instead.");
    }

    @Override
    public int getItemPosition(Object object) {
        if (!contains((Component) object)) {
            return POSITION_NONE;
        } else {
            return indexOf((Component)object);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object component) {
        return view == mComponentViewMap.get(component);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Component component = mComponentGroup.get(position);

        ComponentController componentController;

        if (mComponentPageMap.get(component) == null) {
            RecyclerView view = new RecyclerView(container.getContext());
            container.addView(view);
            componentController = new RecyclerViewComponentController(view);
            mComponentPageMap.put(component, componentController);
            mComponentViewMap.put(component, view);
        } else {
            componentController = mComponentPageMap.get(component);
        }

        if (!componentController.contains(component)) {
            componentController.addComponent(component);
        }

        return component;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object component) {
        if (mComponentViewMap.get(component) == null) {
            // No matches for this component.
            return;
        }
        container.removeView(mComponentViewMap.get(component));
        mComponentViewMap.remove(component);
        mComponentPageMap.remove(component);
    }

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
