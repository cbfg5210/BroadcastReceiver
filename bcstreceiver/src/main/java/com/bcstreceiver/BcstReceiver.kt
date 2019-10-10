package com.bcstreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/10 14:42
 * 功能描述：广播接收器封装
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/10 14:42
 * 修改内容：
 */
class BcstReceiver : BroadcastReceiver() {
    private val intentFilter: IntentFilter by lazy { IntentFilter() }
    private var callback: ((context: Context, intent: Intent) -> Unit)? = null
    private var hasRegisterReceiver = false

    override fun onReceive(context: Context, intent: Intent) {
        callback?.invoke(context, intent)
    }

    fun withFilter(filter: (IntentFilter) -> Unit): BcstReceiver {
        filter.invoke(intentFilter)
        return this
    }

    fun setCallback(callback: (context: Context, intent: Intent) -> Unit): BcstReceiver {
        this.callback = callback
        return this
    }

    fun register(context: Context) {
        register(context, true)
    }

    fun unregister(context: Context) {
        unregister(context, true)
    }

    /**
     * for AppCompatActivity, Fragment, LifecycleService
     */
    fun bind(context: Context, lifecycle: Lifecycle, event: Lifecycle.Event = Lifecycle.Event.ON_CREATE) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                register(context, event == Lifecycle.Event.ON_CREATE)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                register(context, event == Lifecycle.Event.ON_START)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                register(context, event == Lifecycle.Event.ON_RESUME)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                unregister(context, event == Lifecycle.Event.ON_RESUME)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                unregister(context, event == Lifecycle.Event.ON_START)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                unregister(context, event == Lifecycle.Event.ON_CREATE)
            }
        })
    }

    private fun register(context: Context, shouldRegister: Boolean) {
        if (shouldRegister && !hasRegisterReceiver) {
            context.registerReceiver(this, intentFilter)
            hasRegisterReceiver = true
        }
    }

    private fun unregister(context: Context, shouldUnregister: Boolean) {
        if (shouldUnregister && hasRegisterReceiver) {
            context.unregisterReceiver(this)
            hasRegisterReceiver = false
        }
    }
}