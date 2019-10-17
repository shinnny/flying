package com.cyy.flying

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //辅助功能是否开启
        if (!isAccessibilitySettingsOn(this)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                val intent = Intent(this, MainService::class.java)
                startService(intent)
            } else {
                //若没有权限，提示获取.
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                Toast.makeText(this,"需要取得权限以使用悬浮窗",Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        } else {
            //SDK在23以下，不用管.
            val intent = Intent(this, MainService::class.java)
            startService(intent)
        }

        getTitleHeight()
    }


    fun getTitleHeight(): Int {
        // 获取状态栏高度。不能在onCreate回调方法中获取
        val frame = Rect()
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top

        val contentView:View = window.findViewById(Window.ID_ANDROID_CONTENT);
        val contentTop = contentView.getTop()

        return contentTop - statusBarHeight
    }

    private fun isAccessibilitySettingsOn( context:Context):Boolean {
        var accessibilityEnabled = 0
        // TestService为对应的服务
        val service = packageName + "/" + MyAccessibilitySampleService::class.java. canonicalName
        Log.e(TAG, "service: $service")
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.applicationContext.contentResolver,
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch ( e:Settings.SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            var settingValue = Settings.Secure.getString (context.applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    var accessibilityService = mStringColonSplitter.next()
                    Log.v(TAG, "-------------- > accessibilityService :: $accessibilityService  $service" )
                    if (accessibilityService.equals(service , true)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }


    }
