package com.marvinvogl.n_gamepad

import android.view.MotionEvent

class Trigger(
    private val hand: Hand,
    private val button: Button,
) : Control(1, hand.trigger) {
    private val data = FloatArray(1)

    private var threshold = 0

    private var z = 0f

    fun onEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            data[0] = event.getAxisValueWithMotionRange(hand.axisZ)

            if (data[0] == 0f) {
                data[0] = event.getAxisValueWithMotionRange(hand.axis)
            }

            threshold = if (data[0] > 0f) {
                0b00001000
            } else {
                0
            }

            if (z != data[0]) {
                z = data[0]

                transferPlatformData(event.deviceId)

                return prepareMotionData(MotionListener.buffer)
            }
        }
        return false
    }

    private fun transferPlatformData(id: Int) {
        Handler.trigger.sink?.success(listOf(hand.ordinal, id, z))
    }

    private fun prepareMotionData(buffer: ControlBuffer): Boolean {
        if (transmission) {
            buffer.bitfield(this)
            buffer.putByteData(0b01000001 + hand.ordinal + threshold)
            buffer.putFloatData(data)

            return true
        }
        return false
    }

    override fun stop() {
        button.stop()
        super.stop()
    }

    override fun block() {
        button.block()
        super.block()
    }

    override fun resume(safe: Boolean): Boolean {
        button.resume(safe)
        return super.resume(safe)
    }
}
