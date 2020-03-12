package com.yelp.android.bento.test;

import androidx.test.rule.ActivityTestRule;
import com.yelp.android.bento.core.ComponentViewHolder;
import org.junit.Rule;

/**
 * Test cases that allows a {@link ComponentViewHolder} to be tested by inflating and then injecting
 * it into {@link ActivityComponentViewHolderTester}. The {@link ComponentViewHolder} can then be
 * bound with a specified presenter and data object.
 */
public abstract class ComponentViewHolderTestCase<P, T> {

    @Rule
    public ActivityTestRule<ActivityComponentViewHolderTester> mActivityTestRule =
            new ActivityTestRule<>(ActivityComponentViewHolderTester.class);

    private ComponentViewHolder<P, T> holder;

    /**
     * Inflates, injects, and binds a specified {@link ComponentViewHolder} to the tester activity.
     */
    public <ViewHolder extends ComponentViewHolder<P, T>> void bindViewHolder(
            final Class<ViewHolder> viewHolderType, final P presenter, final T element)
            throws Throwable {
        mActivityTestRule.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        holder =
                                mActivityTestRule
                                        .getActivity()
                                        .inflateAndBindViewHolder(
                                                viewHolderType, presenter, element);
                    }
                });
    }

    @SuppressWarnings("Unchecked")
    protected <ViewHolder extends ComponentViewHolder<P, T>> ViewHolder getHolder() {
        return (ViewHolder) holder;
    }
}
