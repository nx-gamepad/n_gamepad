package com.marvinvogl.n_gamepad

import android.view.MotionEvent

enum class Hand(
    val axisX: Int,
    val axisY: Int,
    val axis: Int,
    val axisZ: Int,
    val joystick: Int,
    val trigger: Int,
) {
    LEFT(
        MotionEvent.AXIS_X,
        MotionEvent.AXIS_Y,
        MotionEvent.AXIS_LTRIGGER,
        MotionEvent.AXIS_BRAKE,
        0b01000000,
        0b00000100,
    ),
    RIGHT(
        MotionEvent.AXIS_Z,
        MotionEvent.AXIS_RZ,
        MotionEvent.AXIS_RTRIGGER,
        MotionEvent.AXIS_GAS,
        0b10000000,
        0b00001000,
    ),
}
