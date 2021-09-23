package com.yelp.android.bento.core

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.yelp.android.bento.utils.BentoSettings
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

private const val TAG = "BentoAsyncInflater"

/**
 * Inflates a componentViewHolder's views on one of ten lucky background threads.
 */
internal object BentoAsyncLayoutInflater {

    internal val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

    suspend fun inflate(
        viewHolder: ComponentViewHolder<*, *>,
        parent: ViewGroup,
        inflaterDispatcher: CoroutineDispatcher = dispatcher
    ): Pair<ComponentViewHolder<*, *>, View> =
            withContext(inflaterDispatcher) {
                val view = try {
                    viewHolder.inflate(parent)
                } catch (exception: RuntimeException) {
                    // Probably a Looper failure, retry on the UI thread
                    if (BentoSettings.loggingEnabled) {
                        Log.w(TAG, "Failed to inflate resource in the background!" +
                                " Retrying on the UI thread",
                                exception
                        )
                    }
                    withContext(Dispatchers.Main) {
                        viewHolder.inflate(parent)
                    }
                }
                Pair(viewHolder, view)
            }
}
