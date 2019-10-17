package com.cyy.flying

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log

import android.view.*


class MainService : Service() , View.OnTouchListener {

    private var clickTimes = 0
    private var mTouchSlop = 0

    private var mainView:View? = null
    private var windowManager:WindowManager? = null

    private val mHandle = Handler()

    private var checkHelper:CheckHelper? = null

    private val clickRunnable = Runnable {
        if (!isDragEvent){
            if (clickTimes == 1){
                sendBroadcast(Intent(MyAccessibilitySampleService.BACK_ACTION))
            }else{
                sendBroadcast(Intent(MyAccessibilitySampleService.TASK_ACTION))
            }
        }
        clickTimes = 0
    }

    override fun onCreate() {
        super.onCreate()

        val configuration = ViewConfiguration.get(this)
        mTouchSlop = configuration.scaledTouchSlop
        createView()

        checkHelper = CheckHelper(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    private fun createView(){
        //赋值WindowManager&LayoutParam.
        val params = WindowManager.LayoutParams()
        windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }else{
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }

        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT.or(Gravity.TOP)
        params.x = 0
        params.y = resources.displayMetrics.heightPixels/2
        params.width = dp2px(40).toInt()
        params.height = dp2px(40).toInt()

        val inflater = LayoutInflater.from(application)
        mainView = inflater.inflate(R.layout.main_service_layout,null)
        windowManager?.addView(mainView,params)

        val view = mainView!!.findViewById<View>(R.id.circleBtn)

        view.setOnClickListener {
            clickTimes ++
            mHandle.removeCallbacks(clickRunnable)
            mHandle.postDelayed(clickRunnable , 250)
        }

        view.setOnLongClickListener {
            if (!isDragEvent){
                sendBroadcast(Intent(MyAccessibilitySampleService.HOME_ACTION))
            }
            return@setOnLongClickListener false
        }

        view.setOnTouchListener(this)
    }

    private var downX = 0
    private var downY = 0
    private var isDrag = false
    private var isDragEvent = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        var handle = false
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                isDrag = false
                isDragEvent = false
                downX = event.rawX.toInt()
                downY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = rawX-downX
                val deltaY = rawY-downY
                val lp = mainView!!.layoutParams as WindowManager.LayoutParams
                if (!isDrag && (Math.abs(deltaX)>mTouchSlop ||
                        Math.abs(deltaY)>mTouchSlop)){
                    isDrag = true
                    isDragEvent = true
                    mHandle.removeCallbacks(clickRunnable)
                }
                Log.e("TAG" , "deltaX=$deltaX deltaY=$deltaY")
                if (isDrag){
                    lp.x += deltaX
                    lp.y += deltaY
                    windowManager?.updateViewLayout(mainView,lp)
                }
                downX = rawX
                downY = rawY
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (isDrag){
                    val deltaX = rawX-downX
                    val deltaY = rawY-downY
                    val lp = mainView!!.layoutParams as WindowManager.LayoutParams
                    lp.x += deltaX
                    lp.y += deltaY
                    windowManager?.updateViewLayout(mainView,lp)
                }
                downX = 0
                downY = 0
                isDrag = false
            }
        }
        return handle
    }
}
