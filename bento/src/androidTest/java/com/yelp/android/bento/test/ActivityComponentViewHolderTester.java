package com.yelp.android.bento.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.yelp.android.bento.R;
import com.yelp.android.bento.core.ComponentViewHolder;

/**
 * Basic {@link Activity} with a simple {@link LinearLayout} to test {@link ComponentViewHolder}s
 * with.
 */
public class ActivityComponentViewHolderTester extends Activity {

    private FrameLayout mRootView;
    private View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_componentviewholder_tester);
        mRootView = (FrameLayout) findViewById(R.id.root_view);
    }

    /**
     * Inflates, injects, and binds a specified {@link ComponentViewHolder} to the activity.
     */
    @SuppressWarnings("unchecked")
    public <P, T> void inflateAndBindViewHolder(
            Class<? extends ComponentViewHolder<P, T>> viewHolderType, P presenter, T element) {
        // Remove old view if necessary.
        if (mView != null) {
            mRootView.removeView(mView);
        }

        ComponentViewHolder viewHolder = constructViewHolder(viewHolderType);
        mView = viewHolder.inflate(mRootView);
        mRootView.addView(mView);
        viewHolder.bind(presenter, element);
    }

    /**
     * Uses reflections to instantiate a ComponentViewHolder of the specified type. For this reason,
     * all subclasses of ComponentViewHolder must have a no-arg constructor.
     * <br>
     * See: {@link ComponentViewHolder}
     *
     * @throws RuntimeException if the specified view holder type could not be instantiated.
     */
    private <P, T> ComponentViewHolder<P, T> constructViewHolder(
            Class<? extends ComponentViewHolder<P, T>> viewHolderType) {
        try {
            return viewHolderType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate view holder", e);
        }
    }
}
