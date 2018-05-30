package com.yelp.android.bento.core;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.yelp.android.bento.R;


class GapViewHolder extends ComponentViewHolder<Void, Integer>  {

    private View mItemView;

    @NonNull
    @Override
    public View inflate(@NonNull ViewGroup parent) {
        mItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gap_view, parent, false)
                .findViewById(R.id.gap);
        return mItemView;
    }

    @Override
    public void bind(Void presenter, Integer gapSize) {
        mItemView.setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, gapSize));
    }
}
