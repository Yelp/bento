package com.yelp.android.bento.core

enum class AsyncInflationStrategy {
    // The "Best Guess" strategy inflates views as components are added to a
    // RecyclerViewComponentController. Guessing how many view holders and views are needed based
    // on the component's 'count'.
    BEST_GUESS,

    // The "Smart" strategy" will only pre-inflate if there's a match in SmartAsyncInflationCache.
    // Use this to completely avoid the "Best Guess" strategy.
    SMART,

    // The "Default" strategy uses the "BEST_GUESS" version the first time a RecyclerView is
    // encountered, subsequent run-ins with the same RecyclerView will use the "SMART" strategy, as
    // there should be data in SmartAsyncInflationCache then.
    DEFAULT
}
