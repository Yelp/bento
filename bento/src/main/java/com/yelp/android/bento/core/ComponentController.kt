package com.yelp.android.bento.core

import com.yelp.android.bento.utils.AccordionList.Range

/**
 * Special view controller that allows modular composition of complex views via self-contained
 * [Component]s.
 * <br></br><br></br>
 * See: [Component], [ComponentViewHolder]
 */
@Deprecated(
    message = "Bento is deprecated! Please consider using Jetpack Compose https://developer.android.com/jetpack/compose instead.",
)
interface ComponentController {

    /**
     * Returns the total number of items from all [Component]s in this controller.
     */
    val span: Int

    /**
     * Returns the number of [Component]s in this controller.
     */
    val size: Int

    /**
     * Controls the scrollability of the underlying view.
     */
    var isScrollable: Boolean

    /**
     * Returns the [Component] at the specified position in this controller.
     */
    operator fun get(index: Int): Component

    /**
     * Returns whether or not the specified [Component] is in this controller.
     */
    operator fun contains(component: Component): Boolean

    /**
     * Returns the position of the specified [Component] in this controller if it exists,
     * or -1 if it does not.
     */
    fun indexOf(component: Component): Int

    /**
     * Returns the [Range] occupied by the items of the specified  [Component] in this
     * controller if it exists, or null if it does not.
     */
    fun rangeOf(component: Component): Range?

    /**
     * Inserts the specified [Component] to the end of this controller.
     *
     * @param component [Component] to append
     * @return Reference to this controller
     */
    fun addComponent(component: Component): ComponentController

    /**
     * Inserts the specified [ComponentGroup] to the end of this controller.
     *
     * @param componentGroup [ComponentGroup] to append
     * @return Reference to this controller
     */
    fun addComponent(componentGroup: ComponentGroup): ComponentController

    /**
     * Inserts the specified [Component] to the specified position in this controller.
     *
     * @param index Position to insert
     * @param component [Component] to insert
     * @return Reference to this controller
     */
    fun addComponent(index: Int, component: Component): ComponentController

    /**
     * Inserts the specified [ComponentGroup] to the specified position in this controller.
     *
     * @param index Position to insert
     * @param componentGroup [ComponentGroup] to insert
     * @return Reference to this controller
     */
    fun addComponent(index: Int, componentGroup: ComponentGroup): ComponentController

    /**
     * Adds the given [Component]s to the end of the controller.
     *
     * @param components [Component]s to add
     * @return Reference to this controller
     */
    fun addAll(components: Collection<Component>): ComponentController

    /**
     * Replaces the [Component] at the specified position in this controller.
     *
     * @param index Position to update
     * @param component [Component] to insert
     * @return Reference to this controller
     */
    fun replaceComponent(index: Int, component: Component): ComponentController

    /**
     * Replaces the [ComponentGroup] at the specified position in this controller.
     *
     * @param index Position to update
     * @param componentGroup [ComponentGroup] to insert
     * @return Reference to this controller
     */
    fun replaceComponent(index: Int, componentGroup: ComponentGroup): ComponentController

    /**
     * Removes the [Component] at the specified position from this controller.
     *
     * @param index Position to remove
     * @return Reference to this controller
     */
    fun remove(index: Int): Component

    /**
     * Removes the specified [Component] from this controller if it exists.
     *
     * @param component [Component] to remove
     * @return Reference to this controller
     */
    fun remove(component: Component): Boolean

    /**
     * Removes all [Component]s from this controller.
     */
    fun clear()

    /**
     * Scroll the controller until the specified component is at the top of the screen (or as close
     * as possible if the view cannot scroll enough). If the component cannot be found in the
     * controller, this is a no-op.
     *
     * @param component the component to scroll to
     * @param smoothScroll whether to animate the scroll or instantly move to the position
     */
    fun scrollToComponent(component: Component, smoothScroll: Boolean = false)

    /**
     * Scroll the controller until the specified component is at the top of the screen (or as close
     * as possible if the view cannot scroll enough). If the component cannot be found in the
     * controller, this is a no-op.
     *
     * @param component the component to scroll to
     * @param offset The distance (in pixels) from the start edge of the item view when scrolling
     *               is finished.
     */
    fun scrollToComponentWithOffset(component: Component, offset: Int = 0)
}
