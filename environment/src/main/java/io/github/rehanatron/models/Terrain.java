package io.github.rehanatron.models;

import java.util.List;

public class Terrain {
    private int width;
    private int length;
    private int[][] resources;
    private int[][] elevation;

    public Terrain(int width, int length) {
        this.width = width;
        this.length = length;
        this.resources = new int[width][length];
        this.elevation = new int[width][length];
        initializeResources();
        initializeElevation();
    }

    private void initializeResources() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                resources[i][j] = (int) (Math.random() * 50);
            }
        }
    }

    private void initializeElevation() {
        // Create a noise generator for natural terrain generation
        SimplexNoise noiseGenerator = new SimplexNoise();

        // Control parameters for Minecraft plains-like terrain
        double scale = 0.03; // Controls the size of terrain features
        int baseHeight = 0; // Base elevation level
        int hillHeight = 5; // Small hills
        int valleyDepth = -3; // Shallow valleys

        // Create base terrain with small variations
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                // Create primary noise for general terrain shape
                double primaryNoise = noiseGenerator.noise(i * scale, j * scale);

                // Create secondary noise for small details
                double detailNoise = noiseGenerator.noise(i * scale * 3, j * scale * 3) * 0.3;

                // Combine noise values
                double combinedNoise = primaryNoise + detailNoise;

                // Plains are mostly flat with occasional gentle hills
                if (combinedNoise > 0.3) {
                    // Small hills
                    elevation[i][j] = baseHeight + (int) (hillHeight * (combinedNoise - 0.3));
                } else if (combinedNoise < -0.3) {
                    // Shallow valleys or water pools
                    elevation[i][j] = baseHeight + (int) (valleyDepth * Math.abs(combinedNoise + 0.3));
                } else {
                    // Flat plains with very minor variations
                    elevation[i][j] = baseHeight + (int) (combinedNoise * 2);
                }
            }
        }

        // Add some occasional terrain features like small hills or ponds
        addTerrainFeatures();
    }

    // Add occasional terrain features to break monotony
    private void addTerrainFeatures() {
        java.util.Random rand = new java.util.Random();

        // Add a few scattered hills/ponds
        int numFeatures = width * length / 200; // Approximately one feature per 200 blocks

        for (int n = 0; n < numFeatures; n++) {
            // Random feature location
            int centerX = rand.nextInt(width);
            int centerY = rand.nextInt(length);

            // Random feature size
            int featureSize = 3 + rand.nextInt(5); // Size between 3-7 blocks

            // Random feature type (hill or pond)
            boolean isHill = rand.nextBoolean();
            int featureHeight = isHill ? 5 + rand.nextInt(3) : -4 - rand.nextInt(2);

            // Create the feature with smooth falloff
            for (int i = Math.max(0, centerX - featureSize); i < Math.min(width, centerX + featureSize); i++) {
                for (int j = Math.max(0, centerY - featureSize); j < Math.min(length, centerY + featureSize); j++) {
                    // Calculate distance from center (squared)
                    double distSq = Math.pow(i - centerX, 2) + Math.pow(j - centerY, 2);

                    if (distSq < featureSize * featureSize) {
                        // Smooth falloff from center
                        double falloff = 1.0 - Math.sqrt(distSq) / featureSize;

                        // Apply feature height with falloff
                        elevation[i][j] += (int) (featureHeight * falloff * falloff);
                    }
                }
            }
        }
    }

    // SimplexNoise class for generating coherent noise
    class SimplexNoise {
        private static final int[][] grad3 = {
                { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 },
                { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 },
                { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }
        };

        private int[] perm = new int[512];

        public SimplexNoise() {
            // Initialize permutation array with random values
            java.util.Random rand = new java.util.Random();
            for (int i = 0; i < 256; i++) {
                perm[i] = i;
            }

            // Fisher-Yates shuffle
            for (int i = 0; i < 256; i++) {
                int j = rand.nextInt(256);
                int temp = perm[i];
                perm[i] = perm[j];
                perm[j] = temp;
            }

            // Duplicate for easy indexing
            for (int i = 0; i < 256; i++) {
                perm[i + 256] = perm[i];
            }
        }

        private double dot(int[] g, double x, double y) {
            return g[0] * x + g[1] * y;
        }

        public double noise(double xin, double yin) {
            // Noise implementation using Perlin's simplex noise algorithm
            double n0, n1, n2; // Noise contributions from the three corners

            // Skew input space to determine simplex cell
            final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
            double s = (xin + yin) * F2;
            int i = fastfloor(xin + s);
            int j = fastfloor(yin + s);

            final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
            double t = (i + j) * G2;
            double X0 = i - t;
            double Y0 = j - t;
            double x0 = xin - X0; // The x,y distances from the cell origin
            double y0 = yin - Y0;

            // Determine which simplex we are in
            int i1, j1;
            if (x0 > y0) {
                i1 = 1;
                j1 = 0; // Lower triangle
            } else {
                i1 = 0;
                j1 = 1; // Upper triangle
            }

            double x1 = x0 - i1 + G2;
            double y1 = y0 - j1 + G2;
            double x2 = x0 - 1.0 + 2.0 * G2;
            double y2 = y0 - 1.0 + 2.0 * G2;

            // Work out the hashed gradient indices
            int ii = i & 255;
            int jj = j & 255;
            int gi0 = perm[ii + perm[jj]] % 12;
            int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
            int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;

            // Calculate the contribution from the three corners
            double t0 = 0.5 - x0 * x0 - y0 * y0;
            if (t0 < 0) {
                n0 = 0.0;
            } else {
                t0 *= t0;
                n0 = t0 * t0 * dot(grad3[gi0], x0, y0);
            }

            double t1 = 0.5 - x1 * x1 - y1 * y1;
            if (t1 < 0) {
                n1 = 0.0;
            } else {
                t1 *= t1;
                n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
            }

            double t2 = 0.5 - x2 * x2 - y2 * y2;
            if (t2 < 0) {
                n2 = 0.0;
            } else {
                t2 *= t2;
                n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
            }

            // Add contributions from each corner to get final noise value
            // Scale to return value between -1 and 1
            return 70.0 * (n0 + n1 + n2);
        }

        private int fastfloor(double x) {
            return x > 0 ? (int) x : (int) x - 1;
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

    public int getElevationAt(int x, int y) {
        if (isValidPosition(x, y)) {
            return elevation[x][y];
        }
        return 0;
    }
}
