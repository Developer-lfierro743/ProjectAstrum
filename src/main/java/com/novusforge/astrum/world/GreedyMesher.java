package com.novusforge.astrum.world;

public class GreedyMesher {

    private static final int[][][] FACE_OFFSETS = {
        {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}},
        {{0, 0, 1}, {0, 0, -1}, {0, 0, 1}, {0, 0, -1}, {1, 0, 0}, {-1, 0, 0}}
    };

    private static final int[][] QUAD_INDICES = {
        {0, 1, 2, 0, 2, 3}
    };

    private static final float[] BLOCK_COLORS = {
        0.0f, 0.0f, 0.0f,
        0.6f, 0.6f, 0.6f,
        0.55f, 0.35f, 0.2f,
        0.2f, 0.6f, 0.2f,
        0.3f, 0.5f, 0.8f,
        0.5f, 0.3f, 0.1f,
    };

    public static ChunkMesh generateMesh(Chunk chunk, ChunkManager manager, int cx, int cz) {
        ChunkMesh mesh = new ChunkMesh();
        int baseX = cx * Chunk.SIZE;
        int baseZ = cz * Chunk.SIZE;

        for (int face = 0; face < 6; face++) {
            int[] off = FACE_OFFSETS[0][face];
            int d = face % 2 == 0 ? 1 : -1;

            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int x = 0; x < Chunk.SIZE; x++) {
                    for (int z = 0; z < Chunk.SIZE; z++) {
                        short current = chunk.getBlock(x, y, z);
                        if (current == 0) {
                            continue;
                        }

                        // Skip if already processed (block behind is same)
                        int px = x - off[0];
                        int py = y - off[1];
                        int pz = z - off[2];
                        if (px >= 0 && px < Chunk.SIZE && 
                            py >= 0 && py < Chunk.SIZE && 
                            pz >= 0 && pz < Chunk.SIZE) {
                            if (chunk.getBlock(px, py, pz) == current) {
                                continue;
                            }
                        }

                        int w = 1;
                        while (x + w < Chunk.SIZE &&
                               chunk.getBlock(x + w, y, z) == current &&
                               shouldMerge(chunk, x + w, y, z, x + w - 1, y, z, face, manager)) {
                            w++;
                        }

                        int h = 1;
                        boolean canExtend = true;
                        while (y + h < Chunk.SIZE && canExtend) {
                            for (int i = 0; i < w; i++) {
                                short block = chunk.getBlock(x + i, y + h, z);
                                if (block != current ||
                                    !shouldMerge(chunk, x + i, y + h, z, x + i, y + h - 1, z, face, manager)) {
                                    canExtend = false;
                                    break;
                                }
                            }
                            if (canExtend) h++;
                        }

                        addFaceToMesh(mesh, x + baseX, y, z + baseZ,
                                      w, h, face, current, d);

                        x += w - 1; // -1 because the for loop will increment x
                        break; // Break out of z loop, continue with next x
                    }
                }
            }
        }

        return mesh;
    }

    private static boolean shouldMerge(Chunk chunk, int x, int y, int z, int nx, int ny, int nz, int face, ChunkManager manager) {
        if (x < 0 || x >= Chunk.SIZE || y < 0 || y >= Chunk.SIZE || z < 0 || z >= Chunk.SIZE) {
            short neighbor = chunk.getBlock(x, y, z);
            return neighbor != 0;
        }
        short block = chunk.getBlock(x, y, z);
        return block != 0;
    }

    private static void addFaceToMesh(ChunkMesh mesh, int x, int y, int z, int w, int h, int face, short blockType, int d) {
        float[] color = getBlockColor(blockType);
        float[] vertices = new float[24];

        switch (face) {
            case 0:
                for (int i = 0; i < 4; i++) {
                    int vi = i * 6;
                    vertices[vi] = x + (i < 2 ? w : 0);
                    vertices[vi + 1] = y + (i % 2 == 0 ? 0 : h);
                    vertices[vi + 2] = z + 1;
                    vertices[vi + 3] = color[0];
                    vertices[vi + 4] = color[1];
                    vertices[vi + 5] = color[2];
                }
                break;
            case 1:
                for (int i = 0; i < 4; i++) {
                    int vi = i * 6;
                    vertices[vi] = x + (i < 2 ? 0 : -w);
                    vertices[vi + 1] = y + (i % 2 == 0 ? 0 : h);
                    vertices[vi + 2] = z - 1;
                    vertices[vi + 3] = color[0] * 0.8f;
                    vertices[vi + 4] = color[1] * 0.8f;
                    vertices[vi + 5] = color[2] * 0.8f;
                }
                break;
            case 2:
                for (int i = 0; i < 4; i++) {
                    int vi = i * 6;
                    vertices[vi] = x + (i < 2 ? 0 : w);
                    vertices[vi + 1] = y + h;
                    vertices[vi + 2] = z + (i % 2 == 0 ? 0 : h);
                    vertices[vi + 3] = color[0] * 1.1f;
                    vertices[vi + 4] = color[1] * 1.1f;
                    vertices[vi + 5] = color[2] * 1.1f;
                }
                break;
            case 3:
                for (int i = 0; i < 4; i++) {
                    int vi = i * 6;
                    vertices[vi] = x + (i < 2 ? 0 : w);
                    vertices[vi + 1] = y - 1;
                    vertices[vi + 2] = z + (i % 2 == 0 ? h : 0);
                    vertices[vi + 3] = color[0] * 0.6f;
                    vertices[vi + 4] = color[1] * 0.6f;
                    vertices[vi + 5] = color[2] * 0.6f;
                }
                break;
            case 4:
                for (int i = 0; i < 4; i++) {
                    int vi = i * 6;
                    vertices[vi] = x + (i < 2 ? w : 0);
                    vertices[vi + 1] = y + (i % 2 == 0 ? 0 : h);
                    vertices[vi + 2] = z + h;
                    vertices[vi + 3] = color[0] * 0.9f;
                    vertices[vi + 4] = color[1] * 0.9f;
                    vertices[vi + 5] = color[2] * 0.9f;
                }
                break;
            case 5:
                for (int i = 0; i < 4; i++) {
                    int vi = i * 6;
                    vertices[vi] = x + (i < 2 ? 0 : w);
                    vertices[vi + 1] = y + (i % 2 == 0 ? h : 0);
                    vertices[vi + 2] = z - 1;
                    vertices[vi + 3] = color[0] * 0.7f;
                    vertices[vi + 4] = color[1] * 0.7f;
                    vertices[vi + 5] = color[2] * 0.7f;
                }
                break;
        }

        if (isTransparent(blockType)) {
            mesh.addTransparentQuad(vertices, QUAD_INDICES[0]);
        } else {
            mesh.addOpaqueQuad(vertices, QUAD_INDICES[0]);
        }
    }

    private static float[] getBlockColor(short blockType) {
        switch (blockType) {
            case 1: return new float[]{0.6f, 0.6f, 0.6f};
            case 2: return new float[]{0.55f, 0.35f, 0.2f};
            case 3: return new float[]{0.2f, 0.6f, 0.2f};
            case 4: return new float[]{0.3f, 0.5f, 0.8f};
            case 5: return new float[]{0.5f, 0.3f, 0.1f};
            default: return new float[]{1.0f, 1.0f, 1.0f};
        }
    }

    private static boolean isTransparent(short blockType) {
        return blockType == 0;
    }
}
