package com.bcstreceiver.time

import android.content.Context
import android.content.Intent
import com.bcstreceiver.CallbackProvider
import java.text.SimpleDateFormat
import java.util.*

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/10 16:15
 * 功能描述：时间广播接收器回调
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/10 16:15
 * 修改内容：
 */
class TimeCallbackProvider(format: String? = null, locale: Locale? = null) : CallbackProvider {
    private var dateFormat: SimpleDateFormat? = null
    private lateinit var action: (timeMills: Long, formattedTime: String?) -> Unit

    init {
        format?.run { dateFormat = SimpleDateFormat(this, locale ?: Locale.CHINA) }
    }

    fun act(cb: (timeMills: Long, formattedTime: String?) -> Unit): TimeCallbackProvider {
        this.action = cb
        return this
    }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { _, _ ->
            val timeMills = System.currentTimeMillis()
            val formattedTime = dateFormat?.format(timeMills)
            action.invoke(timeMills, formattedTime)
        }
    }

    override fun triggerAtOnce(context: Context) {
        val timeMills = System.currentTimeMillis()
        val formattedTime = dateFormat?.format(timeMills)
        action.invoke(timeMills, formattedTime)
    }
}