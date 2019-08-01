package stlutils.parser.ascii

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import stlutils.common.Triangle
import stlutils.common.Vector3d
import stlutils.parser.NormalPolicy
import stlutils.parser.StlParsingManager

class OneAsciiFileTests {
    @Test
    fun `parses simple file`() {
        val bytes = OneAsciiFileTests::class.java.getResourceAsStream("one_polygon.stl").readBytes()
        val parsingResults = StlParsingManager(NormalPolicy.COMPUTED).parse(bytes)
        assertEquals(1, parsingResults.size)
        assertEquals(
            Triangle(
                Vector3d(0f, 0f, -1f),
                Vector3d(1f, 0f, 0f),
                Vector3d(0f, 1f, 0f),
                Vector3d(1f, 1f, 0f)
            ), parsingResults.first()
        )
    }
}