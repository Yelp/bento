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
                // First get the span of the cell based on its position in the component
                // Then calculate the column width factor based on the number of columns in
                // the recyclerview. In the 2 and 3 column example, there are 6 total
                // columns.
                // The span of a 2 column cell would be 1, but we need to multiply by 6/2=3
                // to get the true span across the recycler view.
                return componentGroup.spanSizeLookup.getSpanSize(position) *
                        (spanCount / componentGroup
                                .getLowestComponentAtIndex(position).numberLanes)
            }
        }
    }
}