package io.github.rehanatron.models;

import java.util.List;

public class Terrain {
    private int width;
    private int height;
    private int[][] resources; // Represents energy resources available in the terrain

    public Terrain(int width, int height) {
        this.width = width;
        this.height = height;
        this.resources = new int[width][height];
        initializeResources();
    }

    private void initializeResources() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                resources[i][j] = (int) (Math.random() * 50); // Random resource value
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getResourceAt(int x, int y) {
        if (isValidPosition(x, y)) {
            return resources[x][y];
        }
        return 0;
    }

    public void consumeResource(int x, int y, int amount) {
        if (isValidPosition(x, y) && resources[x][y] >= amount) {
            resources[x][y] -= amount;
        }
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void interactWithOrganisms(List<Animal> animals, List<Plant> plants) {
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                int x = animal.getX();
                int y = animal.getY();

                // Animals consume resources
                int energyGained = getResourceAt(x, y) / 2;
                animal.addEnergy(energyGained);
                consumeResource(x, y, energyGained);

                // Keep animals within terrain boundaries
                if (!isValidPosition(x, y)) {
                    animal.setPosition(width / 2, height / 2); // Reset position if out of bounds
                }
            }
        }

        for (Plant plant : plants) {
            if (plant.isAlive()) {
                int x = plant.getX();
                int y = plant.getY();

                // Plants grow by absorbing resources
                if (getResourceAt(x, y) > 10) {
                    plant.addEnergy(5);
                    consumeResource(x, y, 5);
                }
            }
        }
    }

    public void displayTerrain() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(resources[i][j] + " ");
            }
            System.out.println();
        }
    }
}
