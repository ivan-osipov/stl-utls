package stlutils.optimization.stlReducer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

class SupportVerticesComputerTests : Spek({
    Feature("SupportVerticesComputer") {
        Scenario("computes support vertices for empty input") {
            lateinit var supportVertices: Set<VertexIdx>
            Given("empty graph data") {
                supportVertices = SupportVerticesComputer(emptyGraphData()).compute(1, 1, 1)
            }
            Then("computes empty result") {
                assertTrue(supportVertices.isEmpty())
            }
        }
        Scenario("computes support vertices for eight triangles") {
            lateinit var supportVertices: Set<VertexIdx>
            Given("plain eight triangles") {
                supportVertices = SupportVerticesComputer(eightTrianglesGraphData()).compute(2, 2, 1)
            }
            Then("computes 4 vertices") {
                assertEquals(4, supportVertices.size)
            }
        }
    }
})