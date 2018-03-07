package com.yelp.android.bento.base;

import com.yelp.android.bento.core.Component;
import com.yelp.android.bento.core.ComponentController;
import com.yelp.android.bento.core.ComponentViewHolder;

/**
 * Component implementation that provides the bare minimum to be used with
 * {@link ComponentController}, it only has 1 View.
 */
public class SimpleComponent<P> extends Component {

    private Class<? extends ComponentViewHolder> mViewHolderType;
    private P mPresenter;

    public SimpleComponent(Class<? extends ComponentViewHolder> viewHolderType) {
        this(null, viewHolderType);
    }

    /**
     * Constructor that allows a Presenter to be passed in if the View needs to respond to certain
     * behaviour.
     */
    public SimpleComponent(P presenter, Class<? extends ComponentViewHolder> viewHolderType) {
        mPresenter = presenter;
        mViewHolderType = viewHolderType;
    }

    @Override
    public Object getPresenter(int position) {
        return mPresenter;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public Class<? extends ComponentViewHolder> getItemHolderType(int position) {
        return mViewHolderType;
    }
}
