package stlutils.parser.ascii

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import stlutils.common.SimpleTriangle
import stlutils.common.SimpleVector3d
import stlutils.parser.NormalPolicy
import stlutils.parser.StlParsingManager

class OneAsciiFileTests {
    @Test
    fun `parses simple file`() {
        val bytes = OneAsciiFileTests::class.java.getResourceAsStream("one_polygon.stl").readBytes()
        val parsingResults = StlParsingManager(NormalPolicy.COMPUTED).parse(bytes)
        assertEquals(1, parsingResults.size)
        assertEquals(
            SimpleTriangle(
                SimpleVector3d(0f, 0f, -1f),
                SimpleVector3d(1f, 0f, 0f),
                SimpleVector3d(0f, 1f, 0f),
                SimpleVector3d(1f, 1f, 0f)
            ), parsingResults.first()
        )
    }
}