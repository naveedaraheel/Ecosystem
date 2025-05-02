package io.github.rehanatron.models;

import java.util.List;

public class Herbivore extends Animal {
    private Plant currentTarget; // Current plant being targeted for food

    public Herbivore(int x, int y, int z, int energy, int speed, String species) {
        super(x, y, z, energy, speed, species);
    }

    @Override
    public void move(List<? extends Organism> organisms) {
        if (canMove()) {
            energy -= 1;
            // First priority: Flee from carnivores
            Organism nearestCarnivore = findNearestOrganism(organisms, Carnivore.class);
            if (nearestCarnivore != null) {
                moveAwayFrom(nearestCarnivore);
                currentTarget = null; // Abandon current food target when fleeing
            } else if (isCriticalEnergy()) {
                // Second priority: Find food when critically low on energy
                if (currentTarget != null && currentTarget.isAlive()) {
                    // Continue moving towards current target
                    moveTowards(currentTarget);
                } else {
                    // Find new plant target
                    currentTarget = (Plant) findNearestOrganism(organisms, Plant.class);
                    if (currentTarget != null) {
                        moveTowards(currentTarget);
                    } else {
                        moveRandomly();
                    }
                }
            } else if (isLowEnergy()) {
                // Third priority: Look for food when low on energy
                if (currentTarget != null && currentTarget.isAlive()) {
                    moveTowards(currentTarget);
                } else {
                    // Find new plant target
                    currentTarget = (Plant) findNearestOrganism(organisms, Plant.class);
                    if (currentTarget != null) {
                        moveTowards(currentTarget);
                    } else {
                        // Maintain herd spacing if no food is found
                        Organism nearestHerdMember = findNearestHerdMember(organisms);
                        if (nearestHerdMember != null) {
                            maintainHerdDistance(nearestHerdMember);
                        } else {
                            moveRandomly();
                        }
                    }
                }
            } else {
                // Normal behavior: Maintain herd spacing
                Organism nearestHerdMember = findNearestHerdMember(organisms);
                if (nearestHerdMember != null) {
                    maintainHerdDistance(nearestHerdMember);
                } else {
                    moveRandomly();
                }
            }
        }
    }

    @Override
    public void eat(List<? extends Organism> organisms) {
        for (Organism organism : organisms) {
            if (organism instanceof Plant) {
                Plant plant = (Plant) organism;

                if ((Math.abs(plant.x - this.x) < 2) && (Math.abs(plant.y - this.y) < 2)) {
                    this.addEnergy(5);
                    plant.addEnergy(-5);
                    if (plant == currentTarget && plant.getEnergy() <= 0) {
                        currentTarget = null; // Clear target after eating
                    }
                }
            }
        }
    }
}
