package io.github.rehanatron.models;

import java.util.List;

public abstract class Animal extends Organism {
    protected int speed;

    public Animal(int x, int y, int z, int energy, int speed) {
        super(x, y, z, energy);
        this.speed = speed;
    }

    public void move() {
        this.x += (Math.random() * speed) - speed / 2;
        this.y += (Math.random() * speed) - speed / 2;
        this.z += Math.random() * 2;
    }

    public abstract void eat(List<? extends Organism> organisms);
}
