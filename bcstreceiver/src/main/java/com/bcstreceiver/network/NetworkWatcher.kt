package com.bcstreceiver.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import com.bcstreceiver.BcstWatcher

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/11 10:47
 * 功能描述：
 * 参考：https://cloud.tencent.com/developer/article/1394223
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/11 10:47
 * 修改内容：
 */
class NetworkWatcher : BcstWatcher {
    private lateinit var context: Context

    private val connManager: ConnectivityManager by lazy {
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var netConnAction: ((isConnected: Boolean, isAvailable: Boolean, netType: Int) -> Unit)? = null
    private var wifiConnStateAction: ((connState: NetworkInfo.State?) -> Unit)? = null
    private var wifiStateAction: ((isWifiEnabled: Boolean) -> Unit)? = null
    private var wifiRssiAction: ((signalLevel: Int) -> Unit)? = null

    private var maxSignalLevel = 3

    fun onNetConnEvent(cb: (isConnected: Boolean, isAvailable: Boolean, netType: Int) -> Unit): NetworkWatcher {
        this.netConnAction = cb
        return this
    }

    fun onWifiConnStateEvent(cb: (connState: NetworkInfo.State?) -> Unit): NetworkWatcher {
        this.wifiConnStateAction = cb
        return this
    }

    fun onWifiStateEvent(cb: (isWifiEnabled: Boolean) -> Unit): NetworkWatcher {
        this.wifiStateAction = cb
        return this
    }

    fun onWifiRssiEvent(maxSignalLevel: Int, cb: (signalLevel: Int) -> Unit): NetworkWatcher {
        this.maxSignalLevel = maxSignalLevel
        this.wifiRssiAction = cb
        return this
    }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { _: Context, intent: Intent -> handle(intent) }
    }

    @SuppressLint("MissingPermission")
    private fun handle(intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                netConnAction?.run {
                    val networkInfo = connManager.activeNetworkInfo
                    if (networkInfo == null) this.invoke(false, false, -1)
                    else this.invoke(networkInfo.state == NetworkInfo.State.CONNECTED, networkInfo.isAvailable, networkInfo.type)
                }
            }

            WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                wifiConnStateAction?.run {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                    this.invoke(networkInfo?.state)
                }
            }

            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                wifiStateAction?.run {
                    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
                    this.invoke(wifiState == WifiManager.WIFI_STATE_ENABLED)
                }
            }

            WifiManager.RSSI_CHANGED_ACTION -> {
                wifiRssiAction?.run {
                    val rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -1000)
                    this.invoke(WifiManager.calculateSignalLevel(rssi, maxSignalLevel))
                }
            }
        }
    }

    override fun triggerAtOnce(context: Context) {
        this.context = context
    }
}