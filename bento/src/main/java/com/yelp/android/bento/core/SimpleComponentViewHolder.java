package com.yelp.android.bento.core;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yelp.android.bento.core.ComponentViewHolder;

/**
 * Abstract implementation of {@link ComponentViewHolder} that provides a default implementation for
 * very simple components. This class should be extended when the component does not need dynamic
 * data and only has one layout that will ever be inflated.
 * <br><br>
 * See: ContributionsComponent for an example.
 */
public abstract class SimpleComponentViewHolder<P> extends ComponentViewHolder<P, Void> {

    protected View mItemView;
    protected P mPresenter;
    @LayoutRes private int mLayoutId;

    protected abstract void onViewCreated(@NonNull View itemView);

    protected SimpleComponentViewHolder(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    @Override
    public View inflate(ViewGroup parent) {
        mItemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        onViewCreated(mItemView);
        return mItemView;
    }

    @Override
    public void bind(P presenter, Void element) {
        mPresenter = presenter;
    }
}
