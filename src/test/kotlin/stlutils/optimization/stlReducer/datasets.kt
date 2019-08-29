package stlutils.optimization.stlReducer

import stlutils.common.SimpleTriangle
import stlutils.common.SimpleVector3d

fun emptyGraphData() = GraphData(
    GraphData.Source(emptyList(), emptyList()),
    GraphData.Mappings(
        emptyArray(),
        emptyArray(),
        emptyMap(),
        emptyMap(),
        emptyArray(),
        emptyArray(),
        emptyArray()
    )
)

fun oneTriangleGraphData() = GraphDataCollector().collect(
    listOf(
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(0f, 0f, 0f),
            SimpleVector3d(0f, 1f, 0f),
            SimpleVector3d(1f, 0f, 0f)
        )
    )
)

fun eightTrianglesGraphData() = GraphDataCollector().collect(
    listOf(
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(-1f, 0f, 0f),
            SimpleVector3d(-1f, 1f, 0f),
            SimpleVector3d(0f, 1f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(-1f, 0f, 0f),
            SimpleVector3d(0f, 1f, 0f),
            SimpleVector3d(0f, 0f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(0f, 0f, 0f),
            SimpleVector3d(0f, 1f, 0f),
            SimpleVector3d(1f, 0f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(0f, 1f, 0f),
            SimpleVector3d(1f, 1f, 0f),
            SimpleVector3d(0f, 0f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(-1f, 0f, 0f),
            SimpleVector3d(-1f, -1f, 0f),
            SimpleVector3d(0f, -1f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(-1f, 0f, 0f),
            SimpleVector3d(0f, 0f, 0f),
            SimpleVector3d(0f, -1f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(0f, 0f, 0f),
            SimpleVector3d(1f, 0f, 0f),
            SimpleVector3d(0f, -1f, 0f)
        ),
        SimpleTriangle(
            SimpleVector3d(0f, 0f, 1f),
            SimpleVector3d(0f, -1f, 0f),
            SimpleVector3d(0f, 0f, 0f),
            SimpleVector3d(1f, -1f, 0f)
        )
    )
)