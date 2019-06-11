package block

enum class EnumDirection(val dir: Triple<Int, Int, Int>) {
    NORTH(Triple(0, 0, 1)),
    SOUTH(Triple(0,0,-1)),
    EAST(Triple(-1, 0,0 )),
    WEST(Triple(1,0,0)),
    UP(Triple(0, 1, 0)),
    DOWN(Triple(0, -1, 0));

    private fun add(a: Triple<Int, Int, Int>, b: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        return Triple(a.first + b.first, a.second + b.second, a.third + b.third)
    }

    fun getCovering(pos: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        return add(pos, this.dir)
    }
}