package com.novusforge.astrum.common.infiniminer;

import java.util.Random;

/**
 * Port of Infiniminer's CaveGenerator class.
 * Originally defined in InfiniminerServer/CaveGenerator.cs
 */
public class InfiniminerCaveGenerator {
    private static final Random randGen = new Random();

    public static BlockType[][][] generateCaveSystem(int size, boolean includeLava, long oreFactor) {
        float gradientStrength = randGen.nextFloat();
        BlockType[][][] caveData = generateConstant(size, BlockType.DIRT);

        // Add ore.
        float[][][] oreNoise = generatePerlinNoise(32);
        oreNoise = interpolateData(oreNoise, 32, size);
        for (int i = 0; i < oreFactor; i++) {
            paintWithRandomWalk(caveData, oreNoise, size, 1, BlockType.ORE, false);
        }

        // Add minerals.
        addGold(caveData, size);
        addDiamond(caveData, size);

        // Level off everything above ground level, replacing it with mountains.
        float[][][] mountainNoise = generatePerlinNoise(32);
        mountainNoise = interpolateData(mountainNoise, 32, size);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z <= InfiniminerConstants.GROUND_LEVEL * 2; z++) {
                    mountainNoise[x][y][z] = z < 3 ? 0 : Math.min(1.0f, (float) z / (InfiniminerConstants.GROUND_LEVEL * 2));
                }
            }
        }
        float[][][] gradient = generateGradient(size);
        addDataTo(mountainNoise, gradient, size, 0.1f, 0.9f);
        BlockType[][][] mountainData = generateConstant(size, BlockType.NONE);
        int numMountains = randGen.nextInt(size, size * 3 + 1);
        for (int i = 0; i < numMountains; i++) {
            paintWithRandomWalk(mountainData, mountainNoise, size, randGen.nextInt(2, 4), BlockType.DIRT, false);
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z <= InfiniminerConstants.GROUND_LEVEL; z++) {
                    if (mountainData[x][y][z] == BlockType.NONE) {
                        caveData[x][y][z] = BlockType.NONE;
                    }
                }
            }
        }

        // Carve some caves into the ground.
        float[][][] caveNoise = generatePerlinNoise(32);
        caveNoise = interpolateData(caveNoise, 32, size);
        gradient = generateGradient(size);
        addDataTo(caveNoise, gradient, size, 1 - gradientStrength, gradientStrength);
        int cavesToCarve = randGen.nextInt(size / 8, size / 2 + 1);
        for (int i = 0; i < cavesToCarve; i++) {
            paintWithRandomWalk(caveData, caveNoise, size, randGen.nextInt(1, 3), BlockType.NONE, false);
        }

        // Carve the map into a sphere.
        float[][][] sphereGradient = generateRadialGradient(size);
        cavesToCarve = randGen.nextInt(size / 8, size / 2 + 1);
        for (int i = 0; i < cavesToCarve; i++) {
            paintWithRandomWalk(caveData, sphereGradient, size, randGen.nextInt(1, 3), BlockType.NONE, true);
        }

        // Add rocks.
        addRocks(caveData, size);

        // Add lava.
        if (includeLava) {
            addLava(caveData, size);
        }

        return caveData;
    }

    public static void addRocks(BlockType[][][] data, int size) {
        int numRocks = randGen.nextInt(size, 2 * size + 1);
        for (int i = 0; i < numRocks; i++) {
            int x = randGen.nextInt(0, size);
            int y = randGen.nextInt(0, size);

            float zf = 0;
            for (int j = 0; j < 4; j++) {
                zf += randGen.nextFloat();
            }
            zf /= 2;
            zf = 1 - Math.abs(zf - 1);
            int z = (int) (zf * size);

            int rockSize = (int) ((randGen.nextFloat() + randGen.nextFloat() + randGen.nextFloat() + randGen.nextFloat()) / 4 * 8);

            paintAtPoint(data, x, y, z, size, rockSize, BlockType.ROCK);
        }
    }

    public static void addLava(BlockType[][][] data, int size) {
        int numFlows = randGen.nextInt(size / 16, size / 2 + 1);
        while (numFlows > 0) {
            int x = randGen.nextInt(0, size);
            int y = randGen.nextInt(0, size);

            float zf = 0;
            for (int j = 0; j < 4; j++) {
                zf += randGen.nextFloat();
            }
            zf /= 2;
            zf = 1 - Math.abs(zf - 1);
            int z = (int) (zf * size);

            if (data[x][y][z] == BlockType.NONE && z + 1 < size - 1) {
                data[x][y][z] = BlockType.ROCK;
                data[x][y][z + 1] = BlockType.LAVA;
                numFlows -= 1;
            }
        }
    }

    public static void addDiamond(BlockType[][][] data, int size) {
        int numDiamonds = 16;
        for (int i = 0; i < numDiamonds; i++) {
            int x = randGen.nextInt(0, size);
            int y = randGen.nextInt(0, size);

            float zf = 0;
            for (int j = 0; j < 4; j++) {
                zf += randGen.nextFloat();
            }
            zf /= 2;
            zf = 1 - Math.abs(zf - 1);
            int z = (int) (zf * size);

            data[x][y][z] = BlockType.DIAMOND;
        }
    }

    public static void addGold(BlockType[][][] data, int size) {
        int numVeins = 16;
        for (int i = 0; i < numVeins; i++) {
            int fieldLength = randGen.nextInt(size / 3, size + 1);
            float x = randGen.nextInt(0, size);
            float y = randGen.nextInt(0, size);

            float zf = 0;
            for (int j = 0; j < 4; j++) {
                zf += randGen.nextFloat();
            }
            zf /= 2;
            zf = 1 - Math.abs(zf - 1);
            float z = zf * size;

            float dx = randGen.nextFloat() * 2 - 1;
            float dy = randGen.nextFloat() * 2 - 1;
            float dz = randGen.nextFloat() * 2 - 1;
            float dl = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= dl;
            dy /= dl;
            dz /= dl;

            for (int j = 0; j < fieldLength; j++) {
                x += dx;
                y += dy;
                z += dz;
                if (x >= 0 && y >= 0 && z >= 0 && x < size && y < size && z < size) {
                    data[(int) x][(int) y][(int) z] = BlockType.GOLD;
                }
                int tx = 0, ty = 0, tz = 0;
                switch (randGen.nextInt(0, 3)) {
                    case 0 -> tx += 1;
                    case 1 -> ty += 1;
                    case 2 -> tz += 1;
                }
                if (x + tx >= 0 && y + ty >= 0 && z + tz >= 0 && x + tx < size && y + ty < size && z + tz < size) {
                    data[(int) x + tx][(int) y + ty][(int) z + tz] = BlockType.GOLD;
                }
            }
        }
    }

    public static float[][][] generateNoise(int size, float magnitude) {
        float[][][] noiseArray = new float[size][size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    noiseArray[x][y][z] = randGen.nextFloat() * magnitude;
                }
            }
        }
        return noiseArray;
    }

    public static float[][][] generatePerlinNoise(int size) {
        float[][][] data = new float[size][size][size];
        float[][][] noise;
        for (int f = 4; f < 32; f *= 2) {
            noise = generateNoise(f, 2.0f / f);
            noise = interpolateData(noise, f, size);
            addDataTo(data, noise, size);
        }
        return data;
    }

    public static void paintWithRandomWalk(BlockType[][][] caveData, float[][][] noiseData, int size, int paintRadius, BlockType paintValue, boolean dontStopAtEdge) {
        int x = randGen.nextInt(0, size);
        int y = randGen.nextInt(0, size);
        int z = randGen.nextInt(0, size);

        if (z < size / 50) {
            z = 0;
        }

        int count = 0;

        while (dontStopAtEdge == false || count < size) {
            float oldNoise = noiseData[x][y][z];

            paintAtPoint(caveData, x, y, z, size, paintRadius + 1, paintValue);
            int dx = randGen.nextInt(0, paintRadius * 2 + 1) - paintRadius;
            int dy = randGen.nextInt(0, paintRadius * 2 + 1) - paintRadius;
            int dz = randGen.nextInt(0, paintRadius * 2 + 1) - paintRadius;

            x += dx;
            y += dy;
            z += dz;

            if (x < 0 || y < 0 || x >= size || y >= size || z >= size) {
                if (dontStopAtEdge) {
                    count += 1;
                    x = Math.clamp(x, 0, size - 1);
                    y = Math.clamp(y, 0, size - 1);
                    z = Math.clamp(z, 0, size - 1);
                } else {
                    break;
                }
            }

            if (z < 0) {
                z = 0;
            }

            float newNoise = noiseData[x][y][z];

            if (newNoise > oldNoise) {
                paintAtPoint(caveData, x, y, z, size, paintRadius + 1, paintValue);
                x += dx;
                y += dy;
                z += dz;

                if (x < 0 || y < 0 || x >= size || y >= size || z >= size) {
                    if (dontStopAtEdge) {
                        count += 1;
                        x = Math.clamp(x, 0, size - 1);
                        y = Math.clamp(y, 0, size - 1);
                        z = Math.clamp(z, 0, size - 1);
                    } else {
                        break;
                    }
                }

                if (z < 0) {
                    z = 0;
                }
            }
        }
    }

    public static void paintAtPoint(BlockType[][][] caveData, int x, int y, int z, int size, int paintRadius, BlockType paintValue) {
        for (int dx = -paintRadius; dx <= paintRadius; dx++) {
            for (int dy = -paintRadius; dy <= paintRadius; dy++) {
                for (int dz = -paintRadius; dz <= paintRadius; dz++) {
                    if (x + dx >= 0 && y + dy >= 0 && z + dz >= 0 && x + dx < size && y + dy < size && z + dz < size) {
                        if (dx * dx + dy * dy + dz * dz < paintRadius * paintRadius) {
                            caveData[x + dx][y + dy][z + dz] = paintValue;
                        }
                    }
                }
            }
        }
    }

    public static BlockType[][][] generateConstant(int size, BlockType value) {
        BlockType[][][] data = new BlockType[size][size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    data[x][y][z] = value;
                }
            }
        }
        return data;
    }

    public static float[][][] generateGradient(int size) {
        float[][][] data = new float[size][size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    data[x][y][z] = (float) z / size;
                }
            }
        }
        return data;
    }

    public static float[][][] generateRadialGradient(int size) {
        float[][][] data = new float[size][size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    float dist = (float) Math.sqrt(Math.pow(x - size / 2.0, 2) + Math.pow(y - size / 2.0, 2));
                    data[x][y][z] = Math.clamp(dist / size * 0.3f * (float) z / size, 0.0f, 1.0f);
                }
            }
        }
        return data;
    }

    public static void addDataTo(float[][][] dataDst, float[][][] dataSrc, int size, float scalarDst, float scalarSrc) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    dataDst[x][y][z] = Math.clamp(dataDst[x][y][z] * scalarDst + dataSrc[x][y][z] * scalarSrc, 0.0f, 1.0f);
                }
            }
        }
    }

    public static void addDataTo(float[][][] dataDst, float[][][] dataSrc, int size) {
        addDataTo(dataDst, dataSrc, size, 1.0f, 1.0f);
    }

    public static float[][][] interpolateData(float[][][] dataIn, int sizeIn, int sizeOut) {
        if (sizeOut <= sizeIn) throw new IllegalArgumentException("sizeOut must be greater than sizeIn");
        if (sizeOut % sizeIn != 0) throw new IllegalArgumentException("sizeOut must be a multiple of sizeIn");

        float[][][] dataOut = new float[sizeOut][sizeOut][sizeOut];
        int r = sizeOut / sizeIn;

        for (int x = 0; x < sizeOut; x++) {
            for (int y = 0; y < sizeOut; y++) {
                for (int z = 0; z < sizeOut; z++) {
                    int xIn0 = x / r, yIn0 = y / r, zIn0 = z / r;
                    int xIn1 = xIn0 + 1, yIn1 = yIn0 + 1, zIn1 = zIn0 + 1;
                    if (xIn1 >= sizeIn) xIn1 = 0;
                    if (yIn1 >= sizeIn) yIn1 = 0;
                    if (zIn1 >= sizeIn) zIn1 = 0;

                    float v000 = dataIn[xIn0][yIn0][zIn0];
                    float v100 = dataIn[xIn1][yIn0][zIn0];
                    float v010 = dataIn[xIn0][yIn1][zIn0];
                    float v110 = dataIn[xIn1][yIn1][zIn0];
                    float v001 = dataIn[xIn0][yIn0][zIn1];
                    float v101 = dataIn[xIn1][yIn0][zIn1];
                    float v011 = dataIn[xIn0][yIn1][zIn1];
                    float v111 = dataIn[xIn1][yIn1][zIn1];

                    float xS = ((float) (x % r)) / r;
                    float yS = ((float) (y % r)) / r;
                    float zS = ((float) (z % r)) / r;

                    dataOut[x][y][z] = v000 * (1 - xS) * (1 - yS) * (1 - zS) +
                            v100 * xS * (1 - yS) * (1 - zS) +
                            v010 * (1 - xS) * yS * (1 - zS) +
                            v001 * (1 - xS) * (1 - yS) * zS +
                            v101 * xS * (1 - yS) * zS +
                            v011 * (1 - xS) * yS * zS +
                            v110 * xS * yS * (1 - zS) +
                            v111 * xS * yS * zS;
                }
            }
        }
        return dataOut;
    }
}
