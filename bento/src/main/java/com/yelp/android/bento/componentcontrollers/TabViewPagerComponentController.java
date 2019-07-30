package com.yelp.android.bento.componentcontrollers;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TabViewPagerComponentController extends ViewPagerComponentController {
    private List<String> mPageTitles = new ArrayList<>();

    public void setPageTitles(List<String> pageTitles) {
        mPageTitles = pageTitles;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles.get(position);
    }
}
