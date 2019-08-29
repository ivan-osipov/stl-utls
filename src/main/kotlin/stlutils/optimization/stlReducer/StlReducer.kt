package stlutils.optimization.stlReducer

import stlutils.common.Triangle

class StlReducer {

    fun reduce(triangles: List<Triangle>, targetTrianglesQuantity: Int): List<Triangle> {
        if (triangles.isEmpty()) return emptyList()

        val graphData = GraphDataCollector().collect(triangles)

        val supportVertices = SupportVerticesComputer(graphData).compute(64, 64, 64)

        return Reducer(graphData, supportVertices).reduce(targetTrianglesQuantity)
    }
}
