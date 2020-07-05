package blockgame.block;

import blockgame.render.gl.texture.UVPair;

public enum LogType {
    OAK("oak", new UVPair(15, 0), new UVPair(14, 0), new UVPair(0, 1)),
    BIRCH("birch", new UVPair(2, 2), new UVPair(1, 2), new UVPair(0, 2));

    private final String _type_name;
    private final UVPair _top;
    private final UVPair _side;
    private final UVPair _leaves;

    LogType(String type_name, UVPair top, UVPair side, UVPair leaves) {
        this._type_name = type_name;
        this._top = top;
        this._side = side;
        this._leaves = leaves;
    }

    public UVPair getLeaves() {
        return _leaves;
    }

    public UVPair getTop() {
        return _top;
    }

    public UVPair getSide() {
        return _side;
    }

    public String getTypeName() {
        return _type_name;
    }
}
