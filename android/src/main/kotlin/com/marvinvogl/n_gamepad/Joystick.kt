package com.marvinvogl.n_gamepad

import android.view.MotionEvent

class Joystick(
    private val hand: Hand,
) : Control(1, hand.joystick) {
    private val data = FloatArray(2)

    private var threshold = 0

    private var x = 0f
    private var y = 0f

    fun onEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            data[0] = event.getAxisValueWithMotionRange(hand.axisX)
            data[1] = event.getAxisValueWithMotionRange(hand.axisY)

            threshold = if (data[0] * data[0] + data[1] * data[1] > 0f * 0f) {
                0b00001000
            } else {
                0
            }

            if (x != data[0] || y != data[1]) {
                x = data[0]
                y = data[1]

                transferPlatformData(event.deviceId)

                return prepareMotionData(MotionListener.buffer)
            }
        }
        return false
    }

    private fun transferPlatformData(id: Int) {
        Handler.joystick.sink?.success(listOf(hand.ordinal, id, x, y))
    }

    private fun prepareMotionData(buffer: ControlBuffer): Boolean {
        if (transmission) {
            buffer.bitfield(this)
            buffer.putByteData(0b10000001 + hand.ordinal + threshold)
            buffer.putFloatData(data)

            return true
        }
        return false
    }
}
