package com.yelp.android.bento.components;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentController;
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder;
import com.yelp.android.bento.componentcontrollers.TabViewPagerComponentViewHolder.TabViewPagerComponentViewHolderData;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.ComponentViewHolder;
import java.util.List;

public class TabViewPagerComponent extends Component implements ViewPager.OnPageChangeListener {

    private final TabViewPagerComponentViewModel mViewModel;
    private final TabViewPagerComponentController mViewPagerComponentController;
    private final TabViewPagerComponentViewHolderData mTabViewPagerComponentViewHolderData;
    private final TabViewPagerOnPageChangeListener mOnPageChangeListener;

    public TabViewPagerComponent(
            @NonNull TabViewPagerComponentViewModel viewModel,
            @Nullable TabViewPagerOnPageChangeListener onPageChangeListener) {
        mViewModel = viewModel;
        mOnPageChangeListener = onPageChangeListener;
        mViewPagerComponentController = new TabViewPagerComponentController();
        mViewPagerComponentController.setPageTitles(mViewModel.getPageTitles());
        mViewPagerComponentController.addAll(mViewModel.getComponentList());
        mTabViewPagerComponentViewHolderData =
                new TabViewPagerComponentViewHolderData(mViewModel, mViewPagerComponentController);
    }

    @Override
    public ViewPager.OnPageChangeListener getPresenter(int position) {
        return this;
    }

    @Override
    public TabViewPagerComponentViewHolderData getItem(int position) {
        return mTabViewPagerComponentViewHolderData;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        return TabViewPagerComponentViewHolder.class;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        mViewModel.setCurrentIndex(position);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    public static class TabViewPagerComponentViewModel {
        private List<String> mPageTitles;
        private List<Component> mComponentList;
        private int mCurrentIndex;

        @DimenRes private int mTabLayoutSideMargins;
        @DrawableRes private int mBackground;
        @DrawableRes private int mTabLayoutBackground;
        @ColorRes private int mTabLayoutTextColorNormal;
        @ColorRes private int mTabLayoutTextColorSelected;
        @ColorRes private int mTabLayoutIndicatorColor;

        public TabViewPagerComponentViewModel(
                @NonNull List<String> pageTitles, @NonNull List<Component> componentsList) {
            if (pageTitles.size() == componentsList.size()) {
                mPageTitles = pageTitles;
                mComponentList = componentsList;
            } else {
                throw new IllegalArgumentException(
                        "Number of titles must match number of components");
            }
        }

        public List<String> getPageTitles() {
            return mPageTitles;
        }

        public List<Component> getComponentList() {
            return mComponentList;
        }

        public int getCurrentIndex() {
            return mCurrentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.mCurrentIndex = currentIndex;
        }

        public int getTabLayoutSideMargins() {
            return mTabLayoutSideMargins;
        }

        public TabViewPagerComponentViewModel setTabLayoutSideMargins(@DimenRes int sideMargins) {
            mTabLayoutSideMargins = sideMargins;
            return this;
        }

        @DrawableRes
        public int getBackground() {
            return mBackground;
        }

        public TabViewPagerComponentViewModel setBackground(@DrawableRes int background) {
            mBackground = background;
            return this;
        }

        @DrawableRes
        public int getTabLayoutBackground() {
            return mTabLayoutBackground;
        }

        public TabViewPagerComponentViewModel setTabLayoutBackground(@DrawableRes int background) {
            mTabLayoutBackground = background;
            return this;
        }

        @ColorRes
        public int getTextColorNormal() {
            return mTabLayoutTextColorNormal;
        }

        public TabViewPagerComponentViewModel setTextColorNormal(
                @ColorRes int tabLayoutTextNormal) {
            mTabLayoutTextColorNormal = tabLayoutTextNormal;
            return this;
        }

        @ColorRes
        public int getTextColorSelected() {
            return mTabLayoutTextColorSelected;
        }

        public TabViewPagerComponentViewModel setTextColorSelected(
                @ColorRes int tabLayoutTextSelected) {
            mTabLayoutTextColorSelected = tabLayoutTextSelected;
            return this;
        }

        @ColorRes
        public int getIndicatorColor() {
            return mTabLayoutIndicatorColor;
        }

        public TabViewPagerComponentViewModel setIndicatorColor(
                @ColorRes int tabLayoutIndicatorColor) {
            mTabLayoutIndicatorColor = tabLayoutIndicatorColor;
            return this;
        }
    }

    public interface TabViewPagerOnPageChangeListener {

        void onPageSelected(int position);
    }
}
