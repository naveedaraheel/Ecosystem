package io.github.rehanatron;

import java.util.HashMap;
import java.util.Map;

import io.github.rehanatron.controllers.*;
import io.github.rehanatron.models.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class Main extends Application {
    private SimulationController controller;
    private Map<Animal, Sphere> animalShapes;

    @Override
    public void start(Stage primaryStage) {
        controller = new SimulationController();
        animalShapes = new HashMap<>();

        // Sample animals
        Carnivore carnivore = new Carnivore(50, 50, 0, 100, 5);
        Herbivore herbivore = new Herbivore(100, 100, 0, 100, 2);
        Plant plant = new Plant(100, 100, 0, 4);

        controller.addAnimal(carnivore);
        controller.addAnimal(herbivore);
        controller.addPlant(plant);
        // JavaFX Group for rendering
        Group root = new Group();
        Scene scene = new Scene(root, 600, 400, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ecosystem Simulator");

        // Create spheres for animals
        for (Animal animal : controller.getAnimals()) {
            Sphere sphere = new Sphere(10);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(animal instanceof Carnivore ? Color.RED : Color.GREEN);
            sphere.setMaterial(material);
            sphere.setTranslateX(animal.x);
            sphere.setTranslateY(animal.y);
            root.getChildren().add(sphere);
            animalShapes.put(animal, sphere);
        }
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                controller.updateSimulation();
                updateView();
            }
        }.start();

        primaryStage.show();
    }

    private void updateView() {
        for (Map.Entry<Animal, Sphere> entry : animalShapes.entrySet()) {
            Animal animal = entry.getKey();
            Sphere sphere = entry.getValue();
            sphere.setTranslateX(animal.x);
            sphere.setTranslateY(animal.y);
        }
    }
}
