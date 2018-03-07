package com.yelp.android.bento.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yelp.android.bento.R;
import com.yelp.android.bento.base.ComponentGroup.ComponentGroupDataObserver;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.core.ComponentController;
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
    public int indexOf(Component component) {
        return mComponentGroup.indexOf(component);
    }

    @Nullable
    @Override
    public Range rangeOf(Component component) {
        return mComponentGroup.rangeOf(component);
    }

    @Override
    public ComponentGroup addComponent(Component component) {
        return mComponentGroup.addComponent(component);
    }

    @Override
    public ComponentController addComponent(@NonNull ComponentGroup componentGroup) {
        return mComponentGroup.addComponent(componentGroup);
    }

    @Override
    public ComponentController addComponent(int index, @NonNull Component component) {
        return mComponentGroup.addComponent(index, component);
    }

    @Override
    public ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup) {
        return mComponentGroup.addComponent(index, componentGroup);
    }

    @Override
    public ComponentController addAll(@NonNull Collection<? extends Component> components) {
        return mComponentGroup.addAll(components);
    }

    @Override
    public ComponentController setComponent(int index, @NonNull Component component) {
        return mComponentGroup.setComponent(index, component);
    }

    @Override
    public ComponentController setComponent(int index, @NonNull ComponentGroup componentGroup) {
        return mComponentGroup.setComponent(index, componentGroup);
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
    public int getItemPosition(Object object) {
        if (mComponentViewMap.containsKey(object)) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
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
            RecyclerView view =
                    (RecyclerView)
                            LayoutInflater.from(container.getContext())
                                    .inflate(R.layout.recycler_view, container, false);
            view.setLayoutManager(
                    new LinearLayoutManager(
                            container.getContext(), LinearLayoutManager.VERTICAL, false));
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
