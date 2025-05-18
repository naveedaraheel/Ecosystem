package io.github.rehanatron.models;

import java.io.Serializable;
import java.util.List;

public abstract class Animal extends Organism implements Serializable {
    protected float speed;
    private long lastMoveTime;
    private static final long MOVE_INTERVAL = 1000; // Base interval in milliseconds
    private static final int DISTANCE_PER_MOVE = 10; // Distance per move in pixels
    protected static final int DETECTION_RADIUS = 100; // Radius to detect other organisms
    protected static final int HERD_RADIUS = 150; // Radius to detect herd members
    protected static final int MIN_HERD_DISTANCE = 20; // Minimum distance to maintain from herd members
    protected static final int MAX_HERD_DISTANCE = 50; // Maximum distance to maintain from herd members
    protected static final int LOW_ENERGY_THRESHOLD = 30; // Energy level at which to start seeking resources
    protected static final int CRITICAL_ENERGY_THRESHOLD = 15; // Energy level at which to prioritize resource seeking
    protected static final int HUNGRY_DETECTION_RADIUS = 300; // Increased radius when low on energy
    protected static final int STARVING_DETECTION_RADIUS = 500; // Further increased radius when critical energy

    public Animal(int x, int y, int z, int energy, int speed, String species) {
        super(x, y, z, energy, species);
        this.speed = speed;
        this.lastMoveTime = System.currentTimeMillis();
    }

    protected boolean isLowEnergy() {
        return energy <= LOW_ENERGY_THRESHOLD;
    }

    protected boolean isCriticalEnergy() {
        return energy <= CRITICAL_ENERGY_THRESHOLD;
    }

    protected Organism findNearestOrganism(List<? extends Organism> organisms, Class<? extends Organism> targetType) {
        Organism nearest = null;
        double minDistance = Double.MAX_VALUE;
        int currentDetectionRadius = DETECTION_RADIUS;

        // Increase detection radius based on energy level
        if (isCriticalEnergy()) {
            currentDetectionRadius = STARVING_DETECTION_RADIUS;
        } else if (isLowEnergy()) {
            currentDetectionRadius = HUNGRY_DETECTION_RADIUS;
        }

        for (Organism organism : organisms) {
            if (targetType.isInstance(organism) && organism != this && organism.isAlive()) {
                double distance = calculateDistance(organism);
                if (distance < minDistance && distance <= currentDetectionRadius) {
                    minDistance = distance;
                    nearest = organism;
                }
            }
        }
        return nearest;
    }

    protected Organism findNearestHerdMember(List<? extends Organism> organisms) {
        Organism nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Organism organism : organisms) {
            if (organism instanceof Animal && organism != this && organism.isAlive()) {
                Animal otherAnimal = (Animal) organism;
                if (otherAnimal.species.equals(this.species)) {
                    double distance = calculateDistance(organism);
                    if (distance < minDistance && distance <= HERD_RADIUS) {
                        minDistance = distance;
                        nearest = organism;
                    }
                }
            }
        }
        return nearest;
    }

    protected void maintainHerdDistance(Organism herdMember) {
        double distance = calculateDistance(herdMember);

        if (distance < MIN_HERD_DISTANCE) {
            // Too close, move away
            moveAwayFrom(herdMember);
        } else if (distance > MAX_HERD_DISTANCE) {
            // Too far, move closer
            moveTowards(herdMember);
        } else {
            // At comfortable distance, move randomly but stay in the area
            moveRandomly();
        }
    }

    protected double calculateDistance(Organism other) {
        return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
    }

    protected void moveTowards(Organism target) {
        int dx = target.x - this.x;
        int dy = target.y - this.y;

        // Normalize the direction
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = (int) ((dx / length) * DISTANCE_PER_MOVE);
            dy = (int) ((dy / length) * DISTANCE_PER_MOVE);
        }

        this.x += dx;
        this.y += dy;
    }

    protected void moveAwayFrom(Organism threat) {
        int dx = this.x - threat.x;
        int dy = this.y - threat.y;

        // Normalize the direction
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = (int) ((dx / length) * DISTANCE_PER_MOVE);
            dy = (int) ((dy / length) * DISTANCE_PER_MOVE);
        }

        this.x += dx;
        this.y += dy;
    }

    protected void moveRandomly() {
        // Possible directions: 8 directions + staying still
        int direction = (int) (Math.random() * 16);

        // Calculate new position based on direction
        int newX = x;
        int newY = y;

        switch (direction) {
            case 0: // North
                newY -= DISTANCE_PER_MOVE;
                break;
            case 1: // Northeast
                newX += DISTANCE_PER_MOVE;
                newY -= DISTANCE_PER_MOVE;
                break;
            case 2: // East
                newX += DISTANCE_PER_MOVE;
                break;
            case 3: // Southeast
                newX += DISTANCE_PER_MOVE;
                newY += DISTANCE_PER_MOVE;
                break;
            case 4: // South
                newY += DISTANCE_PER_MOVE;
                break;
            case 5: // Southwest
                newX -= DISTANCE_PER_MOVE;
                newY += DISTANCE_PER_MOVE;
                break;
            case 6: // West
                newX -= DISTANCE_PER_MOVE;
                break;
            case 7: // Northwest
                newX -= DISTANCE_PER_MOVE;
                newY -= DISTANCE_PER_MOVE;
                break;
            // case 8-15: stay still
        }

        // Update position if the move was made
        if (direction < 8) {
            x = newX;
            y = newY;
        }
    }

    protected boolean canMove() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime >= MOVE_INTERVAL / speed) {
            lastMoveTime = currentTime;
            return true;
        }
        return false;
    }

    public abstract void move(List<? extends Organism> organisms);

    public abstract void eat(List<? extends Organism> organisms);

    @Override
    public String toString() {
        return this.getClass().getName() + "," + x + "," + y + "," + z + "," + energy + "," + speed;
    }
}
