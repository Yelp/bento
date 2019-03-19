package com.yelp.android.bento.components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yelp.android.bento.R;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentGroup;
import com.yelp.android.bento.core.ComponentViewHolder;
import com.yelp.android.bento.componentcontrollers.ViewPagerComponentController;
import com.yelp.android.bento.utils.AccordionList.Range;
import java.util.Collection;

/**
 * A component to easily add other components to a view pager. A lot of the functionality is
 * delegated to a private {@link ViewPagerComponentController} that keeps track of the components.
 */
public class ViewPagerComponent extends Component implements ComponentController {

    private ViewPagerComponentController mComponentController;

    public ViewPagerComponent() {
        mComponentController = ViewPagerComponentController.create();
    }

    @Override
    public int getSpan() {
        return mComponentController.getSpan();
    }

    @Override
    public int getSize() {
        return mComponentController.getSize();
    }

    @NonNull
    @Override
    public Component get(int index) {
        return mComponentController.get(index);
    }

    @Override
    public boolean contains(@NonNull Component component) {
        return mComponentController.contains(component);
    }

    @Override
    public int indexOf(@NonNull Component component) {
        return mComponentController.indexOf(component);
    }

    @Nullable
    @Override
    public Range rangeOf(@NonNull Component component) {
        return mComponentController.rangeOf(component);
    }

    @Override
    public ComponentController addComponent(@NonNull Component component) {
        return mComponentController.addComponent(component);
    }

    @Override
    public ComponentController addComponent(@NonNull ComponentGroup componentGroup) {
        return mComponentController.addComponent(componentGroup);
    }

    @Override
    public ComponentController addComponent(int index, @NonNull Component component) {
        return mComponentController.addComponent(index, component);
    }

    @Override
    public ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup) {
        return mComponentController.addComponent(index, componentGroup);
    }

    @Override
    public ComponentController addAll(@NonNull Collection<? extends Component> components) {
        return mComponentController.addAll(components);
    }

    @Override
    public ComponentController setComponent(int index, @NonNull Component component) {
        return mComponentController.setComponent(index, component);
    }

    @Override
    public ComponentController setComponent(int index, @NonNull ComponentGroup componentGroup) {
        return mComponentController.setComponent(index, componentGroup);
    }

    @NonNull
    @Override
    public Component remove(int index) {
        return mComponentController.remove(index);
    }

    @Override
    public boolean remove(@NonNull Component component) {
        return mComponentController.remove(component);
    }

    @Override
    public void clear() {
        mComponentController.clear();
    }

    @Override
    public void scrollToComponent(@NonNull Component component, boolean smoothScroll) {
        mComponentController.scrollToComponent(component, smoothScroll);
    }

    @Override
    public void scrollToComponentWithOffset(@NonNull Component component, int offset) {
        mComponentController.scrollToComponentWithOffset(component, offset);
    }

    @Override
    public Object getItem(int position) {
        return mComponentController;
    }

    @Override
    public Object getPresenter(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @NonNull
    @Override
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        return ViewPagerViewHolder.class;
    }

    /**
     * The ViewPager view holder used to display components that are added.
     * @param <P> The type of the Presenter for each page.
     * @param <T> The data element for each page that extends {@link ViewPagerComponentController}
     */
    public static class ViewPagerViewHolder<P, T extends ViewPagerComponentController>
            extends ComponentViewHolder<P, T> {

        protected ViewPager mViewPager;

        public ViewPagerViewHolder() {}

        @NonNull
        @Override
        public View inflate(@NonNull ViewGroup parent) {
            mViewPager =
                    (ViewPager)
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.bento_horizontal_view_pager, parent, false);
            return mViewPager;
        }

        @Override
        public void bind(P presenter, T componentController) {
            if (mViewPager.getAdapter() == null) {
                componentController.setViewPager(mViewPager);
                componentController.notifyDataSetChanged();
            }
        }
    }
}
