package world.biome;

public abstract class BiomeBase {
    private int ID;
    public float temperature;
    public float rainfall;

    public BiomeBase(float temperature, float rainfall) {
        this.temperature = temperature;
        this.rainfall = rainfall;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
