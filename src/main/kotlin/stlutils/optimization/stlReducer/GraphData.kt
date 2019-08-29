package stlutils.optimization.stlReducer

import stlutils.common.Triangle

internal data class GraphData(
    val source: Source,
    val mappings: Mappings
) {
    data class Source(
        val triangles: List<Triangle>,
        val vertices: List<Vertex>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Source

            if (triangles != other.triangles) return false
            if (vertices != other.vertices) return false

            return true
        }

        override fun hashCode(): Int {
            var result = triangles.hashCode()
            result = 31 * result + vertices.hashCode()
            return result
        }
    }

    data class Mappings(
        val triangles: Array<Pair<Normal, IntArray>>,
        val edges: Array<EdgeAsIdxs>,
        val verticesIndices: Map<Vertex, VertexIdx>,
        val edgesIndices: Map<EdgeAsIdxs, EdgeIdx>,
        val trianglesByVertices: Array<Set<TriangleIdx>>,
        val edgesByVertices: Array<Set<EdgeIdx>>,
        val trianglesByEdges: Array<Set<TriangleIdx>>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Mappings

            if (!triangles.contentEquals(other.triangles)) return false
            if (!edges.contentEquals(other.edges)) return false
            if (verticesIndices != other.verticesIndices) return false
            if (edgesIndices != other.edgesIndices) return false
            if (!trianglesByVertices.contentEquals(other.trianglesByVertices)) return false
            if (!edgesByVertices.contentEquals(other.edgesByVertices)) return false
            if (!trianglesByEdges.contentEquals(other.trianglesByEdges)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = triangles.contentHashCode()
            result = 31 * result + edges.contentHashCode()
            result = 31 * result + verticesIndices.hashCode()
            result = 31 * result + edgesIndices.hashCode()
            result = 31 * result + trianglesByVertices.contentHashCode()
            result = 31 * result + edgesByVertices.contentHashCode()
            result = 31 * result + trianglesByEdges.contentHashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GraphData

        if (source != other.source) return false
        if (mappings != other.mappings) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + mappings.hashCode()
        return result
    }
}