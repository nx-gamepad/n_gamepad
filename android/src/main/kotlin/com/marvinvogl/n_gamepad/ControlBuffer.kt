@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.marvinvogl.n_gamepad

import java.lang.Float

class ControlBuffer(
    size: Int,
    private val type: Int,
) {
    private val buffer = ByteArray(size + 1)
    private var offset = 1

    var bitfield = type

    val array: ByteArray
        get() {
            buffer[0] = bitfield.toByte()
            bitfield = type
            return buffer
        }

    val length: Int
        get() {
            val temp = offset
            offset = 1
            return temp
        }

    fun putByteData(data: Int) {
        buffer[offset++] = data.toByte()
    }

    fun putCharData(data: Char) {
        buffer[offset++] = data.code.toByte()
    }

    fun putIntData(data: IntArray) {
        for (int in data) {
            buffer[offset++] = int.toByte()
        }
    }

    fun putFloatData(data: FloatArray) {
        for (float in data) {
            val bits = Float.floatToIntBits(float)

            buffer[offset++] = (bits shr 24).toByte()
            buffer[offset++] = (bits shr 16).toByte()
            buffer[offset++] = (bits shr 8).toByte()
            buffer[offset++] = bits.toByte()
        }
    }
}
