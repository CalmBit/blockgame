package block

enum class EnumDirection(val x: Int, val y: Int, val z: Int) {
    NORTH(0, 0, 1),
    SOUTH(0,0,-1),
    EAST(-1, 0,0 ),
    WEST(1,0,0),
    UP(0, 1, 0),
    DOWN(0, -1, 0);

    private fun add(x: Int, y: Int, z: Int, b: EnumDirection): IntArray {
        return intArrayOf(x + b.x, y + b.y, z + b.z)
    }

    fun getCovering(x: Int, y: Int, z: Int): IntArray {
        return add(x,y,z, this)
    }
}