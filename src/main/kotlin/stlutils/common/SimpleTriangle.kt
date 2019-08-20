package stlutils.common

data class SimpleTriangle(
    override val normal: Vector3d,
    override val a: Vector3d,
    override val b: Vector3d,
    override val c: Vector3d
) : Triangle {

    constructor(normal: Vector3d, vertices: List<Vector3d>) : this(normal, vertices[0], vertices[1], vertices[2])
}