package com.bapplications.maplemobile.ui

import android.view.MotionEvent
import android.view.View
import com.bapplications.maplemobile.input.InputAction

class GameViewController(button: View, val type: InputAction.Type) {

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
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                isPressed = false
                isReleased = true
            } else if( type == InputAction.Type.SINGLE_CLICK) {
                isPressed = false;
            }
            true
        }
    }
}