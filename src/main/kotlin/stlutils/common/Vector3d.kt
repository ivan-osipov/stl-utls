package stlutils.common

import kotlin.math.sqrt

data class Vector3d(val x: Float, val y: Float, val z: Float) {
    operator fun minus(other: Vector3d) = Vector3d(x - other.x, y - other.y, z - other.z)

    val isZero: Boolean
        get() = x == 0f && y == 0f && z == 0f

    fun length() = sqrt(x * x + y * y + z * z)

    fun normalize(): Vector3d {
        val norm = 1 / length()
        return Vector3d(x * norm, y * norm, z * norm)
    }

    infix fun cross(b: Vector3d) = Vector3d(
        x = y * b.z - z * b.y,
        y = z * b.x - x * b.z,
        z = x * b.y - y * b.x
    )

    override fun toString() = "($x, $y, $z)"
}