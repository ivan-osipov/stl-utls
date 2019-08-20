package stlutils.common

inline class ListTriangle(private val normalAndVertices: List<Float>): Triangle {
    override val normal: Vector3d
        get() = ListVector3d(normalAndVertices.subList(0, 3))
    override val a: Vector3d
        get() = ListVector3d(normalAndVertices.subList(3, 6))
    override val b: Vector3d
        get() = ListVector3d(normalAndVertices.subList(6, 9))
    override val c: Vector3d
        get() = ListVector3d(normalAndVertices.subList(9, 12))
}