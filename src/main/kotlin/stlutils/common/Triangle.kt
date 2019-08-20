package stlutils.common

interface Triangle {
    val normal: Vector3d
    val a: Vector3d
    val b: Vector3d
    val c: Vector3d

    val vertices: Array<Vector3d>
        get() = arrayOf(a, b, c)

    val edges: Array<Pair<Vector3d, Vector3d>>
        get() = arrayOf(Pair(a, b), Pair(b, c), Pair(c, a))
}