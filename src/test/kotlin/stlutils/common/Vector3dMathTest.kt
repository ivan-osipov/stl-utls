package stlutils.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Vector3dMathTest {
    @Test
    fun `computes right hand normal`() {
        val normal = computeRightHandNormal(
            SimpleVector3d(0f, 0f, 0f),
            SimpleVector3d(1f, 0f, 0f),
            SimpleVector3d(0f, 1f, 0f)
        )

        assertEquals(SimpleVector3d(0f, 0f, 1f), normal)
    }

    @Test
    fun `computes reversed right hand normal`() {
        val normal = computeRightHandNormal(
            SimpleVector3d(0f, 1f, 0f),
            SimpleVector3d(1f, 0f, 0f),
            SimpleVector3d(0f, 0f, 0f)
        )

        assertEquals(SimpleVector3d(0f, 0f, -1f), normal)
    }
}