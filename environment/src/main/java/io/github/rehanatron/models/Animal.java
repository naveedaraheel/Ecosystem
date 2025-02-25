package io.github.rehanatron.models;

public abstract class Animal extends Organism {
    protected int speed;

    public Animal(int x, int y, int z, int energy, int speed) {
        super(x, y, z, energy);
        this.speed = speed;
    }

    public void move() {
        this.x += (Math.random() * 10);
        this.y += (Math.random() * 10);
        this.z += (Math.random() * 10);
        this.energy -= 5;
    }

    public boolean isAlive() {
        return this.energy > 0;
    }
}
