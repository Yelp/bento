package com.yelp.android.bento.test;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import com.yelp.android.bento.core.ComponentViewHolder;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Test cases that allows a {@link ComponentViewHolder} to be tested by inflating and then injecting
 * it into {@link ActivityComponentViewHolderTester}. The {@link ComponentViewHolder} can then be
 * bound with a specified presenter and data object.
 */
@RunWith(AndroidJUnit4.class)
public abstract class ComponentViewHolderTestCase<P, T>
        extends ActivityInstrumentationTestCase2<ActivityComponentViewHolderTester> {

    public ComponentViewHolderTestCase() {
        super(ActivityComponentViewHolderTester.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        super.setUp();
    }

    /**
     * Inflates, injects, and binds a specified {@link ComponentViewHolder} to the tester activity.
     */
    public void bindViewHolder(
            final Class<? extends ComponentViewHolder<P, T>> viewHolderType,
            final P presenter,
            final T element) {
        setActivityIntent(
                new Intent(
                        getInstrumentation().getTargetContext(),
                        ActivityComponentViewHolderTester.class));
        getActivity()
                .runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity()
                                        .inflateAndBindViewHolder(
                                                viewHolderType, presenter, element);
                            }
                        });
    }
}
