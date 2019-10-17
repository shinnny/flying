package com.cyy.flying

import android.app.ActivityManager
import android.content.Context
import android.util.Log


fun getTopActivity(context:Context):String {
    try {
        val manager =context.getSystemService (Context.ACTIVITY_SERVICE) as ActivityManager
        //获取正在运行的task列表，其中1表示最近运行的task，通过该数字限制列表中task数目，最近运行的靠前
        val runningTaskInfos = manager.getRunningTasks (1)

        if (runningTaskInfos != null && runningTaskInfos.size != 0) {
            return (runningTaskInfos[0].baseActivity).packageName
        }
    } catch (e:Exception) {
        Log.e("TAG","栈顶应用:" + e);
    }
    return ""

}