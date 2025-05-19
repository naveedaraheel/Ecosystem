package io.github.rehanatron.controllers;

import io.github.rehanatron.models.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationController {
    private List<Animal> animals;
    private List<Plant> plants;
    private Terrain terrain;

    public SimulationController(int terrainWidth, int terrainLength) {
        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
        this.terrain = new Terrain(terrainWidth, terrainLength);
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public void updateSimulation() {
        List<Organism> allOrganisms = new ArrayList<>();
        allOrganisms.addAll(animals);
        allOrganisms.addAll(plants);

        for (Animal animal : animals) {
            if (animal.isAlive()) {
                animal.move(allOrganisms, terrain);

                if (animal instanceof Herbivore) {
                    ((Herbivore) animal).eat(plants);
                } else {
                    ((Carnivore) animal).eat(animals);
                }
            }
        }
        terrain.growPlants(plants);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<Plant> getPlants() {
        return plants;
    }
}
