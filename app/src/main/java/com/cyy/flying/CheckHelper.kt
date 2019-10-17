package com.cyy.flying

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log

class CheckHelper(private val context: Context){

    private val mHandlerThread:HandlerThread = HandlerThread("CheckHelper")
    private var mHandler:Handler? = null

    private val checkTask = object :Runnable{
        override fun run() {
            val packageName = getTopActivity(context)
            Log.e("TAG" , packageName)
            mHandler!!.postDelayed( this , 5000)
        }
    }

    init {
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
        mHandler!!.postDelayed(checkTask , 5000)
    }





}