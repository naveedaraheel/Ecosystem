package io.github.rehanatron.models;

import java.util.List;

public class Terrain {
    private int width;
    private int length;
    private int[][] resources;

    public Terrain(int width, int length) {
        this.width = width;
        this.length = length;
        this.resources = new int[width][length];
        initializeResources();
    }

    private void initializeResources() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                resources[i][j] = (int) (Math.random() * 50);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getlength() {
        return length;
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
        return x >= 0 && x < width && y >= 0 && y < length;
    }

    public void growPlants(List<Plant> plants) {
        for (Plant plant : plants) {
            if (plant.isAlive()) {
                int x = plant.x;
                int y = plant.y;

                if (getResourceAt(x, y) > 10) {
                    plant.addEnergy(5);
                    consumeResource(x, y, 5);
                }
            }
        }
    }

    public void displayTerrain() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(resources[i][j] + " ");
            }
            System.out.println();
        }
    }
}
