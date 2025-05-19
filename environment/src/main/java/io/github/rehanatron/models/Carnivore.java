package io.github.rehanatron.models;

import java.util.List;

public class Carnivore extends Animal {
    private Herbivore currentTarget; // Current herbivore being targeted
    private Herbivore killSite; // Location of the last kill
    private int timeAtKillSite; // Time spent at kill site

    public Carnivore(int x, int y, int z, int energy, int speed, String species) {
        super(x, y, z, energy, speed, species);
    }

    @Override
    public void move(List<? extends Organism> organisms, Terrain terrain) {
        if (canMove()) {
            if (killSite != null) {
                // Stay at kill site to eat
                timeAtKillSite++;
                if (timeAtKillSite > 5) { // Stay for 5 moves
                    killSite = null;
                    timeAtKillSite = 0;
                    this.addEnergy(100 - getEnergy());
                }
                return; // Don't move while eating
            }
            energy -= 1;

            if (isCriticalEnergy() || isLowEnergy()) {
                // First priority: Find food when critically low on energy
                if (currentTarget != null && currentTarget.isAlive()) {
                    moveTowards(currentTarget, terrain);
                } else {
                    // Find new herbivore target with increased detection radius
                    currentTarget = (Herbivore) findNearestOrganism(organisms, Herbivore.class);
                    if (currentTarget != null) {
                        moveTowards(currentTarget, terrain);
                    } else {
                        moveRandomly(terrain);
                    }
                }
            } else {
                // Normal behavior: Maintain pack spacing
                Organism nearestPackMember = findNearestHerdMember(organisms);
                if (nearestPackMember != null) {
                    maintainHerdDistance(nearestPackMember, terrain);
                } else {
                    moveRandomly(terrain);
                }
            }
        }
    }

    @Override
    public void eat(List<? extends Organism> organisms) {
        for (Organism organism : organisms) {
            if (organism instanceof Herbivore) {
                Herbivore herbivore = (Herbivore) organism;

                if ((Math.abs(herbivore.x - this.x) < 10) && (Math.abs(herbivore.y - this.y) < 10)) {
                    herbivore.addEnergy(-herbivore.getEnergy());
                    if (herbivore == currentTarget) {
                        currentTarget = null;
                        killSite = herbivore; // Remember the kill site
                        timeAtKillSite = 0;
                    }
                }
            }
        }
    }
}
