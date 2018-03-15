package com.yelp.android.bento.core;

import android.view.View;
import android.view.ViewGroup;

/**
 * Represents a view holder to be used with the {@link Component}. This class is
 * responsible for inflating the associated view (when necessary) and populating the views with
 * data. The data will be provided by the adapter and will be of type T.
 *
 * <br><br> <b> NOTE: Subclasses must provide a no-arg constructor and be visible from this package!
 * </b> <br><br>
 *
 * This class will be instantiated by the {@link ComponentController} when needed, by calling the
 * no-arg constructor. Unfortunately, this means all subclasses must be visible from this package
 * and provide a no-arg constructor.
 */
public abstract class ComponentViewHolder<P, T> {

    public ComponentViewHolder() {}

    public abstract View inflate(ViewGroup parent);

    /**
     * Using FindViewById is a heavy and non-performant method and should never be called in the
     * bind method. You should have fields for any views you wish to modify during the bind method
     * and instantiate them during inflation.
     */
    public abstract void bind(P presenter, T element);
}
