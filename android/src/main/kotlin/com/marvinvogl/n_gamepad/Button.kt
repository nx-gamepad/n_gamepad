package com.marvinvogl.n_gamepad

import android.view.KeyEvent

class Button(
    char: Char,
) : Control(1, 0b00000001) {
    companion object {
        private var ordinal = 0
    }
    private val data = intArrayOf(ordinal++, char.code)

    private var state = false

    fun onEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (!state) {
                state = true

                transferPlatformData(event.deviceId)

                return prepareKeyDownData(KeyListener.buffer)
            }
        }
        if (event.action == KeyEvent.ACTION_UP) {
            if (state) {
                state = false

                transferPlatformData(event.deviceId)

                return prepareKeyUpData(KeyListener.buffer)
            }
        }
        return false
    }

    private fun transferPlatformData(id: Int) {
        Handler.button.sink?.success(listOf(data.first(), id, state))
    }

    private fun prepareKeyDownData(buffer: ControlBuffer): Boolean {
        if (transmission) {
            buffer.bitfield(this)
            buffer.putByteData(0b00101000)
            buffer.putIntData(data)

            return true
        }
        return false
    }

    private fun prepareKeyUpData(buffer: ControlBuffer): Boolean {
        if (transmission) {
            buffer.bitfield(this)
            buffer.putByteData(0b00100000)
            buffer.putIntData(data)

            return true
        }
        return false
    }
}
