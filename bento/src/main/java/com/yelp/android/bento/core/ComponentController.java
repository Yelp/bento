package com.yelp.android.bento.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yelp.android.bento.utils.AccordionList.Range;
import java.util.Collection;

/**
 * Special view controller that allows modular composition of complex views via self-contained
 * {@link Component}s.
 * <br><br>
 * See: {@link Component}, {@link ComponentViewHolder}
 */
public interface ComponentController {

    /**
     * Returns the total number of items from all {@link Component}s in this controller.
     */
    int getSpan();

    /**
     * Returns the number of {@link Component}s in this controller.
     */
    int getSize();

    /**
     * Returns the {@link Component} at the specified position in this controller.
     */
    @NonNull
    Component get(int index);

    /**
     * Returns whether or not the specified {@link Component} is in this controller.
     */
    boolean contains(@NonNull Component component);

    /**
     * Returns the position of the specified {@link Component} in this controller if it exists,
     * or -1 if it does not.
     */
    int indexOf(@NonNull Component component);

    /**
     * Returns the {@link Range} occupied by the items of the specified  {@link Component} in this
     * controller if it exists, or null if it does not.
     */
    @Nullable
    Range rangeOf(@NonNull Component component);

    /**
     * Inserts the specified {@link Component} to the end of this controller.
     *
     * @param component {@link Component} to append
     * @return Reference to this controller
     */
    ComponentController addComponent(@NonNull Component component);

    /**
     * Inserts the specified {@link ComponentGroup} to the end of this controller.
     *
     * @param componentGroup {@link ComponentGroup} to append
     * @return Reference to this controller
     */
    ComponentController addComponent(@NonNull ComponentGroup componentGroup);

    /**
     * Inserts the specified {@link Component} to the specified position in this controller.
     *
     * @param index     Position to insert
     * @param component {@link Component} to insert
     * @return Reference to this controller
     */
    ComponentController addComponent(int index, @NonNull Component component);

    /**
     * Inserts the specified {@link ComponentGroup} to the specified position in this controller.
     *
     * @param index          Position to insert
     * @param componentGroup {@link ComponentGroup} to insert
     * @return Reference to this controller
     */
    ComponentController addComponent(int index, @NonNull ComponentGroup componentGroup);

    /**
     * Adds the given {@link Component}s to the end of the controller.
     *
     * @param components {@link Component}s to add
     * @return Reference to this controller
     */
    ComponentController addAll(@NonNull Collection<? extends Component> components);

    /**
     * Replaces the {@link Component} at the specified position in this controller.
     *
     * @param index     Position to update
     * @param component {@link Component} to insert
     * @return Reference to this controller
     */
    ComponentController setComponent(int index, @NonNull Component component);

    /**
     * Replaces the {@link ComponentGroup} at the specified position in this controller.
     *
     * @param index          Position to update
     * @param componentGroup {@link ComponentGroup} to insert
     * @return Reference to this controller
     */
    ComponentController setComponent(int index, @NonNull ComponentGroup componentGroup);

    /**
     * Removes the {@link Component} at the specified position from this controller.
     *
     * @param index Position to remove
     * @return Reference to this controller
     */
    @NonNull
    Component remove(int index);

    /**
     * Removes the specified {@link Component} from this controller if it exists.
     *
     * @param component {@link Component} to remove
     * @return Reference to this controller
     */
    boolean remove(@NonNull Component component);

    /**
     * Removes all {@link Component}s from this controller.
     */
    void clear();
}
