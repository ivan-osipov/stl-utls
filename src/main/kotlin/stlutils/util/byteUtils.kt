package stlutils.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteArray.toInt(order: ByteOrder = ByteOrder.BIG_ENDIAN) = ByteBuffer.wrap(this).order(order).int

fun ByteArray.toFloat(order: ByteOrder = ByteOrder.BIG_ENDIAN) = ByteBuffer.wrap(this).order(order).float