package io.github.rehanatron.controllers;

import io.github.rehanatron.models.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationController {
    private List<Animal> animals;
    private List<Plant> plants;

    public SimulationController() {
        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public void updateSimulation() {
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                animal.move();
                animal.eat(animals);
            } else {
                animals.remove(animal);
            }
        }
        for (Plant plant : plants) {
            if (plant.isAlive()) {
                plant.addEnergy(10);
            }
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<Plant> getPlants() {
        return plants;
    }
}
