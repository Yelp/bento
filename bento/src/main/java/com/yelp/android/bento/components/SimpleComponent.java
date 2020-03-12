package com.yelp.android.bento.components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentViewHolder;

/**
 * Component implementation that provides the bare minimum to be used with {@link
 * ComponentController}, it only has 1 View.
 */
public class SimpleComponent<P> extends Component {

    private Class<? extends ComponentViewHolder> mViewHolderType;
    private P mPresenter;

    public SimpleComponent(@NonNull Class<? extends ComponentViewHolder> viewHolderType) {
        this(null, viewHolderType);
    }

    /**
     * Constructor that allows a Presenter to be passed in if the View needs to respond to certain
     * behaviour.
     */
    public SimpleComponent(
            @Nullable P presenter, @NonNull Class<? extends ComponentViewHolder> viewHolderType) {
        mPresenter = presenter;
        mViewHolderType = viewHolderType;
    }

    @Nullable
    @Override
    public Object getPresenter(int position) {
        return mPresenter;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @NonNull
    @Override
    public Class<? extends ComponentViewHolder> getHolderType(int position) {
        return mViewHolderType;
    }

    @Override
    @Nullable
    public Object getItem(int position) {
        return null;
    }
}
