@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.marvinvogl.n_gamepad

import java.lang.Float

class ControlBuffer(
    size: Int,
    private val blocks: Int,
    private val type: Byte,
) {
    private val buffer = ByteArray(size + blocks)
    private val bitfield = IntArray(blocks)

    private var offset = blocks

    init {
        for (index in bitfield.indices) {
            reset(index)
        }
    }

    val populated get() = bitfield.first() and 0b1111 != 0

    val array: ByteArray
        get() {
            for ((index, value) in bitfield.withIndex()) {
                buffer[index] = value.toByte()
                reset(index)
            }
            return buffer
        }

    val length: Int
        get() {
            val length = offset
            offset = blocks
            return length
        }

    fun bitfield(control: Control) {
        if (control.block and 1 != 0) {
            bitfield[control.block - 1] = bitfield[control.block - 1] and 0b01111111
        } else {
            bitfield[control.block - 2] = bitfield[control.block - 2] and 0b10111111
        }
        bitfield[control.block] += control.bitmask
        bitfield[0] = bitfield[0] or type.toInt()
    }

    fun putByteData(data: Int) {
        buffer[offset++] = data.toByte()
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

    private fun reset(index: Int) {
        if (index and 1 == 0) {
            bitfield[index] = when {
                blocks - index > 2 -> 0b11100000
                blocks - index > 1 -> 0b11010000
                else -> 0b11000000
            }
        } else {
            bitfield[index] = 0
        }
    }
}
