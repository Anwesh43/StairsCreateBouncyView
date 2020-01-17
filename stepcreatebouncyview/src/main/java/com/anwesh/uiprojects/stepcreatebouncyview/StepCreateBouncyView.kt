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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SCBNode(var i : Int, val state : State = State()) {

        private var next : SCBNode? = null
        private var prev : SCBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SCBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSCBNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SCBNode {
            var curr : SCBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class StepBouncyCreateLine(var i : Int) {

        private var curr : SCBNode = SCBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : StepCreateBouncyView) {

        private val animator : Animator = Animator(view)
        private val scb : StepBouncyCreateLine = StepBouncyCreateLine(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            scb.draw(canvas, paint)
            animator.animate {
                scb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            scb.startUpdating {
                animator.start()
            }
        }
    }
}