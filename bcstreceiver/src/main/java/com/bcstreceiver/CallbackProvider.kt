package com.bcstreceiver

import android.content.Context
import android.content.Intent

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/10/11 9:08
 * 功能描述：
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/10/11 9:08
 * 修改内容：
 */
interface CallbackProvider {
    /**
     * 创建 BcstReceiver 广播接收器回调
     */
    fun create(): (context: Context, intent: Intent) -> Unit

    /**
     * 注册广播接收器时调用
     */
    fun triggerAtOnce()
}