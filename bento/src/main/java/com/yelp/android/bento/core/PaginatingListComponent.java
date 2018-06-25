package com.yelp.android.bento.core;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yelp.android.bento.R;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * A {@link ListComponent} that supports pagination by exposing an {@link Observable} reporting the
 * furthest list data item {@link ListComponent#getItem} has been called on as well as adding a
 * toggleable loading footer.
 */
public class PaginatingListComponent<P, T> extends ListComponent<P, T> {

    private final BehaviorSubject<Integer> mFurthestObservable = BehaviorSubject.create();
    private int mFurthestItemDisplayed = -1;
    private boolean mShouldShowFooter = false;
    private Class<? extends LoadingFooterViewHolder> mLoadingFooter =
            DefaultLoadingFooterViewHolder.class;

    public PaginatingListComponent(
            P presenter, Class<? extends ComponentViewHolder<P, T>> listItemViewHolder) {
        super(presenter, listItemViewHolder);
    }

    @Override
    public P getPresenter(int position) {
        return mShouldShowFooter && position == (getCount() - 1)
                ? null
                : super.getPresenter(position);
    }

    @Override
    public Object getItem(int position) {
        return mShouldShowFooter && position == (getCount() - 1) ? null : super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount() + (mShouldShowFooter ? 1 : 0);
    }

    @NonNull
    @Override
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        return mShouldShowFooter && position == (getCount() - 1)
                ? mLoadingFooter
                : super.getHolderType(position);
    }

    @Override
    @CallSuper
    protected void onGetListItem(int position) {
        super.onGetListItem(position);
        if (position > mFurthestItemDisplayed) {
            mFurthestItemDisplayed = position;
            mFurthestObservable.onNext(mFurthestItemDisplayed);
        }
    }

    /**
     * Returns an {@link Observable} that fires every time a list item with a new furthest index is
     * retrieved.
     */
    public Observable<Integer> getFurthestObservable() {
        return mFurthestObservable;
    }

    public void toggleLoadingFooter(boolean shouldShowFooter) {
        boolean oldShouldShowFooter = mShouldShowFooter;
        mShouldShowFooter = shouldShowFooter;
        if (oldShouldShowFooter && !shouldShowFooter) {
            notifyItemRangeRemoved(getCount(), 1);
        } else if (!oldShouldShowFooter && shouldShowFooter) {
            notifyItemRangeInserted(getCount(), 1);
        }
    }

    public void setLoadingFooter(@NonNull Class<? extends LoadingFooterViewHolder> loadingFooter) {
        mLoadingFooter = loadingFooter;
        if (mShouldShowFooter) {
            notifyItemRangeChanged(super.getCount(), 1);
        }
    }

    @SuppressWarnings("WeakerAccess") // Required to be public for instantiation by reflection
    public abstract static class LoadingFooterViewHolder extends ComponentViewHolder {

        @Override
        public final void bind(Object presenter, Object element) {
            // Force do nothing.
        }
    }

    @SuppressWarnings("WeakerAccess") // Required to be public for instantiation by reflection
    public static class DefaultLoadingFooterViewHolder extends LoadingFooterViewHolder {

        @NonNull
        @Override
        public View inflate(@NonNull ViewGroup parent) {
            return LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_footer_default, parent, false);
        }
    }
}
