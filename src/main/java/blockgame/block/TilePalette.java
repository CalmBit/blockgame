package blockgame.block;

import java.util.ArrayList;
import java.util.List;

public class TilePalette {
    private static final List<TileState> _PALETTE = new ArrayList<>();
    public static TilePalette INSTANCE = new TilePalette();

    private TilePalette() {
        _PALETTE.add(new TileState(BlockRegistry.AIR));
    }

    public static int getTileRepresentation(TileState t) {
        if(_PALETTE.contains(t)) {
            return _PALETTE.indexOf(t);
        }
        _PALETTE.add(t);
        return _PALETTE.size()-1;
    }

    public static TileState getTileState(int t) throws Exception {
        if(_PALETTE.size() <= t) {
            throw new Exception("Invalid tile '$t'");
        }
        return _PALETTE.get(t);
    }
}
