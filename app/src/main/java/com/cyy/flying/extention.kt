package com.cyy.flying

import android.content.Context
import android.util.TypedValue


fun Context.dp2px(dp:Int):Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , dp.toFloat() , resources.displayMetrics)