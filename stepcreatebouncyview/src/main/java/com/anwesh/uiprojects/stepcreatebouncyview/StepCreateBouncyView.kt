package com.anwesh.uiprojects.stepcreatebouncyview

/**
 * Created by anweshmishra on 17/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.content.Context
import android.app.Activity

val nodes : Int = 5
val lines : Int = 4
val strokeFactor : Int = 90
val scGap : Float = 0.02f / lines
val delay : Long = 20L / lines
val backColor : Int = Color.parseColor("#BDBDBD")
val foreColor: Int = Color.parseColor("#3F51B5")
