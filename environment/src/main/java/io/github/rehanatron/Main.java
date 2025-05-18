package io.github.rehanatron;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import io.github.rehanatron.controllers.*;
import io.github.rehanatron.models.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

public class Main extends Application {
    private SimulationController controller;
    private Map<Animal, Node> animalShapes;
    private Map<Plant, Node> plantShapes;
    private Group terrainGroup;
    private Group UIgroup;
    private PerspectiveCamera camera;
    private String selectedAnimalType = null; // Track selected animal type

    @Override
    public void start(Stage primaryStage) {
        controller = new SimulationController(100, 100);
        animalShapes = new HashMap<>();
        plantShapes = new HashMap<>();
        terrainGroup = new Group();
        UIgroup = new Group();

        // JavaFX Group for rendering
        Group root = new Group();
        root.getChildren().addAll(terrainGroup);
        root.getChildren().addAll(UIgroup);
        Scene scene = new Scene(root, 1000, 720, true);
        scene.setFill(Color.rgb(24, 174, 255));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ecosystem Simulator");

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case S:
                    saveGame("savegame.ser");
                    break;
                case L:
                    loadGame("savegame.ser", root);
                    break;
                default:
                    break;
            }
        });

        setupCamera(scene);
        drawUI();
        drawTerrain();

        // Add click handler for spawning animals
        scene.setOnMouseClicked(event -> {
            if (selectedAnimalType != null && event.getButton() == MouseButton.PRIMARY) {
                Object targetData = ((Node) event.getTarget()).getUserData();
                if ("GROUND".equals(targetData)) {
                    double x = event.getX();
                    double y = event.getY();
                    double z = 0;
                    Animal newAnimal;
                    Group animalNode;
                    if (selectedAnimalType.equals("Herbivore")) {
                        z = -10;
                        newAnimal = new Herbivore((int) x, (int) y, (int) z, 100, 1, "Goat");
                        animalNode = loadModel(getClass().getResource("/Goat.obj"), selectedAnimalType);
                        animalNode.setRotationAxis(new Point3D(1, 0, 0));
                        animalNode.setRotate(90);
                        animalNode.setScaleX(0.5);
                        animalNode.setScaleY(0.5);
                        animalNode.setScaleZ(0.5);
                    } else {
                        newAnimal = new Carnivore((int) x, (int) y, 50, 100, 2, "Lion");
                        animalNode = loadModel(getClass().getResource("/Lion.obj"), selectedAnimalType);
                        animalNode.setScaleX(0.3);
                        animalNode.setScaleZ(0.3);
                    }
                    controller.addAnimal(newAnimal);
                    animalNode.setTranslateX(x);
                    animalNode.setTranslateY(y);
                    animalNode.setTranslateZ(z);

                    // Add click handler for the animal
                    animalNode.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            showAnimalDetails(newAnimal);
                        }
                    });

                    root.getChildren().add(animalNode);
                    animalShapes.put(newAnimal, animalNode);
                }
            }
        });

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
        // Position camera at the edge of the terrain looking horizontally
        camera.setTranslateZ(-50);
        camera.setTranslateY(500);
        camera.setTranslateX(500);
        camera.setFarClip(5000);

        // Set initial rotation to look horizontally
        UIgroup.getChildren().add(camera);
        camera.setRotationAxis(Rotate.X_AXIS); // X-axis
        camera.setRotate(90); // Rotate 90 degrees on X-axis

        scene.setCamera(camera);

        final double[] mousePosX = { 0 };
        final double[] mousePosY = { 0 };

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            mousePosX[0] = event.getSceneX();
            mousePosY[0] = event.getSceneY();
        });

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            double deltaX = event.getSceneX() - mousePosX[0];
            double deltaY = event.getSceneY() - mousePosY[0];

            if (event.getButton() == MouseButton.PRIMARY) {
                // Move camera (left click)
                UIgroup.setTranslateX(UIgroup.getTranslateX() - deltaX * 0.5);
                UIgroup.setTranslateY(UIgroup.getTranslateY() - deltaY * 0.5);
            }

            mousePosX[0] = event.getSceneX();
            mousePosY[0] = event.getSceneY();
        });

        scene.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            UIgroup.setTranslateZ(UIgroup.getTranslateZ() + delta);
        });
    }

    private void drawTerrain() {
        Terrain terrain = controller.getTerrain();
        int cellSize = 10; // Adjust for visualization
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < terrain.getWidth(); i++) {
            for (int j = 0; j < terrain.getlength(); j++) {
                Rectangle cell = new Rectangle(i * cellSize, j * cellSize, cellSize, cellSize);
                cell.setUserData("GROUND");
                cell.setFill(Color.rgb(0, 100, 0));
                terrainGroup.getChildren().add(cell);
                // Randomly generate a plant with 5% probability
                if (rand.nextDouble() < 0.05) {
                    Plant plant = new Plant(i * cellSize + 4, j * cellSize + 4, 0, 100, "Bush");
                    controller.getPlants().add(plant);
                    Node plantNode;
                    if (rand.nextDouble() < 0.25) {
                        plantNode = loadModel(getClass().getResource("/Bush.obj"), "Plant");
                        plantNode.setTranslateZ(-2);
                    } else {
                        plantNode = loadModel(getClass().getResource("/Tree.obj"), "Plant");
                        plantNode.setTranslateZ(-20);
                    }
                    plantNode.setTranslateX(i * cellSize);
                    plantNode.setTranslateY(j * cellSize);
                    plantNode.setScaleX(2);
                    plantNode.setScaleY(2);
                    plantNode.setScaleZ(2);
                    plantNode.setRotationAxis(new Point3D(1, 0, 0));
                    plantNode.setRotate(90);
                    terrainGroup.getChildren().add(plantNode);
                    plantShapes.put(plant, plantNode);
                }
            }
        }
    }

    private void drawUI() {
        Rectangle herbivoreButton = new Rectangle(20, 20);
        Rectangle carnivoreButton = new Rectangle(20, 20);
        Rectangle cancelButton = new Rectangle(20, 20);

        herbivoreButton.setTranslateX(320);
        herbivoreButton.setTranslateY(10);
        herbivoreButton.setTranslateZ(-180);
        herbivoreButton.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        carnivoreButton.setTranslateX(350);
        carnivoreButton.setTranslateY(10);
        carnivoreButton.setTranslateZ(-180);
        carnivoreButton.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        cancelButton.setTranslateX(650);
        cancelButton.setTranslateY(10);
        cancelButton.setTranslateZ(-180);
        cancelButton.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        carnivoreButton.setOnMouseClicked(e -> {
            selectedAnimalType = "Carnivore";
        });
        herbivoreButton.setOnMouseClicked(e -> {
            selectedAnimalType = "Herbivore";
        });
        cancelButton.setOnMouseClicked(e -> {
            selectedAnimalType = null;
        });

        herbivoreButton.setFill(Color.rgb(0, 125, 0));
        carnivoreButton.setFill(Color.rgb(250, 0, 0));
        cancelButton.setFill(Color.rgb(250, 0, 0));

        // Add buttons and labels to UI group
        UIgroup.getChildren().addAll(herbivoreButton, carnivoreButton, cancelButton);
    }

    private void showAnimalDetails(Animal animal) {
        // Create a simple dialog to show animal details
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Animal Details");
        alert.setHeaderText(animal.species);

        String content = String.format(
                "Type: %s\nEnergy: %d\nPosition: (%d, %d)\nStatus: %s",
                animal instanceof Carnivore ? "Carnivore" : "Herbivore",
                animal.getEnergy(),
                (int) animal.x,
                (int) animal.y,
                animal.isAlive() ? "Alive" : "Dead");

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateView() {
        // Update animal positions
        for (Map.Entry<Animal, Node> entry : animalShapes.entrySet()) {
            Animal animal = entry.getKey();
            Group node = (Group) entry.getValue();
            node.setTranslateX(animal.x);
            node.setTranslateY(animal.y);

            // Optionally, update color/material if needed
            if (!animal.isAlive()) {
                PhongMaterial material = new PhongMaterial(Color.GRAY);
                for (Node child : ((Group) node).getChildren()) {
                    if (child instanceof MeshView) {
                        ((MeshView) child).setMaterial(material);
                    }
                }
            }
        }

        for (Map.Entry<Plant, Node> entry : plantShapes.entrySet()) {
            Plant plant = entry.getKey();
            Group node = (Group) entry.getValue();
            // Optionally, update color/material if needed
            if (!plant.isAlive()) {
                PhongMaterial material = new PhongMaterial(Color.GRAY);
                for (Node child : ((Group) node).getChildren()) {
                    if (child instanceof MeshView) {
                        ((MeshView) child).setMaterial(material);
                    }
                }
            }
        }
    }

    private Group loadModel(URL url, String selectedAnimalType) {
        Group modelRoot = new Group();

        ObjModelImporter importer = new ObjModelImporter();
        importer.read(url);

        PhongMaterial material = new PhongMaterial();
        if (selectedAnimalType == "Carnivore") {
            material.setDiffuseColor(Color.GOLD);
        } else if (selectedAnimalType == "Herbivore") {
            material.setDiffuseColor(new Color(0.66, 0.3, 0, 1));
        } else {
            material.setDiffuseColor(new Color(0, 0.8, 0, 1));
        }

        for (MeshView view : importer.getImport()) {
            view.setMaterial(material);
            modelRoot.getChildren().addAll(view);
        }

        return modelRoot;
    }

    private void saveGame(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Animal animal : controller.getAnimals()) {
                writer.write(animal.toString());
                writer.newLine();
            }
            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame(String filename, Group root) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            controller = new SimulationController(100, 100);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String className = parts[0];
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                int energy = Integer.parseInt(parts[4]);
                int speed = (int) Double.parseDouble(parts[5]);

                Organism newOrganism;
                Group organismNode;

                if (className.endsWith("Herbivore")) {
                    newOrganism = new Herbivore(x, y, z, energy, speed, "Goat");
                    organismNode = loadModel(getClass().getResource("/Goat.obj"), "Herbivore");
                    organismNode.setRotationAxis(new Point3D(1, 0, 0));
                    organismNode.setRotate(90);
                    organismNode.setScaleX(0.5);
                    organismNode.setScaleY(0.5);
                    organismNode.setScaleZ(0.5);
                } else if (className.endsWith("Carnivore")) {
                    newOrganism = new Carnivore(x, y, z, energy, speed, "Lion");
                    organismNode = loadModel(getClass().getResource("/Lion.obj"), "Carnivore");
                    organismNode.setScaleX(0.3);
                    organismNode.setScaleZ(0.3);
                } else {
                    newOrganism = new Plant(x, y, z, energy, "Bush");
                    organismNode = loadModel(getClass().getResource("/Tree.obj"), "Plant");
                }

                organismNode.setTranslateX(x);
                organismNode.setTranslateY(y);
                organismNode.setTranslateZ(z);

                if (newOrganism instanceof Animal) {
                    // Add click handler for detail view
                    controller.addAnimal((Animal) newOrganism);
                    organismNode.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            showAnimalDetails((Animal) newOrganism);
                        }
                    });
                    animalShapes.put((Animal) newOrganism, organismNode);
                }
                root.getChildren().add(organismNode);
            }

            System.out.println("Game loaded from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
