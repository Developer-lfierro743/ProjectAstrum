package com.novusforge.astrum.infiniminer.engines;

import com.novusforge.astrum.infiniminer.BlockType;
import com.novusforge.astrum.infiniminer.Defines;
import com.novusforge.astrum.infiniminer.PropertyBag;
import org.joml.Vector3f;

/**
 * BlockEngine - Handles block storage, rendering logic, and collision.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class BlockEngine {
    
    public BlockType[][][] blockList;
    public BlockType[][][] downloadList;
    
    private static final int REGION_SIZE = 16;
    private final int regionRatio;
    private final int numRegions;
    private final boolean[][] vertexListDirty;
    
    private PropertyBag _P;

    public BlockEngine() {
        regionRatio = Defines.MAP_SIZE_X / REGION_SIZE;
        numRegions = regionRatio * regionRatio;
        
        blockList = new BlockType[Defines.MAP_SIZE_X][Defines.MAP_SIZE_Y][Defines.MAP_SIZE_Z];
        downloadList = new BlockType[Defines.MAP_SIZE_X][Defines.MAP_SIZE_Y][Defines.MAP_SIZE_Z];
        
        // Initialize with Air/None
        for (int x = 0; x < Defines.MAP_SIZE_X; x++) {
            for (int y = 0; y < Defines.MAP_SIZE_Y; y++) {
                for (int z = 0; z < Defines.MAP_SIZE_Z; z++) {
                    blockList[x][y][z] = BlockType.None;
                    downloadList[x][y][z] = BlockType.None;
                }
            }
        }
        
        vertexListDirty = new boolean[numRegions][BlockType.values().length];
        
        System.out.println("BlockEngine initialized: " + Defines.MAP_SIZE_X + "x" + Defines.MAP_SIZE_Y + "x" + Defines.MAP_SIZE_Z);
    }
    
    public void setPropertyBag(PropertyBag p) {
        this._P = p;
    }

    public void setBlock(int x, int y, int z, BlockType blockType) {
        if (isInBounds(x, y, z)) {
            BlockType oldBlock = blockList[x][y][z];
            blockList[x][y][z] = blockType;
            
            if (oldBlock != blockType) {
                makeRegionDirty(x, z);
            }
        }
    }
    
    public BlockType getBlock(int x, int y, int z) {
        if (isInBounds(x, y, z)) {
            return blockList[x][y][z];
        }
        return BlockType.None;
    }
    
    public void addBlock(int x, int y, int z, BlockType blockType) {
        setBlock(x, y, z, blockType);
    }
    
    public void removeBlock(int x, int y, int z) {
        setBlock(x, y, z, BlockType.None);
    }
    
    public void makeRegionDirty(int x, int z) {
        int regionX = x / REGION_SIZE;
        int regionZ = z / REGION_SIZE;
        int regionIndex = regionX + regionZ * regionRatio;
        
        if (regionIndex >= 0 && regionIndex < numRegions) {
            for (int i = 0; i < BlockType.values().length; i++) {
                vertexListDirty[regionIndex][i] = true;
            }
        }
    }
    
    public boolean isInBounds(int x, int y, int z) {
        return x >= 0 && x < Defines.MAP_SIZE_X &&
               y >= 0 && y < Defines.MAP_SIZE_Y &&
               z >= 0 && z < Defines.MAP_SIZE_Z;
    }
    
    public boolean isTransparent(int x, int y, int z) {
        BlockType block = getBlock(x, y, z);
        return block.isTransparent();
    }
    
    public boolean isSolid(int x, int y, int z) {
        BlockType block = getBlock(x, y, z);
        return block.isSolid();
    }
    
    public boolean solidAtPointForPlayer(Vector3f point) {
        return isSolid((int)Math.floor(point.x), (int)Math.floor(point.y), (int)Math.floor(point.z));
    }

    public float getBlockShade(int x, int y, int z) {
        // Simple lighting - blocks deeper = darker
        float depth = (float) y / Defines.MAP_SIZE_Y;
        return 0.3f + (0.7f * depth);
    }
    
    public void downloadComplete() {
        System.out.println("BlockEngine: Processing download list...");
        for (int x = 0; x < Defines.MAP_SIZE_X; x++) {
            for (int y = 0; y < Defines.MAP_SIZE_Y; y++) {
                for (int z = 0; z < Defines.MAP_SIZE_Z; z++) {
                    if (downloadList[x][y][z] != BlockType.None) {
                        addBlock(x, y, z, downloadList[x][y][z]);
                    }
                }
            }
        }
        System.out.println("BlockEngine: Download complete!");
    }
    
    public int getRegionIndex(int x, int z) {
        int regionX = x / REGION_SIZE;
        int regionZ = z / REGION_SIZE;
        return regionX + regionZ * regionRatio;
    }
    
    public boolean shouldRenderFace(int x, int y, int z, int direction) {
        int nx = x, ny = y, nz = z;
        
        switch (direction) {
            case 0 -> nx--; // Left
            case 1 -> nx++; // Right
            case 2 -> ny--; // Bottom
            case 3 -> ny++; // Top
            case 4 -> nz--; // Back
            case 5 -> nz++; // Front
        }
        
        // Render if neighbor is air or transparent
        if (!isInBounds(nx, ny, nz)) {
            return true; // Render chunk boundaries
        }
        
        BlockType neighbor = blockList[nx][ny][nz];
        return neighbor == BlockType.None || neighbor.isTransparent();
    }
}
