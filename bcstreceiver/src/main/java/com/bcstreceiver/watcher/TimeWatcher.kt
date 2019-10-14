package com.bcstreceiver.watcher

import android.content.Context
import android.content.Intent
import com.bcstreceiver.BcstWatcher
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
class TimeWatcher(format: String? = null, locale: Locale? = null) : BcstWatcher {
    private var dateFormat: SimpleDateFormat? = null
    private lateinit var action: (timeMills: Long, formattedTime: String?) -> Unit

    init {
        format?.run { dateFormat = SimpleDateFormat(this, locale ?: Locale.CHINA) }
    }

    fun onTimeEvent(cb: (timeMills: Long, formattedTime: String?) -> Unit): TimeWatcher {
        this.action = cb
        return this
    }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { _, _ -> handle() }
    }

    override fun triggerAtOnce(context: Context) {
        handle()
    }

    private fun handle() {
        val timeMills = System.currentTimeMillis()
        val formattedTime = dateFormat?.format(timeMills)
        action.invoke(timeMills, formattedTime)
    }
}