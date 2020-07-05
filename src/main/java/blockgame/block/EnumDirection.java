package blockgame.block;

public enum EnumDirection {
    NORTH(0, 0, 1),
    SOUTH(0, 0, -1),
    EAST(-1, 0, 0),
    WEST(1, 0, 0),
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

    public int getX(int offset) {
        return this.x + offset;
    }

    public int getX() {
        return this.getX(0);
    }

    public int getY(int offset) {
        return this.y + offset;
    }

    public int getY() {
        return this.getY(0);
    }

    public int getZ(int offset) {
        return this.z + offset;
    }

    public int getZ() {
        return this.getZ(0);
    }

    private int[] add(int x, int y, int z, EnumDirection dir) {
        return new int[]{x + dir.x, y + dir.y, z + dir.z};
    }

    public int[] getCovering(int x, int y, int z) {
        return add(x, y, z, this);
    }
}
