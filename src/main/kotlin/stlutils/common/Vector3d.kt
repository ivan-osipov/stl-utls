package stlutils.common

interface Vector3d {
    val x: Float
    val y: Float
    val z: Float

    operator fun component1() = x

    operator fun component2() = y

    operator fun component3() = z
}