package io.github.rehanatron;

import java.util.HashMap;
import java.util.Map;

import io.github.rehanatron.controllers.*;
import io.github.rehanatron.models.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    private PerspectiveCamera camera;

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

        setupCamera(scene);
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

    private void setupCamera(Scene scene) {
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-800);
        camera.setTranslateY(-200);
        camera.setTranslateX(500);
        camera.setFarClip(5000); // Fix disappearing objects

        scene.setCamera(camera);

        final double[] mousePosX = {0};
        final double[] mousePosY = {0};
        final double[] angleX = {0};
        final double[] angleY = {0};

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            mousePosX[0] = event.getSceneX();
            mousePosY[0] = event.getSceneY();
        });

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            double deltaX = event.getSceneX() - mousePosX[0];
            double deltaY = event.getSceneY() - mousePosY[0];

            if (event.getButton() == MouseButton.PRIMARY) {
                // Move camera (left click)
                camera.setTranslateX(camera.getTranslateX() - deltaX * 0.5);
                camera.setTranslateY(camera.getTranslateY() - deltaY * 0.5);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                // Rotate camera (right click)
                angleY[0] += deltaX * 0.5;
                angleX[0] -= deltaY * 0.5;

                camera.setRotationAxis(javafx.geometry.Point3D.ZERO.add(1, 0, 0)); // X-axis
                camera.setRotate(angleX[0]);
                camera.setRotationAxis(javafx.geometry.Point3D.ZERO.add(0, 1, 0)); // Y-axis
                camera.setRotate(angleY[0]);
            }

            mousePosX[0] = event.getSceneX();
            mousePosY[0] = event.getSceneY();
        });

        scene.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            camera.setTranslateZ(camera.getTranslateZ() + delta);
        });
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

    public static void main(String[] args) {
        launch(args);
    }
}
