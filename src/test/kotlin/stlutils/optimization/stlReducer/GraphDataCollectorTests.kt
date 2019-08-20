package stlutils.optimization.stlReducer

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import stlutils.common.SimpleTriangle
import stlutils.common.SimpleVector3d

class GraphDataCollectorTests : Spek({

    Feature("GraphDataCollector") {
        val graphDataCollector by memoized { GraphDataCollector() }
        Scenario("collect data for empty input") {
            lateinit var data: GraphData
            Given("empty input") {
                data = graphDataCollector.collect(emptyList())
            }
            Then("returns empty mappings") {
                assertEquals(
                    GraphData.Mappings(
                        emptyArray(),
                        emptyArray(),
                        emptyMap(),
                        emptyMap(),
                        emptyArray(),
                        emptyArray(),
                        emptyArray()
                    ),
                    data.mappings
                )
            }
            Then("returns empty source data") {
                assertEquals(
                    GraphData.Source(
                        emptyList(),
                        emptyList()
                    ),
                    data.source
                )
            }
        }

        Scenario("collect data for one triangle") {
            lateinit var data: GraphData
            Given("one triangle") {
                data = graphDataCollector.collect(
                    listOf(
                        SimpleTriangle(
                            SimpleVector3d(0f, 0f, 1f),
                            SimpleVector3d(0f, 0f, 0f),
                            SimpleVector3d(0f, 1f, 0f),
                            SimpleVector3d(1f, 0f, 0f)
                        )
                    )
                )
            }
            Then("returns sources") {
                assertEquals(
                    GraphData.Source(
                        listOf(
                            SimpleTriangle(
                                SimpleVector3d(0f, 0f, 1f),
                                SimpleVector3d(0f, 0f, 0f),
                                SimpleVector3d(0f, 1f, 0f),
                                SimpleVector3d(1f, 0f, 0f)
                            )
                        ),
                        listOf(
                            SimpleVector3d(0f, 0f, 0f),
                            SimpleVector3d(0f, 1f, 0f),
                            SimpleVector3d(1f, 0f, 0f)
                        )
                    ),
                    data.source
                )
            }
            Then("returns mapped triangles") {
                assertEquals(1, data.mappings.triangles.size)
                assertEquals(
                    SimpleVector3d(0f, 0f, 1f),
                    data.mappings.triangles.first().first
                )
                assertArrayEquals(
                    intArrayOf(0, 1, 2),
                    data.mappings.triangles.first().second
                )
            }
            Then("returns mapped edges") {
                assertArrayEquals(
                    arrayOf(Pair(0, 1), Pair(1, 2), Pair(2, 0), Pair(1, 0), Pair(2, 1), Pair(0, 2)),
                    data.mappings.edges
                )
            }
            Then("returns mapped verticesIndices") {
                assertEquals(
                    mapOf(
                        SimpleVector3d(0f, 0f, 0f) to 0,
                        SimpleVector3d(0f, 1f, 0f) to 1,
                        SimpleVector3d(1f, 0f, 0f) to 2
                    ),
                    data.mappings.verticesIndices
                )
            }
            Then("returns mapped edgesIndices") {
                assertEquals(
                    mapOf(
                        EdgeAsIdxs(0, 1) to 0,
                        EdgeAsIdxs(1, 2) to 1,
                        EdgeAsIdxs(2, 0) to 2,
                        EdgeAsIdxs(1, 0) to 3,
                        EdgeAsIdxs(2, 1) to 4,
                        EdgeAsIdxs(0, 2) to 5
                    ),
                    data.mappings.edgesIndices
                )
            }
            Then("returns mapped trianglesByVertices") {
                assertArrayEquals(
                    arrayOf(setOf(0), setOf(0), setOf(0)),
                    data.mappings.trianglesByVertices
                )
            }
            Then("returns mapped edgesByVertices") {
                assertArrayEquals(
                    arrayOf(setOf(0, 2, 3, 5), setOf(0, 1, 3, 4), setOf(1, 2, 4, 5)),
                    data.mappings.edgesByVertices
                )
            }
            Then("returns mapped trianglesByEdges") {
                assertArrayEquals(
                    arrayOf(setOf(0), setOf(0), setOf(0), setOf(0), setOf(0), setOf(0)),
                    data.mappings.trianglesByEdges
                )
            }
        }
        Scenario("collect data for two triangles") {
            lateinit var data: GraphData
            Given("two triangles") {
                data = graphDataCollector.collect(
                    listOf(
                        SimpleTriangle(
                            SimpleVector3d(0f, 0f, 1f),
                            SimpleVector3d(0f, 0f, 0f),
                            SimpleVector3d(0f, 1f, 0f),
                            SimpleVector3d(1f, 0f, 0f)
                        ),
                        SimpleTriangle(
                            SimpleVector3d(0f, 0f, -1f),
                            SimpleVector3d(0f, 0f, 0f),
                            SimpleVector3d(-1f, 0f, 0f),
                            SimpleVector3d(0f, 1f, 0f)
                        )
                    )
                )
            }
            Then("returns sources") {
                assertEquals(
                    GraphData.Source(
                        listOf(
                            SimpleTriangle(
                                SimpleVector3d(0f, 0f, 1f),
                                SimpleVector3d(0f, 0f, 0f),
                                SimpleVector3d(0f, 1f, 0f),
                                SimpleVector3d(1f, 0f, 0f)
                            ),
                            SimpleTriangle(
                                SimpleVector3d(0f, 0f, -1f),
                                SimpleVector3d(0f, 0f, 0f),
                                SimpleVector3d(-1f, 0f, 0f),
                                SimpleVector3d(0f, 1f, 0f)
                            )
                        ),
                        listOf(
                            SimpleVector3d(0f, 0f, 0f),
                            SimpleVector3d(0f, 1f, 0f),
                            SimpleVector3d(1f, 0f, 0f),
                            SimpleVector3d(-1f, 0f, 0f)
                        )
                    ),
                    data.source
                )
            }
            Then("returns mapped triangles") {
                assertEquals(2, data.mappings.triangles.size)
                assertEquals(
                    SimpleVector3d(0f, 0f, 1f),
                    data.mappings.triangles[0].first
                )
                assertEquals(
                    SimpleVector3d(0f, 0f, -1f),
                    data.mappings.triangles[1].first
                )
                assertArrayEquals(
                    intArrayOf(0, 1, 2),
                    data.mappings.triangles[0].second
                )
                assertArrayEquals(
                    intArrayOf(0, 3, 1),
                    data.mappings.triangles[1].second
                )
            }
            Then("returns mapped edges") {
                assertArrayEquals(
                    arrayOf(
                        Pair(0, 1), Pair(1, 2), Pair(2, 0), Pair(1, 0), Pair(2, 1), Pair(0, 2),
                        Pair(0, 3), Pair(3, 1), Pair(3, 0), Pair(1, 3)
                    ),
                    data.mappings.edges
                )
            }
            Then("returns mapped verticesIndices") {
                assertEquals(
                    mapOf(
                        SimpleVector3d(0f, 0f, 0f) to 0,
                        SimpleVector3d(0f, 1f, 0f) to 1,
                        SimpleVector3d(1f, 0f, 0f) to 2,
                        SimpleVector3d(-1f, 0f, 0f) to 3
                    ),
                    data.mappings.verticesIndices
                )
            }
            Then("returns mapped edgesIndices") {
                assertEquals(
                    mapOf(
                        EdgeAsIdxs(0, 1) to 0,
                        EdgeAsIdxs(1, 2) to 1,
                        EdgeAsIdxs(2, 0) to 2,
                        EdgeAsIdxs(1, 0) to 3,
                        EdgeAsIdxs(2, 1) to 4,
                        EdgeAsIdxs(0, 2) to 5,
                        EdgeAsIdxs(0, 3) to 6,
                        EdgeAsIdxs(3, 1) to 7,
                        EdgeAsIdxs(3, 0) to 8,
                        EdgeAsIdxs(1, 3) to 9
                    ),
                    data.mappings.edgesIndices
                )
            }
            Then("returns mapped trianglesByVertices") {
                assertArrayEquals(
                    arrayOf(setOf(0, 1), setOf(0, 1), setOf(0), setOf(1)),
                    data.mappings.trianglesByVertices
                )
            }
            Then("returns mapped edgesByVertices") {
                assertArrayEquals(
                    arrayOf(setOf(0, 2, 3, 5, 6, 8), setOf(0, 1, 3, 4, 7, 9), setOf(1, 2, 4, 5), setOf(6, 7, 8, 9)),
                    data.mappings.edgesByVertices
                )
            }
            Then("returns mapped trianglesByEdges") {
                assertArrayEquals(
                    arrayOf(
                        setOf(0, 1),
                        setOf(0),
                        setOf(0),
                        setOf(0, 1),
                        setOf(0),
                        setOf(0),
                        setOf(1),
                        setOf(1),
                        setOf(1),
                        setOf(1)
                    ),
                    data.mappings.trianglesByEdges
                )
            }
        }
    }
})