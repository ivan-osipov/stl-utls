package stlutils.optimization.stlReducer

import stlutils.common.Triangle

internal class GraphDataCollector {
    fun collect(triangles: List<Triangle>): GraphData {
        return triangles.fold(
            GraphDataBuilder(
                triangles
            )
        ) { acc, triangle ->
            for (vector in triangle.vertices) {
                acc.sourceTrianglesByVertices.computeIfAbsent(vector) { HashSet() }
                acc.sourceTrianglesByVertices.getValue(vector).add(triangle)
            }
            acc.sourceVertices.addAll(triangle.vertices)
            val edges = triangle.edges
            val allEdges = edges + edges.map { (left, right) -> Pair(right, left) }
            acc.sourceEdges.addAll(allEdges)
            for (edge in allEdges) {
                acc.sourceEdgesByVertices.computeIfAbsent(edge.first) { HashSet() }
                acc.sourceEdgesByVertices.getValue(edge.first).add(edge)
                acc.sourceEdgesByVertices.computeIfAbsent(edge.second) { HashSet() }
                acc.sourceEdgesByVertices.getValue(edge.second).add(edge)
            }
            acc
        }.build()
    }

    private class GraphDataBuilder(
        val orderedSourceTriangles: List<Triangle> = ArrayList(),
        val sourceEdges: MutableSet<Edge> = HashSet(),
        val sourceVertices: MutableSet<Vertex> = HashSet(),
        val sourceTrianglesByVertices: MutableMap<Vertex, MutableSet<Triangle>> = HashMap(),
        val sourceEdgesByVertices: MutableMap<Vertex, MutableSet<Edge>> = HashMap()
    ) {
        fun build(): GraphData {
            val orderedSourceVertices: List<Vertex> = sourceVertices.toList()
            val orderedSourceEdges: List<Edge> = sourceEdges.toList()
            val sourceTrianglesIndices =
                orderedSourceTriangles.asSequence().withIndex().associateBy({ it.value }, { it.index })
            val sourceEdgesIndices =
                orderedSourceEdges.asSequence().withIndex().associateBy({ it.value }, { it.index })
            val sourceVerticesIndices =
                orderedSourceVertices.asSequence().withIndex().associateBy({ it.value }, { it.index })

            val edgesIndices = orderedSourceEdges.withIndex().map { (edgeIdx, edge) ->
                Pair(
                    Pair(
                        sourceVerticesIndices.getValue(edge.first),
                        sourceVerticesIndices.getValue(edge.second)
                    ),
                    edgeIdx
                )
            }.toMap()
            val trianglesByVertices: Array<Set<TriangleIdx>> = sourceTrianglesByVertices.asSequence()
                .map { (vertex, relatedTriangles) ->
                    Pair<Int, HashSet<Int>>(
                        sourceVerticesIndices.getValue(vertex),
                        relatedTriangles.mapTo(HashSet()) { sourceTrianglesIndices.getValue(it) }
                    )
                }.sortedBy { it.first }
                .map { it.second }
                .toList()
                .toTypedArray()

            val triangles: Array<Pair<Normal, IntArray>> = Array(orderedSourceTriangles.size) { triangleIdx ->
                val triangle = orderedSourceTriangles[triangleIdx]
                Pair(
                    triangle.normal,
                    triangle.vertices
                        .map { vertex -> sourceVerticesIndices.getValue(vertex) }
                        .toIntArray()
                )
            }

            val edges: Array<EdgeAsIdxs> = Array(sourceEdges.size) { edgeIdx ->
                orderedSourceEdges[edgeIdx].toIndices(sourceVerticesIndices)
            }

            val edgesByVertices: Array<Set<EdgeIdx>> = sourceEdgesByVertices.asSequence()
                .map { (vertex, relatedEdges) ->
                    Pair<Int, HashSet<Int>>(
                        sourceVerticesIndices.getValue(vertex),
                        relatedEdges.mapTo(HashSet()) { sourceEdgesIndices.getValue(it) }
                    )
                }.sortedBy { it.first }
                .map { it.second }
                .toList()
                .toTypedArray()

            val trianglesByEdges: Array<Set<TriangleIdx>> = Array(sourceEdges.size) { edgeIdx ->
                val (v0Idx, v1Idx) = edges[edgeIdx]
                val v0Triangles = trianglesByVertices[v0Idx]
                val v1Triangles = trianglesByVertices[v1Idx]
                v0Triangles.intersect(v1Triangles)
            }

            return GraphData(
                GraphData.Source(
                    orderedSourceTriangles,
                    orderedSourceVertices
                ),
                GraphData.Mappings(
                    triangles,
                    edges,
                    sourceVerticesIndices,
                    edgesIndices,
                    trianglesByVertices,
                    edgesByVertices,
                    trianglesByEdges
                )
            )
        }

        fun Edge.toIndices(verticesIndices: Map<Vertex, VertexIdx>) =
            Pair(verticesIndices.getValue(first), verticesIndices.getValue(second))

    }
}