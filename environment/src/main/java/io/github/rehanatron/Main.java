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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class Main extends Application {
    private SimulationController controller;
    private Map<Animal, Sphere> animalShapes;
    private Map<Plant, Rectangle> plantShapes;
    private Group terrainGroup;

    @Override
    public void start(Stage primaryStage) {
        controller = new SimulationController(100, 100);
        animalShapes = new HashMap<>();
        plantShapes = new HashMap<>();
        terrainGroup = new Group();

        // Sample animals
        Carnivore carnivore = new Carnivore(30, 30, 0, 100, 5);
        Herbivore herbivore = new Herbivore(20, 20, 0, 100, 5);
        Plant plant = new Plant(10, 10, 0, 100);

        controller.addAnimal(carnivore);
        controller.addAnimal(herbivore);
        controller.addPlant(plant);
        // JavaFX Group for rendering
        Group root = new Group();
        root.getChildren().add(terrainGroup);
        Scene scene = new Scene(root, 1000, 720, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ecosystem Simulator");

        drawTerrain();
        drawAnimals(root);
        drawPlants(root);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                controller.updateSimulation();
                updateView();
            }
        }.start();

        primaryStage.show();
    }

    private void drawTerrain() {
        Terrain terrain = controller.getTerrain();
        int cellSize = 10; // Adjust for visualization
        for (int i = 0; i < terrain.getWidth(); i++) {
            for (int j = 0; j < terrain.getlength(); j++) {
                Rectangle cell = new Rectangle(i * cellSize, j * cellSize, cellSize, cellSize);
                int resourceLevel = terrain.getResourceAt(i, j);
                cell.setFill(Color.rgb(0, resourceLevel * 5, 0)); // Darker green = more resources
                terrainGroup.getChildren().add(cell);
            }
        }
    }

    private void drawAnimals(Group root) {
        for (Animal animal : controller.getAnimals()) {
            Sphere sphere = new Sphere(5);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(animal instanceof Carnivore ? Color.RED : Color.GREEN);
            sphere.setMaterial(material);
            sphere.setTranslateX(animal.x);
            sphere.setTranslateY(animal.y);
            root.getChildren().add(sphere);
            animalShapes.put(animal, sphere);
        }
    }

    private void drawPlants(Group root) {
        for (Plant plant : controller.getPlants()) {
            Rectangle rectangle = new Rectangle(10, 10);
            rectangle.setFill(Color.BLUE);
            rectangle.setTranslateX(plant.x);
            rectangle.setTranslateY(plant.y);
            root.getChildren().add(rectangle);
            plantShapes.put(plant, rectangle);
        }
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
