package com.marvinvogl.n_gamepad

import android.view.MotionEvent
import kotlin.math.abs

abstract class Control(
    val block: Int,
    val bitmask: Int,
) {
    private var allowed = true
    private var accepted = true

    val transmission get() = allowed && accepted

    open fun stop() {
        allowed = false
    }

    open fun block() {
        accepted = false
    }
    
    open fun resume(safe: Boolean): Boolean {
        val before = transmission
        accepted = true

        if (!safe) {
            allowed = true
            return !before
        }
        return transmission
    }

    fun MotionEvent.getAxisValueWithMotionRange(axis: Int): Float {
        val value = getAxisValue(axis)
        val flat = device.getMotionRange(axis, source)?.flat ?: return value

        if (abs(value) > flat) {
            return value
        }
        return 0f
    }
}
