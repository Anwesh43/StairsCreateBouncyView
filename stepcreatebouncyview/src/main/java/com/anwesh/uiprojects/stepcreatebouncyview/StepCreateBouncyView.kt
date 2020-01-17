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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawStepLine(i : Int, scale : Float, size : Float, paint : Paint) {
    val wsize : Float = ((size * 2)/ lines)
    val ik : Int = i % 2
    val jk : Int = i / 2
    val sf : Float = scale.sinify().divideScale(i, lines)
    save()
    translate(jk * wsize + ik * size, jk * wsize)
    drawLine(0f, 0f, wsize * (1 - jk) * sf, wsize * ik * sf, paint)
    restore()
}

fun Canvas.drawStepLines(scale : Float, size : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawStepLine(j, scale, size, paint)
    }
}

fun Canvas.drawSCBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes)
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * i, i * gap)
    drawStepLines(scale, gap, paint)
    restore()
}

class StepCreateBouncyView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}