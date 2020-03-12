package com.yelp.android.bento.components;

import static junit.framework.Assert.assertEquals;

import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class PaginatingListComponentTest {

    private PaginatingListComponent<Void, Object> mPaginatingListComponent;

    @Before
    public void setup() {
        mPaginatingListComponent = new PaginatingListComponent<>(null, null);
    }

    @Test
    public void addSomeItems() {
        addItems(5);

        assertEquals(9, mPaginatingListComponent.getCount()); // 5 items, 4 dividers
    }

    @Test
    public void makesPaginationRequest() {
        addItems(5);

        Observable observable = mPaginatingListComponent.getFurthestObservable();

        mPaginatingListComponent.getItem(6);
        observable.test().assertValueCount(1);
    }

    /**
     * Adds fake items to the list component.
     *
     * @param count The number of items to add.
     */
    private void addItems(int count) {
        List<Object> fakeData = new ArrayList<>(count);

        while (count > 0) {
            count--;
            fakeData.add(new Object());
        }
        mPaginatingListComponent.appendData(fakeData);
    }
}
