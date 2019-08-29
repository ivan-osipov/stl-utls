package stlutils.optimization.stlReducer

import stlutils.common.*
import kotlin.math.max
import kotlin.math.min

internal class SupportVerticesComputer(private val data: GraphData) {
    fun compute(
        accuracyCoefX: Int = 1,
        accuracyCoefY: Int = 1,
        accuracyCoefZ: Int = 1
    ): Set<VertexIdx> {
        if (data.source.triangles.isEmpty()) return emptySet()
        val boundingBox = computeBoundingBox()
        val partialBoundingBoxes = splitBoundingBox(boundingBox, accuracyCoefX, accuracyCoefY, accuracyCoefZ)
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
        accuracyCoefX: Int,
        accuracyCoefY: Int,
        accuracyCoefZ: Int
    ): MutableList<BoundingBox> {
        val (minVector, maxVector) = boundingBox
        val diagonal = minVector.abs() + maxVector.abs()
        val stepVector = SimpleVector3d(
            diagonal.x / accuracyCoefX,
            diagonal.y / accuracyCoefY,
            diagonal.z / accuracyCoefZ
        )
        val boundingBoxes = mutableListOf<BoundingBox>()
        for (i in (0 until accuracyCoefX)) {
            for (j in (0 until accuracyCoefY)) {
                for (k in (0 until accuracyCoefZ)) {
                    boundingBoxes.add(
                        BoundingBox(
                            SimpleVector3d(
                                minVector.x + stepVector.x * i,
                                minVector.y + stepVector.y * j,
                                minVector.z + stepVector.z * k
                            ),
                            SimpleVector3d(
                                minVector.x + stepVector.x * (i + 1),
                                minVector.y + stepVector.y * (j + 1),
                                minVector.z + stepVector.z * (k + 1)
                            )
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
