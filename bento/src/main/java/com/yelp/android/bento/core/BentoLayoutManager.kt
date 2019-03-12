package com.yelp.android.bento.core

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BentoLayoutManager(
        context: Context,
        componentGroup: ComponentGroup,
        @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL
) : GridLayoutManager(context, componentGroup.numberLanes, orientation, false) {

    init {
        spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                // Get the component at the position and the range of that component.
                val component = componentGroup.componentAt(position)
                val range = componentGroup.rangeOf(component) ?: return 1

                // Should never happen, but AS complains about a possible NPE.
                // First get the span of the cell based on its position in the component
                // Then calculate the column width factor based on the number of columns in
                // the recyclerview. In the 2 and 3 column example, there are 6 total
                // columns.
                // The span of a 2 column cell would be 1, but we need to multiply by 6/2=3
                // to get the true span across the recycler view.
                return component.spanSizeLookup.getSpanSize(position - range.mLower) * (spanCount / component.getNumberLanesAtPosition(
                        position - range.mLower))
            }
        }
    }
}