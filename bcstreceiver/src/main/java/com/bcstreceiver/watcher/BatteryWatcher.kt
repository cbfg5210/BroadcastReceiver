package com.bcstreceiver.watcher

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.bcstreceiver.BcstWatcher

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/10 17:10
 * 功能描述：电量广播接收器回调
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/10 17:10
 * 修改内容：
 */
class BatteryWatcher : BcstWatcher {
    private var chargeAction: ((isCharging: Boolean) -> Unit)? = null
    private var amountAction: ((amount: Int) -> Unit)? = null
    private var otherAction: ((action: String) -> Unit)? = null

    private var lastLevel = -2
    private var lastScale = -2

    fun onChargeEvent(event: (isCharging: Boolean) -> Unit): BatteryWatcher {
        this.chargeAction = event
        return this
    }

    fun onAmountEvent(event: (amount: Int) -> Unit): BatteryWatcher {
        this.amountAction = event
        return this
    }

    fun onOtherEvent(event: (action: String) -> Unit): BatteryWatcher {
        this.otherAction = event
        return this
    }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { _: Context, intent: Intent -> handle(intent) }
    }

    private fun handle(intent: Intent) {
        when (val action = intent.action ?: return) {
            Intent.ACTION_POWER_CONNECTED -> chargeAction?.invoke(true)
            Intent.ACTION_POWER_DISCONNECTED -> chargeAction?.invoke(false)
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                //电量没有发生变化则不回调
                if (lastLevel == level && lastScale == scale) {
                    return
                }

                lastLevel = level
                lastScale = scale

                val curAmount = (level.toFloat() / scale * 100).toInt()
                amountAction?.invoke(curAmount)
            }
            else -> otherAction?.invoke(action)
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