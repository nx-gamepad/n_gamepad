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

    val populated get() = bitfield.first().toInt() and 0b1111 != 0

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
            val temp = offset
            offset = blocks
            return temp
        }

    fun bitfield(control: Control) {
        if (control.block and 1 != 0) {
            bitfield[control.block - 1] = bitfield[control.block - 1] or 0b10000000
        } else {
            bitfield[control.block - 2] = bitfield[control.block - 2] or 0b01000000
        }
        bitfield[control.block] = bitfield[control.block] + control.bitmask
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
                blocks - index > 2 -> 0b00100000
                blocks - index > 1 -> 0b00010000
                else -> 0
            }
        } else {
            bitfield[index] = 0
        }
    }
}
