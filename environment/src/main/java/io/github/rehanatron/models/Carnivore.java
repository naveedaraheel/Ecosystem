package io.github.rehanatron.models;

import java.util.List;

public class Carnivore extends Animal {
    public Carnivore(int x, int y, int z, int energy, int speed) {
        super(x, y, z, energy, speed);
    }

    public void eat(List<? extends Organism> organisms) {
        for (Organism organism : organisms) {
            if (organism instanceof Herbivore) {
                Herbivore herbivore = (Herbivore) organism;

                if ((Math.abs(herbivore.x - this.x) < 10) && (Math.abs(herbivore.y - this.y) < 10)) {
                    this.addEnergy(10);
                    herbivore.addEnergy(-herbivore.getEnergy());
                }
            }
        }
    }
}
