package stlutils.optimization.stlReducer

import stlutils.common.*
import kotlin.math.absoluteValue

internal class ErrorComputer(
    vertices: List<Vector3d>,
    private val angleErrorComputer: AngleErrorComputer = AngleErrorComputer(),
    private val areaErrorComputer: AreaErrorComputer = AreaErrorComputer(vertices)
) {

    fun compute(
        collapsingEdge: EdgeAsIdxs,
        triangleBefore: Pair<Normal, IntArray>,
        triangleAfter: Pair<Normal, IntArray>
    ): Float {
        val angleError = angleErrorComputer.compute(triangleBefore.first, triangleAfter.first).absoluteValue
        val areaError = areaErrorComputer.compute(collapsingEdge, triangleBefore).absoluteValue
        if (angleError == 0f) return areaError
        if (areaError == 0f) return angleError
        return angleError * areaError
    }
}


