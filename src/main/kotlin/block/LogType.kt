package block

import gl.UVPair

enum class LogType(val type_name: String, val top: UVPair, val side: UVPair, val leaves: UVPair) {
    OAK("oak", UVPair(15, 0), UVPair(14, 0), UVPair(0, 1)),
    BIRCH("birch", UVPair(2, 2), UVPair(1, 2), UVPair(0, 2))
}