package world;

import org.joml.Vector3f;
import world.generators.DefaultGenerator;
import world.generators.HellGenerator;
import world.generators.IGenerator;
import world.generators.SkyGenerator;

public enum WorldType {
    DEFAULT(new Vector3f(0.529f, 0.808f, 0.980f),
            new Vector3f(0.000f, 0.749f, 1.000f),
            new Vector3f(0.118f, 0.565f, 1.000f),
            new DefaultGenerator(),
            1.0f),
    HELL(new Vector3f(0.698f, 0.133f, 0.133f),
            new Vector3f(0.545f, 0.000f, 0.000f),
            new Vector3f(1.000f, 0.000f, 0.000f),
            new HellGenerator(),
            0.45f),
    SKY(new Vector3f(0.529f, 0.808f, 0.980f),
            new Vector3f(0.000f, 0.749f, 1.000f),
            new Vector3f(0.118f, 0.565f, 1.000f),
            new SkyGenerator(),
            1.0f);

    public final Vector3f atmoColor;
    public final Vector3f skyColor;
    public final Vector3f voidColor;
    public final IGenerator gen;
    public final float light;

    WorldType(Vector3f atmoColor, Vector3f skyColor, Vector3f voidColor, IGenerator gen, float light) {
        this.atmoColor = atmoColor;
        this.skyColor = skyColor;
        this.voidColor = voidColor;
        this.gen = gen;
        this.light = light;
    }
}
