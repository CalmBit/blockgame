package block

object TilePalette {
    private var _palette: MutableList<TileState> = mutableListOf()

    init {
        _palette.add(TileState(BlockRegistration.AIR))
    }
    fun getTileRepresentation(t: TileState): Int {
        if(_palette.contains(t)) {
            return _palette.indexOf(t)
        }
        _palette.add(t)
        return _palette.size-1
    }

    fun getTileState(t: Int): TileState {
        if(_palette.size <= t) {
            throw Exception("Invalid tile '$t'")
        }
        return _palette[t]
    }
}