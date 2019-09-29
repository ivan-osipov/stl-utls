package stlutils.optimization.stlReducer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

class ErrorComputerTest : Spek({
    Feature("ErrorComputer") {
        lateinit var errorComputer: ErrorComputer
        Scenario("computes areaError if angleError is 0") {
            Given("areaError and angleError equals 0") {
                val angleErrorComputer = mock<AngleErrorComputer>().apply {
                    whenever(compute(any(), any())).thenReturn(0f)
                }
                val areaErrorComputer = mock<AreaErrorComputer>().apply {
                    whenever(compute(any(), any())).thenReturn(1.2345f)
                }
                errorComputer = ErrorComputer(
                    emptyList(),
                    angleErrorComputer,
                    areaErrorComputer
                )
            }
            Then("computes areaError") {
                val error = errorComputer.compute(mock(), mock(), mock())
                assertEquals(1.2345f, error)
            }
        }

        Scenario("computes angleError if areaError is 0") {
            Given("angleError and areaError equals 0") {
                val angleErrorComputer = mock<AngleErrorComputer>().apply {
                    whenever(compute(any(), any())).thenReturn(12.5f)
                }
                val areaErrorComputer = mock<AreaErrorComputer>().apply {
                    whenever(compute(any(), any())).thenReturn(0f)
                }

                errorComputer = ErrorComputer(
                    emptyList(),
                    angleErrorComputer,
                    areaErrorComputer
                )
            }
            Then("computes angleError") {
                val triangleBefore = mock<Pair<Normal, IntArray>>()
                whenever(triangleBefore.first).thenReturn(mock())
                val triangleAfter = mock<Pair<Normal, IntArray>>()
                whenever(triangleAfter.first).thenReturn(mock())

                val error = errorComputer.compute(
                    mock(),
                    triangleBefore,
                    triangleAfter
                )
                assertEquals(12.5f, error)
            }
        }

        Scenario("computes total error on top of angleError and areaError") {
            Given("angleError and areaError equals 0") {
                val angleErrorComputer = mock<AngleErrorComputer>().apply {
                    whenever(compute(any(), any())).thenReturn(12.5f)
                }
                val areaErrorComputer = mock<AreaErrorComputer>().apply {
                    whenever(compute(any(), any())).thenReturn(4f)
                }

                errorComputer = ErrorComputer(
                    emptyList(),
                    angleErrorComputer,
                    areaErrorComputer
                )
            }
            Then("computes total error") {
                val triangleBefore = mock<Pair<Normal, IntArray>>()
                whenever(triangleBefore.first).thenReturn(mock())
                val triangleAfter = mock<Pair<Normal, IntArray>>()
                whenever(triangleAfter.first).thenReturn(mock())

                val error = errorComputer.compute(
                    mock(),
                    triangleBefore,
                    triangleAfter
                )
                assertEquals(50f, error)
            }
        }
    }
})