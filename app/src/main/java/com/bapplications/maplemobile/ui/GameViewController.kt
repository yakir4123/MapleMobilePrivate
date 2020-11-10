package com.bapplications.maplemobile.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.ExpressionInputAction
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.input.events.ExpressionButtonEvent
import com.bapplications.maplemobile.input.events.PressButtonEvent

class GameViewController(button: View, val input: InputAction) {

    var isPressed = false
    var isReleased = false
    get() {
        val res = field
        field = false
        return res
    }

    init {
        button.setOnTouchListener { _: View?, motionEvent: MotionEvent ->
            if (!isPressed && motionEvent.action == MotionEvent.ACTION_DOWN) {
                isPressed = true
                when (input){
                    is ExpressionInputAction -> input.exp?.let {EventsQueue.instance.enqueue( ExpressionButtonEvent(0, it)) }
                    else ->
                        EventsQueue.instance.enqueue(PressButtonEvent(0, input.key, true))
                }
            } else if( input.type == InputAction.Type.SINGLE_CLICK) {
                isPressed = false
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                isPressed = false
                isReleased = true
                EventsQueue.instance.enqueue(PressButtonEvent(0, input.key, false))
            }
            true
        }
    }
}