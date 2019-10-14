package com.bcstreceiver.watcher

import android.content.Context
import android.content.Intent
import com.bcstreceiver.BcstWatcher

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/11 10:47
 * 功能描述：
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/11 10:47
 * 修改内容：
 */
class HomeWatcher(private val action: (reason: String) -> Unit) : BcstWatcher {
    companion object {
        private const val KEY_REASON = "reason"
        const val FLAG_HOME = "homekey"
        const val FLAG_LOCK = "lock"
        const val FLAG_RECENT_APPS = "recentapps"
        const val FLAG_ASSIST = "assist" //某些三星手机的程序列表键
    }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { _, intent -> action.invoke(intent.getStringExtra(KEY_REASON)) }
    }

    override fun triggerAtOnce(context: Context) {
    }
}