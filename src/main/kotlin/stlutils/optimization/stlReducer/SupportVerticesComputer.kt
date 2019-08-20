package stlutils.optimization.stlReducer

import stlutils.common.*
import kotlin.math.max
import kotlin.math.min

class SupportVerticesComputer(val data: GraphData) {
    fun compute(): Set<VertexIdx> {
        val boundingBox = computeBoundingBox()
        val partialBoundingBoxes = splitBoundingBox(boundingBox, 4)
        val vectorsByBoxes = data.source.vertices.groupByBoxes(partialBoundingBoxes)
        return vectorsByBoxes.findSupportVertices(partialBoundingBoxes)
            .mapTo(HashSet()) { data.mappings.verticesIndices.getValue(it) }
    }

    private fun computeBoundingBox(): BoundingBox {
        val triangles = data.source.triangles
        var minVector: Vector3d = triangles.first().a
        var maxVector: Vector3d = triangles.first().a
        for (triangle in triangles) {
            for (point in triangle.vertices) {
                minVector =
                    SimpleVector3d(min(point.x, minVector.x), min(point.y, minVector.y), min(point.z, minVector.z))
                maxVector =
                    SimpleVector3d(max(point.x, maxVector.x), max(point.y, maxVector.y), max(point.z, maxVector.z))
            }
        }
        return BoundingBox(minVector, maxVector)
    }

    private fun splitBoundingBox(
        boundingBox: BoundingBox,
        sectionsQuantity: Int
    ): MutableList<BoundingBox> {
        val (minVector, maxVector) = boundingBox
        val stepVector =
            (minVector + maxVector) / (2f * sectionsQuantity) //TODO possible to use differently devided volumes
        val boundingBoxes = mutableListOf<BoundingBox>()
        for (i in (0 until sectionsQuantity)) {
            for (j in (0 until sectionsQuantity)) {
                for (k in (0 until sectionsQuantity)) {
                    boundingBoxes.add(
                        BoundingBox(
                            SimpleVector3d(stepVector.x * i, stepVector.y * j, stepVector.z * k),
                            SimpleVector3d(stepVector.x * (i + 1), stepVector.y * (j + 1), stepVector.z * (k + 1))
                        )
                    )
                }
            }
        }
        return boundingBoxes
    }

    private fun Collection<Vector3d>.groupByBoxes(boundingBoxes: MutableList<BoundingBox>): Map<Int, List<Vector3d>> {
        return groupBy { (x, y, z) ->
            for ((i, bb) in boundingBoxes.withIndex()) {
                val (minVector, maxVector) = bb
                val (minX, minY, minZ) = minVector
                val (maxX, maxY, maxZ) = maxVector
                if (x in minX..maxX && y in minY..maxY && z in minZ..maxZ) {
                    return@groupBy i
                }
            }
            throw IllegalStateException("($x,$y,$z) located in unknown bounding box")
        }
    }

    private fun Map<Int, List<Vector3d>>.findSupportVertices(boundingBoxes: MutableList<BoundingBox>): Set<Vector3d> {
        return entries.asSequence().flatMap { (boxIdx, vertices) ->
            if (vertices.isEmpty()) return@flatMap emptySequence<SimpleVector3d>()
            val (minVector, maxVector) = vertices.extremePointsBy { (boundingBoxes[boxIdx].center - it).length() }!! //TODO length is redundant, it can be just squares
            sequenceOf(minVector, maxVector)
        }.toSet()
    }
}
