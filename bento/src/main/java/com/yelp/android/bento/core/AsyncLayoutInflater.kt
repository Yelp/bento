package com.yelp.android.bento.core

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.util.Pools.SynchronizedPool
import java.util.concurrent.ArrayBlockingQueue

class AsyncLayoutInflater(context: Context) {

    private val handlerCallback = Handler.Callback { msg ->
        val request = msg.obj as InflateRequest
        if (request.view == null) {
            request.view = request.inflateBlock?.invoke(request.parent!!)
        }
        requireNotNull(request.callback).invoke(
            requireNotNull(request.view)
        )
        mInflateThread.releaseRequest(request)
        true
    }

    var inflater: LayoutInflater = BasicInflater(context)
    var mHandler: Handler = Handler(handlerCallback)
    var mInflateThread: InflateThread = InflateThread.instance

    @UiThread
    fun inflate(
        inflateBlock: (parent: ViewGroup) -> View,
        parent: ViewGroup,
        callback: (view: View) -> Unit
    ) {
        val request = mInflateThread.obtainRequest()
        request.inflater = this
        request.inflateBlock = inflateBlock
        request.parent = parent
        request.callback = callback
        mInflateThread.enqueue(request)
    }

    private class BasicInflater(context: Context) : LayoutInflater(context) {
        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        @Throws(ClassNotFoundException::class)
        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = createView(name, prefix, attrs)
                    if (view != null) {
                        return view
                    }
                } catch (e: ClassNotFoundException) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf(
                "android.widget.", "android.webkit.", "android.app."
            )
        }
    }

    class InflateRequest {
        var inflateBlock: ((parent: ViewGroup) -> View)? = null
        var inflater: AsyncLayoutInflater? = null
        var parent: ViewGroup? = null
        var view: View? = null
        var callback: ((View) -> Unit)? = null
    }

    class InflateThread : Thread() {
        companion object {
            val instance: InflateThread = InflateThread()

            init {
                instance.start()
            }
        }

        private val mQueue = ArrayBlockingQueue<InflateRequest>(10)
        private val mRequestPool = SynchronizedPool<InflateRequest>(10)

        // Extracted to its own method to ensure locals have a constrained liveness
        // scope by the GC. This is needed to avoid keeping previous request references
        // alive for an indeterminate amount of time, see b/33158143 for details
        private fun runInner() {
            val request: InflateRequest = try {
                mQueue.take()
            } catch (ex: InterruptedException) {
                // Odd, just continue
                Log.w(TAG, ex)
                return
            }
            try {
                request.view = request.inflateBlock?.invoke(request.parent!!)
            } catch (ex: RuntimeException) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(
                    TAG, "Failed to inflate resource in the background! Retrying on the UI" +
                            " thread",
                    ex
                )
            }
            Message.obtain(requireNotNull(request.inflater).mHandler, 0, request).sendToTarget()
        }

        override fun run() {
            while (true) {
                runInner()
            }
        }

        fun obtainRequest(): InflateRequest {
            var obj = mRequestPool.acquire()
            if (obj == null) {
                obj = InflateRequest()
            }
            return obj
        }

        fun releaseRequest(obj: InflateRequest) {
            obj.callback = null
            obj.inflateBlock = null
            obj.inflater = null
            obj.parent = null
            obj.view = null
            mRequestPool.release(obj)
        }

        fun enqueue(request: InflateRequest) {
            try {
                mQueue.put(request)
            } catch (e: InterruptedException) {
                throw RuntimeException("Failed to enqueue async inflate request", e)
            }
        }
    }

    companion object {
        private const val TAG = "AsyncLayoutInflater"
    }

    init {
        inflater = BasicInflater(context)
        mHandler = Handler(handlerCallback)
        mInflateThread = InflateThread.instance
    }
}
