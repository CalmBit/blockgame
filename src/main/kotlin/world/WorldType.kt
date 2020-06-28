package world

import org.joml.Vector3f
import world.generators.*

enum class WorldType(val atmoColor: Vector3f, val skyColor: Vector3f, val voidColor: Vector3f, val gen: IGenerator, val light: Float) {
    DEFAULT(Vector3f(0.529f, 0.808f, 0.980f),
        Vector3f(0.000f, 0.749f, 1.000f),
        Vector3f(0.118f, 0.565f, 1.000f),
        DefaultGenerator(),
        1.0f),
    HELL(Vector3f(0.698f, 0.133f, 0.133f),
        Vector3f(0.545f, 0.000f, 0.000f),
        Vector3f(1.000f, 0.000f, 0.000f),
        HellGenerator(),
        0.45f),
    SKY(Vector3f(0.529f, 0.808f, 0.980f),
        Vector3f(0.000f, 0.749f, 1.000f),
        Vector3f(0.118f, 0.565f, 1.000f),
        SkyGenerator(),
        1.0f),


}