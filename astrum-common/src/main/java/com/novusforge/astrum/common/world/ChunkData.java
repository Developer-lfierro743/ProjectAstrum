package com.novusforge.astrum.common.world;

/**
 * High-performance chunk data storage using a 1D array as specified in the 2026 Formula.
 * Optimized for cache locality and minimal memory overhead.
 */
public class ChunkData {
    public static final int SIZE = 32;
    public static final int TOTAL_BLOCKS = SIZE * SIZE * SIZE;

    // Using short[] to allow for >256 BlockIDs (Formula Part 3: "exceeds upgrade chunk data blocks as shorts")
    private final short[] blocks;

    public ChunkData() {
        this.blocks = new short[TOTAL_BLOCKS];
    }

    /**
     * Maps 3D coordinates to a 1D index: (y * SIZE * SIZE) + (z * SIZE) + x
     * This order is optimized for vertical-first column processing.
     */
    private int getIndex(int x, int y, int z) {
        return (y << 10) | (z << 5) | x; // Optimized bit-shifting for 32x32x32
    }

    public void setBlock(int x, int y, int z, short blockId) {
        if (isValid(x, y, z)) {
            blocks[getIndex(x, y, z)] = blockId;
        }
    }

    public short getBlock(int x, int y, int z) {
        if (!isValid(x, y, z)) return 0;
        return blocks[getIndex(x, y, z)];
    }

    private boolean isValid(int x, int y, int z) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE;
    }

    public short[] getRawData() {
        return blocks;
    }
}
