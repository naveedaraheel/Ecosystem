package io.github.rehanatron.models;

import java.util.List;

public class Herbivore extends Animal {
    public Herbivore(int x, int y, int z, int energy, int speed) {
        super(x, y, z, energy, speed);
    }

    @Override
    public void eat(List<? extends Organism> organisms) {
        for (Organism organism : organisms) {
            if (organism instanceof Plant) {
                Plant plant = (Plant) organism;

                if ((Math.abs(plant.x - this.x) < 2) && (Math.abs(plant.y - this.y) < 2)) {
                    this.addEnergy(5);
                    plant.addEnergy(-5);
                }
            }
        }
    }
}
