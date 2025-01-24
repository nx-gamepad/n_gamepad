package com.marvinvogl.n_gamepad

import android.view.KeyEvent
import android.view.MotionEvent

class Dpad : Control(1, 0b00000010) {
    private val data = IntArray(2)

    private var center = 0

    private var x = 0
    private var y = 0

    fun onEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> data[1] = -1
                KeyEvent.KEYCODE_DPAD_DOWN -> data[1] = 1
                KeyEvent.KEYCODE_DPAD_LEFT -> data[0] = -1
                KeyEvent.KEYCODE_DPAD_RIGHT -> data[0] = 1
                KeyEvent.KEYCODE_DPAD_CENTER -> center = 0b00001000
            }
        }
        if (event.action == KeyEvent.ACTION_UP) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> data[1] = 0
                KeyEvent.KEYCODE_DPAD_DOWN -> data[1] = 0
                KeyEvent.KEYCODE_DPAD_LEFT -> data[0] = 0
                KeyEvent.KEYCODE_DPAD_RIGHT -> data[0] = 0
                KeyEvent.KEYCODE_DPAD_CENTER -> center = 0
            }
        }

        if (x != data[0] || y != data[1]) {
            x = data[0]
            y = data[1]

            transferPlatformData(event.deviceId)

            return prepareKeyData(KeyListener.buffer)
        }
        return false
    }

    fun onEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            data[0] = event.getAxisValue(MotionEvent.AXIS_HAT_X).toInt()
            data[1] = event.getAxisValue(MotionEvent.AXIS_HAT_Y).toInt()

            if (x != data[0] || y != data[1]) {
                x = data[0]
                y = data[1]

                transferPlatformData(event.deviceId)

                return prepareMotionData(KeyListener.buffer)
            }
        }
        return false
    }

    private fun transferPlatformData(id: Int) {
        Handler.dpad.sink?.success(listOf(id, x, y))
    }

    private fun prepareKeyData(buffer: ControlBuffer): Boolean {
        if (transmission) {
            buffer.bitfield(this)
            buffer.putByteData(0b00100000 + center)
            buffer.putIntData(data)

            return true
        }
        return false
    }

    private fun prepareMotionData(buffer: ControlBuffer): Boolean {
        if (transmission) {
            buffer.bitfield(this)
            buffer.putByteData(0b00100001)
            buffer.putIntData(data)

            return true
        }
        return false
    }
}
