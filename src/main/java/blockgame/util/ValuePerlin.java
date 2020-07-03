package blockgame.util;

import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.Utils;
import org.spongepowered.noise.module.source.Perlin;

public class ValuePerlin extends Perlin {
    private double _frequency = DEFAULT_PERLIN_FREQUENCY;
    private double _lacunarity = DEFAULT_PERLIN_LACUNARITY;
    private NoiseQuality noiseQuality = DEFAULT_PERLIN_QUALITY;
    private int _octaveCount = DEFAULT_PERLIN_OCTAVE_COUNT;
    private double _persistence = DEFAULT_PERLIN_PERSISTENCE;
    private int _seed = DEFAULT_PERLIN_SEED;

    @Override
    public void setFrequency(double frequency) {
        _frequency = frequency;
    }

    @Override
    public void setOctaveCount(int octaveCount) {
        _octaveCount = octaveCount;
    }

    @Override
    public void setSeed(int seed) {
        _seed = seed;
    }

    @Override
    public void setPersistence(double persistence) {
        _persistence = persistence;
    }

    @Override
    public void setLacunarity(double lacunarity) {
        _lacunarity = lacunarity;
    }


    @Override
    public double getValue(double x, double y, double z) {
        double x1 = x;
        double y1 = y;
        double z1 = z;
        double value = 0.0;
        double signal;
        double curPersistence = 1.0;
        double nx, ny, nz;
        int seed;

        x1 *= _frequency;
        y1 *= _frequency;
        z1 *= _frequency;

        for (int curOctave = 0; curOctave < _octaveCount; curOctave++) {

            // Make sure that these floating-point values have the same range as a 32-
            // bit integer so that we can pass them to the coherent-noise functions.
            nx = Utils.makeInt32Range(x1);
            ny = Utils.makeInt32Range(y1);
            nz = Utils.makeInt32Range(z1);

            // Get the coherent-noise value from the input value and add it to the
            // final result.
            seed = (this._seed + curOctave);
            signal = Noise.valueCoherentNoise3D(nx, ny, nz, seed, noiseQuality);
            value += signal * curPersistence;

            // Prepare the next octave.
            x1 *= _lacunarity;
            y1 *= _lacunarity;
            z1 *= _lacunarity;
            curPersistence *= _persistence;
        }

        return value;
    }
}
