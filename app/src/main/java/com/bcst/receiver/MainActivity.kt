package com.bcst.receiver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bcstreceiver.BatteryCallbackBuilder
import com.bcstreceiver.BcstReceiver
import com.bcstreceiver.TimeCallbackBuilder
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
                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
                            intentFilter.addAction(Intent.ACTION_TIME_TICK)
                        }
                        //.setCallback { _, _ -> Log.e("***", "${System.currentTimeMillis()}") }
                        .setCallback(TimeCallbackBuilder("yyyy-MM-dd HH:mm:ss")
                                .act { timeMills, formattedTime -> Log.e("***", "timeMills=$timeMills,formattedTime=$formattedTime") }
                                .create())
                        .triggerWhenRegister(true)
                        .bind(this, lifecycle)
            }
            R.id.btnBattery -> {
                val callback = BatteryCallbackBuilder()
                        .act(object : BatteryCallbackBuilder.Callback {
                            override fun onChargeChanged(isCharging: Boolean) {
                                tvIsCharging.text = "isCharging = $isCharging"
                            }

                            override fun onAmountChanged(curAmount: Int) {
                                tvBatteryAmount.text = "amount = $curAmount"
                            }
                        })
                        .create()

                BcstReceiver()
                        .withFilter { intentFilter ->
                            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
                            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                        }
                        //.setCallback { _, _ -> Log.e("***", "${System.currentTimeMillis()}") }
                        .setCallback(callback)
                        .triggerWhenRegister(true)
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
