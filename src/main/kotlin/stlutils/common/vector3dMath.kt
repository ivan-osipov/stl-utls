package stlutils.common

import kotlin.math.absoluteValue
import kotlin.math.sqrt

operator fun Vector3d.minus(other: Vector3d): Vector3d = SimpleVector3d(x - other.x, y - other.y, z - other.z)

fun Vector3d.scalarProduct(other: Vector3d) = x * other.x + y * other.y + z * other.z

infix fun Vector3d.cos(other: Vector3d) = scalarProduct(other) / length() / other.length()

fun Vector3d.length() = sqrt(x * x + y * y + z * z)

fun Vector3d.normalize(): Vector3d {
    val norm = 1 / length()
    return SimpleVector3d(x * norm, y * norm, z * norm)
}

fun Vector3d.abs(): Vector3d = SimpleVector3d(x.absoluteValue, y.absoluteValue, z.absoluteValue)

infix fun Vector3d.cross(b: Vector3d): Vector3d = SimpleVector3d(
    x = y * b.z - z * b.y,
    y = z * b.x - x * b.z,
    z = x * b.y - y * b.x
)

operator fun Vector3d.plus(other: Vector3d): Vector3d = SimpleVector3d(
    x + other.x, y + other.y, z + other.z
)

operator fun Vector3d.div(scalar: Float): Vector3d = SimpleVector3d(x / scalar, y / scalar, z / scalar)

val Vector3d.isZero: Boolean
    get() = x == 0f && y == 0f && z == 0f


fun computeRightHandNormal(a: Vector3d, b: Vector3d, c: Vector3d): Vector3d {
    return ((b - a) cross (c - a)).normalize()
}