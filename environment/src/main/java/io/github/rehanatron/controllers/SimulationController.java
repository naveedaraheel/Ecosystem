package io.github.rehanatron.controllers;

import io.github.rehanatron.models.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationController {
    private List<Animal> animals;

    public SimulationController() {
        this.animals = new ArrayList<>();
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void updateSimulation() {
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                animal.move();
            }
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }
}
