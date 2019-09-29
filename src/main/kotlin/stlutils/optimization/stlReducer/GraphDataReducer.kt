package stlutils.optimization.stlReducer

import stlutils.common.SimpleTriangle
import stlutils.common.Triangle
import stlutils.common.computeRightHandNormal
import java.util.*
import kotlin.collections.HashSet

internal class GraphDataReducer(graphData: GraphData) {

    private val vertices = graphData.source.vertices
    private val data = graphData.mappings

    private val transformations: Array<TransformationOption?> = arrayOfNulls(data.edges.size)
    private val accumulatedError: FloatArray = FloatArray(data.edges.size) { 0f }
    private val collapsedTriangles: BooleanArray = BooleanArray(data.triangles.size) { false }

    private val errorComputer = ErrorComputer(vertices)

    fun reduce(targetTrianglesQuantity: Int, supportVertices: Set<VertexIdx> = emptySet()): List<Triangle> {
        require(data.triangles.size >= targetTrianglesQuantity) {
            "Triangles quantity (${data.triangles.size}) should be less or equals targetTrianglesQuantity $targetTrianglesQuantity"
        }

        if (targetTrianglesQuantity == 0) return emptyList()

        var currentTrianglesQuantity = data.triangles.size
        if (currentTrianglesQuantity == targetTrianglesQuantity) return collectTriangles()

        val collapsedEdges = HashSet<EdgeIdx>()
        val edgesIndicesQueue = buildEdgesIndicesQueue(supportVertices)

        while (currentTrianglesQuantity > targetTrianglesQuantity) {
            edgesIndicesQueue.sortBy { transformations[it]!!.estimation }

            val collapsingEdgeIdx = edgesIndicesQueue.findNextEdge(collapsedEdges) ?: break
            collapsedEdges.add(collapsingEdgeIdx)

            // <v0, v1> -> v1
            val collapsingTriangles =
                data.trianglesByEdges[collapsingEdgeIdx].onEach { collapsedTriangles[it] = true }

            val (v0Idx, v1Idx) = data.edges[collapsingEdgeIdx]
            data.trianglesByVertices[v0Idx]
                .filterNot { triangleIdx -> collapsedTriangles[triangleIdx] }
                .forEach { triangleIdx ->
                    val vertices = data.triangles[triangleIdx].second
                    if (collapsingTriangles.contains(triangleIdx)) {
                        vertices.indices.forEach {
                            vertices[it] = v1Idx
                        }
                        return@forEach
                    }
                    val v0LocalIdx = vertices.indexOf(v0Idx)
                    if(v0LocalIdx == -1) {
                        return@forEach //TODO it is a hack
                    }
                    vertices[v0LocalIdx] = v1Idx
                }
            val executingTransformation = transformations[collapsingEdgeIdx]!!

            data.edgesByVertices[v0Idx].forEach { edgeIdx ->
                data.edges[edgeIdx] = data.edges[edgeIdx].replaceVertex(v0Idx, v1Idx)
                accumulatedError[edgeIdx] += executingTransformation.estimation
                transformations[edgeIdx] = estimateTransformation(edgeIdx)
            }

            currentTrianglesQuantity -= 2
            println("Current triangles quantity: $currentTrianglesQuantity")
        }
        return collectTriangles()
    }

    private fun collectTriangles(): List<Triangle> {
        return data.triangles.indices.asSequence()
            .filter { !collapsedTriangles[it] }
            .map {
                val (normal, verticesIndices) = data.triangles[it]
                SimpleTriangle(normal, verticesIndices.map { idx -> vertices[idx] })
            }
            .toList()
    }

    private fun buildEdgesIndicesQueue(supportVertices: Set<VertexIdx>): LinkedList<Int> =
        data.edges.indices.asSequence()
            .filter { edgeIdx -> data.trianglesByEdges[edgeIdx].size == 2 }
            .filter { edgeIdx ->
                val (v0, v1) = data.edges[edgeIdx]
                !(supportVertices.contains(v0) || supportVertices.contains(v1))
            }
            .onEach { edgeIdx -> transformations[edgeIdx] = estimateTransformation(edgeIdx) }
            .toCollection(LinkedList())

    private fun estimateTransformation(edgeIdx: EdgeIdx): TransformationOption {
        val (v0Idx, v1Idx) = data.edges[edgeIdx]
        val transformations = data.trianglesByVertices[v0Idx].map { triangleIdx ->
            val triangleBeforeTransformation = data.triangles[triangleIdx]
            Pair(triangleIdx, triangleBeforeTransformation.replace(v0Idx, v1Idx))
        }
        val reductionError = transformations.sumByDouble { (triangleIdx, transformation) ->
            errorComputer.compute(
                data.edges[edgeIdx],
                data.triangles[triangleIdx].copy(),
                transformation
            ).toDouble()
        }
        return TransformationOption(
            estimation = reductionError.toFloat() + accumulatedError[edgeIdx]
        )
    }

    private class TransformationOption(
        val estimation: Float
    )

    private fun Pair<Normal, IntArray>.replace(v0: VertexIdx, v1: VertexIdx): Pair<Normal, IntArray> {
        val transformedTriangle = second.copyOf()
        val foundV0Index = transformedTriangle.indexOf(v0)
        if (foundV0Index == -1) {
            for (i in transformedTriangle.indices) {
                transformedTriangle[i] = v1
            }
        } else {
            transformedTriangle[foundV0Index] = v1
        }

        val normal = if (transformedTriangle.toSet().size == 3) {
            computeRightHandNormal(
                vertices[transformedTriangle[0]],
                vertices[transformedTriangle[1]],
                vertices[transformedTriangle[2]]
            )
        } else first

        return Pair(normal, transformedTriangle)
    }

    private fun Queue<EdgeIdx>.findNextEdge(collapsedEdges: HashSet<EdgeIdx>): Int? {
        while (isNotEmpty()) {
            val edgeIdx = remove()
            if (data.edges[edgeIdx].collapsed) {
                continue
            }
            val backwardEdgeIdx = data.edgesIndices[data.edges[edgeIdx].swap()] ?: continue //TODO should be getValue
            val notCollapsed = !collapsedEdges.contains(backwardEdgeIdx)
            if (notCollapsed) return edgeIdx
        }
        return null
    }

    private val EdgeAsIdxs.collapsed: Boolean
        get() = first == second

    private fun <T, R> Pair<T, R>.swap() = Pair(second, first)

    private fun EdgeAsIdxs.replaceVertex(oldIdx: VertexIdx, newIdx: VertexIdx) = when (oldIdx) {
        first -> Pair(newIdx, second)
        second -> Pair(first, newIdx)
        else -> {
            System.err.println("Pair doesn't contain oldIdx as any item of tuple")
            this //TODO it is a hack
        }
    }
}