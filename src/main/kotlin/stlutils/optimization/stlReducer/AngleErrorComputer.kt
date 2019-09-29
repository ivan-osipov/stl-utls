package stlutils.optimization.stlReducer

import stlutils.common.cos

internal class AngleErrorComputer {
    fun compute(firstNormal: Normal, secondNormal: Normal): Float = 1 - (firstNormal cos secondNormal)
}
