package stlutils.optimization.stlReducer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import stlutils.common.Triangle

class GraphDataReducerTest: Spek({
    Feature("GraphDataReducer") {
        Scenario("computes valid result for eight triangles") {
            lateinit var reducedTriangles: List<Triangle>
            Given("8 triangles reduced to 4") {
                reducedTriangles = GraphDataReducer(eightTrianglesGraphData()).reduce(4)
            }
            Then("4 triangles") {
                assertEquals(4, reducedTriangles.size)
            }
        }
        Scenario("computes empty result for empty triangles list") {
            lateinit var reducedTriangles: List<Triangle>
            Given("empty triangles list reduced to 0") {
                reducedTriangles = GraphDataReducer(emptyGraphData()).reduce(0)
            }
            Then("empty triangles list") {
                assertTrue(reducedTriangles.isEmpty())
            }
        }
        Scenario("converts 8 triangles to empty list") {
            lateinit var reducedTriangles: List<Triangle>
            Given("empty triangles list reduced to 0") {
                reducedTriangles = GraphDataReducer(eightTrianglesGraphData()).reduce(0)
            }
            Then("empty triangles list") {
                assertTrue(reducedTriangles.isEmpty())
            }
        }
    }
})