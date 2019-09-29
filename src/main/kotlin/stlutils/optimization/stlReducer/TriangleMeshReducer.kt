package stlutils.optimization.stlReducer

import stlutils.common.Triangle

class TriangleMeshReducer {

    fun reduce(triangles: List<Triangle>, targetTrianglesQuantity: Int): List<Triangle> {
        if (triangles.isEmpty()) return emptyList()
        if (triangles.size == targetTrianglesQuantity) return triangles

        val graphData = GraphDataCollector().collect(triangles)

        val supportVertices = SupportVerticesComputer(graphData).compute(64, 64, 64)

        return GraphDataReducer(graphData).reduce(targetTrianglesQuantity, supportVertices)
    }
}
