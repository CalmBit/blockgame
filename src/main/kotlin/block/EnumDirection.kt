package block

enum class EnumDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    UP,
    DOWN;

    fun getCovering(pos: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        var f = pos.copy()
        f = when(this) {
            NORTH -> Triple(f.first, f.second, f.third + 1)
            SOUTH -> Triple(f.first, f.second, f.third - 1)
            WEST -> Triple(f.first + 1, f.second, f.third)
            EAST -> Triple(f.first - 1, f.second, f.third)
            UP -> Triple(f.first, f.second + 1, f.third)
            DOWN -> Triple(f.first, f.second - 1, f.third)
        }
        return f
    }
}