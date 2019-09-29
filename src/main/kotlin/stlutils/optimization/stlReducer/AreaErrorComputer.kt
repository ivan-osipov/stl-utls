package stlutils.optimization.stlReducer

import stlutils.common.Vector3d
import stlutils.common.cross
import stlutils.common.length
import stlutils.common.minus

internal class AreaErrorComputer(private val vertices: List<Vector3d>) {
    fun compute(collapsingEdge: EdgeAsIdxs, triangleBefore: Pair<Normal, IntArray>): Float {
        val (v0, v1) = collapsingEdge
        val v2 = triangleBefore.second.find { it != v0 && it != v1 } ?: return Float.MAX_VALUE
        val a = vertices[v0] - vertices[v2]
        val b = vertices[v2] - vertices[v1]
        return a.cross(b).length() / 2
    }
}
