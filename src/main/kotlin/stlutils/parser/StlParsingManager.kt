package stlutils.parser

import stlutils.common.Triangle

private val SOLID_TOKEN = "solid ".toByteArray()

class StlParsingManager(private val normalPolicy: NormalPolicy = NormalPolicy.MIXED) {

    fun parse(bytes: ByteArray): List<Triangle> {
        val ascii = (0 until SOLID_TOKEN.size).all {
            bytes[it] == SOLID_TOKEN[it]
        }
        return (if(ascii) StlAsciiParser(normalPolicy) else StlBinaryParser(normalPolicy)).parse(bytes)
    }
}