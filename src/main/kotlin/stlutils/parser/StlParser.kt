package stlutils.parser

import stlutils.common.Triangle
import stlutils.common.Vector3d
import stlutils.util.toFloat
import stlutils.util.toInt
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.io.StreamTokenizer
import java.io.StreamTokenizer.*
import java.nio.ByteOrder
import java.nio.ByteOrder.*
import java.nio.charset.Charset

internal sealed class StlParser(private val normalPolicy: NormalPolicy) {
    abstract fun parse(bytes: ByteArray): List<Triangle>

    fun computeNormal(a: Vector3d, b: Vector3d, c: Vector3d, normal: Vector3d): Vector3d {
        return when (normalPolicy) {
            NormalPolicy.STRICTLY_INHERITED -> {
                require(!normal.isZero) { "Normal is undefined $normal and not computed"}
                normal
            }
            NormalPolicy.INHERITED -> normal
            NormalPolicy.COMPUTED -> computeRightHandNormal(a, b, c)
            NormalPolicy.MIXED -> if (normal.isZero) computeRightHandNormal(a, b, c) else normal
        }
    }

    private fun computeRightHandNormal(a: Vector3d, b: Vector3d, c: Vector3d): Vector3d {
        return ((b - a) cross (c - a)).normalize()
    }
}

internal class StlBinaryParser(normalPolicy: NormalPolicy) : StlParser(normalPolicy) {
    override fun parse(bytes: ByteArray): List<Triangle> {
        val headerContent = String(bytes.sliceArray(0..79))
        require(!headerContent.startsWith("solid")) { "Ascii stl cannot be parsed as binary" }

        val facesQuantity = bytes.sliceArray(80..83).toInt(LITTLE_ENDIAN)
        require(facesQuantity > 0) { "Faces quantity must be more that 0 ($facesQuantity)" }

        return (0 until facesQuantity).asSequence()
            .map { CONTENT_OFFSET + it * SINGLE_FACE_BYTES_QUANTITY }
            .map {
                val a = collectVector(bytes, it + SINGLE_VECTOR_BYTES_QUANTITY)
                val b = collectVector(bytes, it + SINGLE_VECTOR_BYTES_QUANTITY * 2)
                val c = collectVector(bytes, it + SINGLE_VECTOR_BYTES_QUANTITY * 3)
                val normal = computeNormal(a, b, c, collectVector(bytes, it))
                Triangle(normal, a, b, c)
            }.toCollection(ArrayList(facesQuantity))
    }

    private fun collectVector(bytes: ByteArray, from: Int) = Vector3d(
        bytes.sliceArray(from..from + 3).toFloat(LITTLE_ENDIAN),
        bytes.sliceArray(from + 4..from + 7).toFloat(LITTLE_ENDIAN),
        bytes.sliceArray(from + 8..from + 11).toFloat(LITTLE_ENDIAN)
    )

    companion object {
        const val HEADER_SIZE = 80
        const val FACE_QUANTITY_FIELD_SIZE = 4
        const val CONTENT_OFFSET = HEADER_SIZE + FACE_QUANTITY_FIELD_SIZE
        const val SINGLE_VECTOR_BYTES_QUANTITY = 12
        const val ATTRIBUTE_BYTE_SIZE = 2
        const val SINGLE_FACE_BYTES_QUANTITY = SINGLE_VECTOR_BYTES_QUANTITY * 4 + ATTRIBUTE_BYTE_SIZE
    }
}

internal class StlAsciiParser(normalPolicy: NormalPolicy) : StlParser(normalPolicy) {
    override fun parse(bytes: ByteArray): List<Triangle> {
        val triangles = mutableListOf<Triangle>()
        val tokenizer = StreamTokenizer(InputStreamReader(ByteArrayInputStream(bytes), Charset.forName("ASCII")))

        var parsingStep = ParsingStep.NORMAL
        var triangleBuilder: TriangleBuilder = TriangleBuilder()
        while (true) {
            when (tokenizer.nextToken()) {
                TT_EOF -> return triangles
                TT_NUMBER -> {
                    when (parsingStep) {
                        ParsingStep.NORMAL -> {
                            val x = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val y = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val z = tokenizer.nval.toFloat()
                            triangleBuilder.normal = Vector3d(x, y, z)
                        }
                        ParsingStep.V_0 -> {
                            val x = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val y = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val z = tokenizer.nval.toFloat()
                            triangleBuilder.a = Vector3d(x, y, z)
                        }
                        ParsingStep.V_1 -> {
                            val x = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val y = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val z = tokenizer.nval.toFloat()
                            triangleBuilder.b = Vector3d(x, y, z)
                        }
                        ParsingStep.V_2 -> {
                            val x = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val y = tokenizer.nval.toFloat()
                            tokenizer.nextToken()
                            val z = tokenizer.nval.toFloat()
                            triangleBuilder.c = Vector3d(x, y, z)
                        }
                    }
                }
                TT_WORD -> {
                    when (tokenizer.sval) {
                        FACET_PREFIX -> triangleBuilder = TriangleBuilder()
                        NORMAL_PREFIX -> parsingStep = ParsingStep.NORMAL
                        VERTEX_PREFIX -> {
                            parsingStep = when (parsingStep) {
                                ParsingStep.V_0 -> ParsingStep.V_1
                                ParsingStep.V_1 -> ParsingStep.V_2
                                else -> ParsingStep.V_0
                            }
                        }
                        ENDFACET_PREFIX -> triangles.add(triangleBuilder.build())
                    }
                }
            }
        }
    }

    private enum class ParsingStep { NORMAL, V_0, V_1, V_2 }

    private inner class TriangleBuilder {
        lateinit var normal: Vector3d
        lateinit var a: Vector3d
        lateinit var b: Vector3d
        lateinit var c: Vector3d

        fun build() = Triangle(computeNormal(a, b, c, normal), a, b, c)
    }

    companion object {
        const val FACET_PREFIX = "facet"
        const val ENDFACET_PREFIX = "endfacet"
        const val NORMAL_PREFIX = "normal"
        const val VERTEX_PREFIX = "vertex"
    }
}