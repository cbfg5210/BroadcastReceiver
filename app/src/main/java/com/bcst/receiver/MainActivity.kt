package com.bcst.receiver

import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bcstreceiver.BcstReceiver
import com.bcstreceiver.battery.BatteryWatcher
import com.bcstreceiver.home.HomeWatcher
import com.bcstreceiver.network.NetworkWatcher
import com.bcstreceiver.time.TimeWatcher
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTime.setOnClickListener(this)
        btnBattery.setOnClickListener(this)
        btnHome.setOnClickListener(this)
        btnScreen.setOnClickListener(this)
        btnNet.setOnClickListener(this)
        btnPkg.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        v.isEnabled = false

        when (v.id) {
            R.id.btnTime -> {
                val bcstWatcher = TimeWatcher("yyyy-MM-dd HH:mm:ss").onTimeEvent { timeMills, formattedTime ->
                    Log.e("***", "timeMills=$timeMills,formattedTime=$formattedTime")
                }

                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
                            intentFilter.addAction(Intent.ACTION_TIME_TICK)
                        }
                        //.setCallback { context, intent -> Log.e("***", "${System.currentTimeMillis()}") }
                        .setBcstWatcher(bcstWatcher)
                        .bind(this, lifecycle)
            }

            R.id.btnBattery -> {
                val bcstWatcher = BatteryWatcher()
                        .onChargeEvent { isCharging -> Log.e("***", "isCharging = $isCharging") }
                        .onAmountEvent { amount -> Log.e("***", "battery amount = $amount") }
                        .onOtherEvent { action -> Log.e("***", "action = $action") }

                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
                            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                            intentFilter.addAction(Intent.ACTION_BATTERY_LOW)
                            //由低电状态恢复电量
                            intentFilter.addAction(Intent.ACTION_BATTERY_OKAY)
                        }
                        //.setCallback { context, intent -> Log.e("***", "action = ${intent.action}") }
                        .setBcstWatcher(bcstWatcher)
                        .bind(this, lifecycle)
            }

            R.id.btnHome -> {
                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                        }
                        //.setCallback { context, intent -> Log.e("***", "action = ${intent.action}") }
                        .setBcstWatcher(HomeWatcher { reason ->
                            when (reason) {
                                HomeWatcher.FLAG_HOME -> Log.e("***", "Home")
                                HomeWatcher.FLAG_LOCK -> Log.e("***", "Lock")
                                HomeWatcher.FLAG_RECENT_APPS -> Log.e("***", "Recent apps")
                                HomeWatcher.FLAG_ASSIST -> Log.e("***", "Assist")
                                else -> Log.e("***", "Other")
                            }
                        })
                        .bind(this, lifecycle)
            }

            R.id.btnScreen -> {
                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
                            //息屏(锁屏)
                            intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
                            //屏幕解锁
                            intentFilter.addAction(Intent.ACTION_USER_PRESENT)
                        }
                        .setCallback { _, intent -> Log.e("***", "action = ${intent.action}") }
                        .bind(this, lifecycle)
            }

            R.id.btnNet -> {
                val bcstWatcher = NetworkWatcher()
                        .onNetConnEvent { isConnected, isAvailable, netType ->
                            Log.e("***", "isConnected = $isConnected,isAvailable = $isAvailable,netType = $netType")
                        }
                        .onWifiStateEvent { isWifiEnabled ->
                            Log.e("***", "isWifiEnabled = $isWifiEnabled")
                        }
                        .onWifiConnStateEvent { connState ->
                            Log.e("***", "connState = $connState")
                        }
                        .onWifiRssiEvent(5) { signalLevel ->
                            Log.e("***", "signalLevel = $signalLevel")
                        }

                BcstReceiver()
                        .withFilter { intentFilter ->
                            //监听网络连接,包括 wifi 和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
                            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                            //用于判断是否连接到了有效 wifi (不能用于判断是否能够连接互联网)
                            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                            //wifi 打开或关闭的状态
                            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
                        }
                        //.setCallback { _, intent -> Log.e("***", "intent = $intent") }
                        .setBcstWatcher(bcstWatcher)
                        .bind(this, lifecycle)
            }

            R.id.btnPkg -> {
                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
                            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
                            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
                            intentFilter.addDataScheme("package")
                        }
                        .setCallback { _, intent ->
                            Log.e("***", "intent = $intent,dataString = ${intent.dataString}")
                        }
                        .bind(this, lifecycle)
            }
        }
    }
}
