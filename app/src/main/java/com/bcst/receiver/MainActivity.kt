package com.bcst.receiver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bcstreceiver.BcstReceiver
import com.bcstreceiver.battery.BatteryCallbackProvider
import com.bcstreceiver.home.HomeCallbackProvider
import com.bcstreceiver.time.TimeCallbackProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTime.setOnClickListener(this)
        btnBattery.setOnClickListener(this)
        btnWifi.setOnClickListener(this)
        btnHome.setOnClickListener(this)
        btnScreen.setOnClickListener(this)
        btnNet.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        v.isEnabled = false

        when (v.id) {
            R.id.btnTime -> {
                val callbackProvider = TimeCallbackProvider("yyyy-MM-dd HH:mm:ss").act { timeMills, formattedTime ->
                    Log.e("***", "timeMills=$timeMills,formattedTime=$formattedTime")
                }

                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
                            intentFilter.addAction(Intent.ACTION_TIME_TICK)
                        }
                        //.setCallback { context, intent -> Log.e("***", "${System.currentTimeMillis()}") }
                        .setCallbackProvider(callbackProvider)
                        .bind(this, lifecycle)
            }

            R.id.btnBattery -> {
                val callbackProvider = BatteryCallbackProvider()
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
                        .setCallbackProvider(callbackProvider)
                        .bind(this, lifecycle)
            }

            R.id.btnWifi -> {
            }

            R.id.btnHome -> {
                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                        }
                        //.setCallback { context, intent -> Log.e("***", "action = ${intent.action}") }
                        .setCallbackProvider(HomeCallbackProvider { reason ->
                            when (reason) {
                                HomeCallbackProvider.FLAG_HOME -> Log.e("***", "Home")
                                HomeCallbackProvider.FLAG_LOCK -> Log.e("***", "Lock")
                                HomeCallbackProvider.FLAG_RECENT_APPS -> Log.e("***", "Recent apps")
                                HomeCallbackProvider.FLAG_ASSIST -> Log.e("***", "Assist")
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
            }
        }
    }
}
