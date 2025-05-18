package io.github.rehanatron.models;

public class Plant extends Organism {
    public Plant(int x, int y, int z, int energy, String species) {
        super(x, y, z, energy, species);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "," + x + "," + y + "," + z + "," + energy + "," + 0;
    }
}
