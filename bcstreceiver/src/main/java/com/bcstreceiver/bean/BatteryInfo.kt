package com.bcstreceiver.bean

import android.os.BatteryManager

/**
 * 添加人：  Tom Hawk
 * 添加时间：2019/11/22 13:55
 * 功能描述：电池状态信息
 * <p>
 * 修改人：  Tom Hawk
 * 修改时间：2019/11/22 13:55
 * 修改内容：
 */
class BatteryInfo {
    var level: Int = 0
        internal set
    var scale: Int = 0
        internal set
    var amount: Int = 0
        internal set
    var voltage: Int = 0
        internal set
    var status: Int = BatteryManager.BATTERY_STATUS_UNKNOWN
        internal set
    var plugged: Int = -1
        internal set
}