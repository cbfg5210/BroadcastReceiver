package com.bcstreceiver.battery

import android.content.Context
import android.content.Intent
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
    private lateinit var action: Callback

    fun act(callback: Callback): BatteryCallbackProvider {
        this.action = callback
        return this
    }

    override fun create(): (context: Context, intent: Intent?) -> Unit {
        return { _, intent: Intent? ->
            intent?.action?.run {
                when (this) {
                    Intent.ACTION_POWER_CONNECTED -> action.onChargeChanged(true)
                    Intent.ACTION_POWER_DISCONNECTED -> action.onChargeChanged(false)
                    Intent.ACTION_BATTERY_CHANGED -> {
                        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        val curAmount = (level.toFloat() / scale * 100).toInt()
                        action.onAmountChanged(curAmount)
                    }
                }
            }
        }
    }

    override fun triggerAtOnce() {

    }

    /**
     * 电池事件回调
     */
    interface Callback {
        /**
         * 充电/断电
         *
         * @param isCharging true:充电
         */
        fun onChargeChanged(isCharging: Boolean)

        /**
         * 电池电量变化
         *
         * @param curAmount 当前剩余电量
         */
        fun onAmountChanged(curAmount: Int)
    }
}