package com.novusforge.astrum.common.world;

import org.joml.Vector3i;

/**
 * Utility methods for chunk-local coordinate math.
 * Centered around the 32x32x32 chunk size defined in "The Formula".
 */
public class ChunkMath {

    /**
     * Converts world coordinates to chunk coordinates.
     */
    public static Vector3i worldToChunk(int x, int y, int z) {
        return new Vector3i(
            Math.floorDiv(x, VoxelData.CHUNK_SIZE),
            Math.floorDiv(y, VoxelData.CHUNK_SIZE),
            Math.floorDiv(z, VoxelData.CHUNK_SIZE)
        );
    }

    /**
     * Converts world coordinates to local coordinates within a chunk (0-31).
     */
    public static Vector3i worldToLocal(int x, int y, int z) {
        return new Vector3i(
            Math.floorMod(x, VoxelData.CHUNK_SIZE),
            Math.floorMod(y, VoxelData.CHUNK_SIZE),
            Math.floorMod(z, VoxelData.CHUNK_SIZE)
        );
    }

    /**
     * Converts a specific coordinate to its local chunk counterpart.
     */
    public static int toLocal(int coord) {
        return Math.floorMod(coord, VoxelData.CHUNK_SIZE);
    }

    /**
     * Converts a specific coordinate to its chunk counterpart.
     */
    public static int toChunk(int coord) {
        return Math.floorDiv(coord, VoxelData.CHUNK_SIZE);
    }

    /**
     * Calculates the 1D array index for a local 3D coordinate.
     */
    public static int getIndex(int localX, int localY, int localZ) {
        return (localX * VoxelData.CHUNK_SIZE + localY) * VoxelData.CHUNK_SIZE + localZ;
    }

    /**
     * Converts chunk and local coordinates back to world coordinates.
     */
    public static Vector3i toWorld(Vector3i chunkPos, int lx, int ly, int lz) {
        return new Vector3i(
            chunkPos.x * VoxelData.CHUNK_SIZE + lx,
            chunkPos.y * VoxelData.CHUNK_SIZE + ly,
            chunkPos.z * VoxelData.CHUNK_SIZE + lz
        );
    }
}
