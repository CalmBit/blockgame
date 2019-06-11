package gl

data class UVPair(val u: Int, val v: Int) {
    companion object {
        var MISSING_UV: UVPair = UVPair(15, 15)
    }
}