package com.bcst.receiver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bcstreceiver.BcstReceiver
import com.bcstreceiver.battery.BatteryCallbackProvider
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
                        }
                        //.setCallback { context, intent -> Log.e("***", "action = ${intent.action}") }
                        .setCallbackProvider(callbackProvider)
                        .bind(this, lifecycle)
            }

            R.id.btnWifi -> {
            }

            R.id.btnHome -> {
            }

            R.id.btnScreen -> {
            }

            R.id.btnNet -> {
            }
        }
    }
}
