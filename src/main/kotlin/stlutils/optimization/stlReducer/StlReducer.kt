package stlutils.optimization.stlReducer

import stlutils.common.*
import java.util.*
import kotlin.collections.HashSet

class StlReducer {

    fun reduce(triangles: List<Triangle>): List<Triangle> {
        if (triangles.isEmpty()) return emptyList()

        val graphData = GraphDataCollector().collect(triangles)

        val supportVertices = SupportVerticesComputer(graphData).compute()

        return Reducer(graphData, supportVertices).reduce(triangles.size / 2)
    }

    private class Reducer(graphData: GraphData, val supportVertices: Set<VertexIdx>) {

        val vertices = graphData.source.vertices
        val data = graphData.mappings

        val transformations: Array<TransformationOption?> = arrayOfNulls(data.edges.size)
        val accumulatedError: FloatArray = FloatArray(data.edges.size) { 0f }
        val collapsedTriangles: BooleanArray = BooleanArray(data.triangles.size) { false }

        fun reduce(targetTrianglesQuantity: Int): List<Triangle> {
            val collapsedEdges = HashSet<EdgeIdx>()
            val edgesIndicesQueue = data.edges.indices.asSequence()
                .filter { edgeIdx -> data.trianglesByEdges[edgeIdx].size == 2 }
                .filter { edgeIdx ->
                    val (v0, v1) = data.edges[edgeIdx]
                    !(supportVertices.contains(v0) || supportVertices.contains(v1))
                }
                .onEach { edgeIdx -> transformations[edgeIdx] = estimateTransformation(edgeIdx) }
                .toCollection(LinkedList())

            var trianglesQuantity = data.triangles.size
            while (trianglesQuantity > targetTrianglesQuantity) {
                edgesIndicesQueue.sortBy { transformations[it]!!.estimation }

                val collapsingEdgeIdx = edgesIndicesQueue.findNextEdge(collapsedEdges) ?: break
                collapsedEdges.add(collapsingEdgeIdx)

                // <v0, v1> -> v1
                val collapsingTriangles =
                    data.trianglesByEdges[collapsingEdgeIdx].onEach { collapsedTriangles[it] = true }

                val (v0Idx, v1Idx) = data.edges[collapsingEdgeIdx]
                data.trianglesByVertices[v0Idx].forEach { triangleIdx ->
                    val vertices = data.triangles[triangleIdx].second
                    if (collapsingTriangles.contains(triangleIdx)) {
                        vertices.indices.forEach {
                            vertices[it] = v1Idx
                        }
                        return@forEach
                    }
                    vertices[vertices.indexOf(v0Idx)] = v1Idx
                }
                val executingTransformation = transformations[collapsingEdgeIdx]!!

                data.edgesByVertices[v0Idx].forEach { edgeIdx ->
                    data.edges[edgeIdx] = data.edges[edgeIdx].replaceVertex(v0Idx, v1Idx)
                    accumulatedError[edgeIdx] += executingTransformation.estimation
                    transformations[edgeIdx] = estimateTransformation(edgeIdx)
                }

                trianglesQuantity -= 2
            }
            return collectTriangles()
        }

        private fun Queue<EdgeIdx>.findNextEdge(collapsedEdges: HashSet<EdgeIdx>): Int? {
            while (isNotEmpty()) {
                val edgeIdx = remove()
                val backwardEdgeIdx = data.edgesIndices.getValue(data.edges[edgeIdx].swap())
                val notCollapsed = !collapsedEdges.contains(backwardEdgeIdx)
                if (notCollapsed) return edgeIdx
            }
            return null
        }

        private fun collectTriangles(): List<Triangle> {
            return data.triangles.indices.asSequence()
                .filter { !collapsedTriangles[it] }
                .map {
                    val (normal, verticesIdxs) = data.triangles[it]
                    SimpleTriangle(normal, verticesIdxs.map { idx -> vertices[idx] })
                }
                .toList()
        }

        private fun EdgeAsIdxs.replaceVertex(oldIdx: VertexIdx, newIdx: VertexIdx) = when (oldIdx) {
            first -> Pair(newIdx, second)
            second -> Pair(first, newIdx)
            else -> throw IllegalArgumentException("Pair doesn't contain oldIdx as any item of tuple")
        }

        private fun <T, R> Pair<T, R>.swap() = Pair(second, first)

        private fun estimateTransformation(edgeIdx: EdgeIdx): TransformationOption {
            val (v0Idx, v1Idx) = data.edges[edgeIdx]
            val transformations = data.trianglesByVertices[v0Idx].map { triangleIdx ->
                val triangleBeforeTransformation = data.triangles[triangleIdx]
                Pair(triangleIdx, triangleBeforeTransformation.replace(v0Idx, v1Idx))
            }
            val reductionError = transformations.sumByDouble { (triangleIdx, transformation) ->
                estimateError(
                    data.edges[edgeIdx],
                    Pair(data.triangles[triangleIdx].first, data.triangles[triangleIdx].second),
                    transformation
                ).toDouble()
            }
            return TransformationOption(
                estimation = reductionError.toFloat() + accumulatedError[edgeIdx]
//                transformations = transformations
            )
        }

        class TransformationOption(
            val estimation: Float
//            val transformations: List<Pair<TriangleIdx, Pair<Normal, IntArray>>>
        )

        private fun Pair<Normal, IntArray>.replace(v0: VertexIdx, v1: VertexIdx): Pair<Normal, IntArray> {
            val transformedTriangle = second.copyOf()
            transformedTriangle[transformedTriangle.indexOf(v0)] = v1
            return Pair(
                computeRightHandNormal(
                    vertices[transformedTriangle[0]],
                    vertices[transformedTriangle[1]],
                    vertices[transformedTriangle[2]]
                ), transformedTriangle
            )
        }

        private fun estimateError(
            edge: EdgeAsIdxs,
            triangleBefore: Pair<Normal, IntArray>,
            triangleAfter: Pair<Normal, IntArray>
        ): Float {
            val angleError = estimateAngleError(triangleBefore, triangleAfter)
            if (angleError == 0f) return 0f
            return when (val changedVerticesQty = (triangleBefore.second.toSet() - triangleAfter.second.toSet()).size) {
                1, 2 -> angleError * estimateSquareError(edge, triangleBefore)
                else -> throw IllegalStateException("Changed not 1 or 2 vertices of triangle ($changedVerticesQty)")
            }
        }

        private fun estimateAngleError(
            before: Pair<Normal, IntArray>,
            after: Pair<Normal, IntArray>
        ): Float = 1 - (before.first cos after.first)

        private fun estimateSquareError(edge: EdgeAsIdxs, before: Pair<Normal, IntArray>): Float {
            val (v0, v1) = edge
            val v2 = before.second.find { it != v0 && it != v1 }!!
            val a = vertices[v0] - vertices[v2]
            val b = vertices[v2] - vertices[v1]
            return a.cross(b).length() / 2
        }
    }
}
