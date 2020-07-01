package blockgame.block;

public enum EnumDirection {
    NORTH(0, 0, 1),
    SOUTH(0,0,-1),
    EAST(-1, 0,0 ),
    WEST(1,0,0),
    UP(0, 1, 0),
    DOWN(0, -1, 0);

    private final int x;
    private final int y;
    private final int z;

    EnumDirection(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private int[] add(int x, int y, int z, EnumDirection dir) {
        return new int[]{x + dir.x, y + dir.y, z + dir.z};
    }

    public int[] getCovering(int x, int y, int z) {
        return add(x,y,z, this);
    }
}
