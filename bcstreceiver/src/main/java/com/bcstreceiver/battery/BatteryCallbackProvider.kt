package com.bcstreceiver.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.bcstreceiver.CallbackProvider

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/10 17:10
 * 功能描述：电量广播接收器回调
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/10 17:10
 * 修改内容：
 */
class BatteryCallbackProvider : CallbackProvider {
    private var chargeAction: ((isCharging: Boolean) -> Unit)? = null
    private var amountAction: ((amount: Int) -> Unit)? = null
    private var otherAction: ((action: String) -> Unit)? = null

    private var lastLevel = -2
    private var lastScale = -2

    fun onChargeEvent(event: (isCharging: Boolean) -> Unit): BatteryCallbackProvider {
        this.chargeAction = event
        return this
    }

    fun onAmountEvent(event: (amount: Int) -> Unit): BatteryCallbackProvider {
        this.amountAction = event
        return this
    }

    fun onOtherEvent(event: (action: String) -> Unit): BatteryCallbackProvider {
        this.otherAction = event
        return this
    }

    override fun create(): (context: Context, intent: Intent) -> Unit = { _: Context, intent: Intent ->
        intent.action?.run {
            when (this) {
                Intent.ACTION_POWER_CONNECTED -> chargeAction?.invoke(true)
                Intent.ACTION_POWER_DISCONNECTED -> chargeAction?.invoke(false)
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                    //电量没有发生变化则不回调
                    if (lastLevel == level && lastScale == scale) {
                        return@run
                    }

                    lastLevel = level
                    lastScale = scale

                    val curAmount = (level.toFloat() / scale * 100).toInt()
                    amountAction?.invoke(curAmount)
                }
                else -> otherAction?.invoke(this)
            }
        }
    }

    override fun triggerAtOnce(context: Context) {
        chargeAction?.run {
            val intent = context.applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            this.invoke(isCharging)
        }
    }
}