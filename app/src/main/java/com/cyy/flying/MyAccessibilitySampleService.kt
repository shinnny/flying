package com.cyy.flying

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent

 class MyAccessibilitySampleService : AccessibilityService() {

    companion object {
        val BACK_ACTION = "com.cyy.flying.BACK_ACTION"
        val HOME_ACTION = "com.cyy.flying.HOME_ACTION"
        val TASK_ACTION = "com.cyy.flying.TASK_ACTION"
    }

    private val TAG = "MY-Accessibility"

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when(it.action){
                    BACK_ACTION->{
                        this@MyAccessibilitySampleService.performGlobalAction(
                                AccessibilityService.GLOBAL_ACTION_BACK)
                    }
                    HOME_ACTION->{
                        this@MyAccessibilitySampleService.performGlobalAction(
                                AccessibilityService.GLOBAL_ACTION_HOME)
                    }
                    TASK_ACTION->{
                        this@MyAccessibilitySampleService.performGlobalAction(
                                AccessibilityService.GLOBAL_ACTION_RECENTS)
                    }
                    else -> {
                    }
                }
            }

        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(BACK_ACTION)
        filter.addAction(HOME_ACTION)
        filter.addAction(TASK_ACTION)
        registerReceiver(receiver , filter)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        Log.e(TAG , "onServiceConnected")
    }

    override fun onInterrupt() {
        Log.e(TAG , "onInterrupt")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        rootInActiveWindow.className
    }

}