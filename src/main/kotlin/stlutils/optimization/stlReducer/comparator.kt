package stlutils.optimization.stlReducer

internal inline fun <T, R : Comparable<R>> Iterable<T>.extremePointsBy(selector: (T) -> R): Pair<T, T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var minElem = iterator.next()
    var maxElem = minElem
    if (!iterator.hasNext()) return Pair(minElem, maxElem)
    var minValue = selector(minElem)
    var maxValue = minValue
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        } else if (maxValue < v) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return Pair(minElem, maxElem)
}