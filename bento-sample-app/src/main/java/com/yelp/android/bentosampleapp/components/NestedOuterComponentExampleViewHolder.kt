package com.yelp.android.bentosampleapp.components

import com.yelp.android.bento.components.NestedOuterComponentViewHolder
import com.yelp.android.bentosampleapp.R

class NestedOuterComponentExampleViewHolder : NestedOuterComponentViewHolder() {
    override val outerLayout = R.layout.nested_component_example
    override val recyclerViewId = R.id.recycler_view
}