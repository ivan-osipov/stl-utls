package stlutils.common

inline class ListVector3d(private val coords: List<Float>) : Vector3d {
    override val x: Float
        get() = coords[0]
    override val y: Float
        get() = coords[1]
    override val z: Float
        get() = coords[2]
}