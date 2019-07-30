package com.yelp.android.bento.componentcontrollers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.VisibleForTesting;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.google.android.material.tabs.TabLayout;
import com.yelp.android.bento.R;
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder.TabViewPagerComponentViewHolderData;
import com.yelp.android.bento.components.TabViewPagerComponent.TabViewPagerComponentViewModel;
import com.yelp.android.bento.core.ComponentViewHolder;

public class TabViewPagerComponentViewHolder
        extends ComponentViewHolder<OnPageChangeListener, TabViewPagerComponentViewHolderData> {
    private Context mContext;
    private View mContainer;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @VisibleForTesting
    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    @VisibleForTesting
    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public View inflate(ViewGroup parent) {
        mContext = parent.getContext();
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.component_tab_view_pager, parent, false);
        mContainer = view.findViewById(R.id.tab_view_pager_parent);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(Integer.MAX_VALUE);
        return view;
    }

    @Override
    public void bind(
            OnPageChangeListener onPageChangeListener,
            TabViewPagerComponentViewHolderData element) {
        element.getViewPagerComponentController().setViewPager(mViewPager);

        if (onPageChangeListener != null) {
            mViewPager.removeOnPageChangeListener(onPageChangeListener);
            mViewPager.addOnPageChangeListener(onPageChangeListener);
        }

        if (element.getViewModel().getBackground() != 0) {
            mContainer.setBackgroundResource(element.getViewModel().getBackground());
        }
        if (element.getViewModel().getTabLayoutBackground() != 0) {
            mTabLayout.setBackgroundResource(element.getViewModel().getTabLayoutBackground());
        }
        if (element.getViewModel().getIndicatorColor() != 0) {
            mTabLayout.setSelectedTabIndicatorColor(
                    mContext.getResources().getColor(element.getViewModel().getIndicatorColor()));
        }
        if (element.getViewModel().getTextColorNormal() != 0
                && element.getViewModel().getTextColorSelected() != 0) {
            mTabLayout.setTabTextColors(
                    mContext.getResources().getColor(element.getViewModel().getTextColorNormal()),
                    mContext.getResources()
                            .getColor(element.getViewModel().getTextColorSelected()));
        }
        if (element.getViewModel().getTabLayoutSideMargins() != 0) {
            LayoutParams tabLayoutParams =
                    new LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            tabLayoutParams.setMargins(
                    (int)
                            mContext.getResources()
                                    .getDimension(element.getViewModel().getTabLayoutSideMargins()),
                    0,
                    (int)
                            mContext.getResources()
                                    .getDimension(element.getViewModel().getTabLayoutSideMargins()),
                    0);
            mTabLayout.setLayoutParams(tabLayoutParams);
        }

        mViewPager.setCurrentItem(element.getViewModel().getCurrentIndex());
    }

    public static class TabViewPagerComponentViewHolderData {
        private TabViewPagerComponentViewModel mViewModel;
        private TabViewPagerComponentController mViewPagerComponentController;

        public TabViewPagerComponentViewHolderData(
                TabViewPagerComponentViewModel viewModel,
                TabViewPagerComponentController viewPagerComponentController) {
            mViewModel = viewModel;
            mViewPagerComponentController = viewPagerComponentController;
        }

        public TabViewPagerComponentViewModel getViewModel() {
            return mViewModel;
        }

        public void setViewModel(TabViewPagerComponentViewModel viewModel) {
            mViewModel = viewModel;
        }

        public TabViewPagerComponentController getViewPagerComponentController() {
            return mViewPagerComponentController;
        }
    }
}
