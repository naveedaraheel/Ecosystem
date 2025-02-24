package io.github.rehanatron.models;

public abstract class Organism {
    protected int x, y, z;
    protected int energy;

    public Organism(int x, int y, int z, int energy) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.energy = energy;
    }
}
