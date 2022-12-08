package com.yelp.android.bento.core;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Wrapper class for ViewHolders that allows {@link ComponentViewHolder}s to have an empty
 * constructor and perform view inflation post-instantiation. (This is necessary because
 * RecyclerView.ViewHolder forces an already-inflated view to be passed into the constructor).
 *
 * @param <T> The type of data the wrapped {@link ComponentViewHolder} uses.
 */
public class ViewHolderWrapper<P, T> extends RecyclerView.ViewHolder {

    private ComponentViewHolder<P, T> mViewHolder;

    public ViewHolderWrapper(View itemView, ComponentViewHolder<P, T> viewHolder) {
        super(itemView);
        mViewHolder = viewHolder;
    }

    public void bind(P presenter, int position, T element) {
        mViewHolder.setAbsolutePosition(position);
        mViewHolder.bind(presenter, element);
    }

    public void onViewRecycled() {
        mViewHolder.onViewRecycled();
    }

    public void setAbsolutePosition(int currentIndex) {
        mViewHolder.setAbsolutePosition(currentIndex);
    }

    public void onViewAttachedToWindow() {
        mViewHolder.onViewAttachedToWindow();
    }

    public void onViewDetachedFromWindow() {
        mViewHolder.onViewDetachedFromWindow();
    }
}
