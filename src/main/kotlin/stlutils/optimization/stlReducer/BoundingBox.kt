package stlutils.optimization.stlReducer

import stlutils.common.Vector3d
import stlutils.common.div
import stlutils.common.plus

data class BoundingBox(val minVector: Vector3d, val maxVector: Vector3d) {
    val center = (minVector + maxVector) / 2f
}