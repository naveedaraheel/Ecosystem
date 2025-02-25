package io.github.rehanatron.models;

public abstract class Organism {
    public int x, y, z;
    int energy;

    public Organism(int x, int y, int z, int energy) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.energy = energy;
    }

    public boolean isAlive() {
        return this.energy > 0;
    }

    public int getEnergy() {
        return energy;
    }

    public void addEnergy(int x) {
        this.energy += x;
    }
}
