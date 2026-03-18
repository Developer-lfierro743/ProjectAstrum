package com.novusforge.astrum.common.infiniminer;

import java.util.HashSet;
import java.util.Set;

/**
 * Port of Infiniminer's BlockEngine (logic only).
 * Originally defined in InfiniminerClient/Engines/BlockEngine.cs
 */
public class InfiniminerBlockEngine {
    public static final int MAPSIZE = 64;
    public static final int REGIONSIZE = 16;
    public static final int REGIONRATIO = MAPSIZE / REGIONSIZE;
    public static final int NUMREGIONS = REGIONRATIO * REGIONRATIO * REGIONRATIO;

    private final BlockType[][][] blockList = new BlockType[MAPSIZE][MAPSIZE][MAPSIZE];
    
    // faceMap[textureIndex][regionIndex] -> Set of encoded face indices
    @SuppressWarnings("unchecked")
    private final Set<Integer>[][] faceMap = new Set[BlockTexture.values().length][NUMREGIONS];
    
    // Cache of block type to texture mapping for all 6 faces
    private final BlockTexture[][] blockTextureMap = new BlockTexture[BlockType.values().length][BlockFaceDirection.values().length];

    private final boolean[][] vertexListDirty = new boolean[BlockTexture.values().length][NUMREGIONS];

    public InfiniminerBlockEngine() {
        // Initialize block list
        for (int i = 0; i < MAPSIZE; i++) {
            for (int j = 0; j < MAPSIZE; j++) {
                for (int k = 0; k < MAPSIZE; k++) {
                    blockList[i][j][k] = BlockType.NONE;
                }
            }
        }

        // Initialize face lists
        for (int t = 0; t < BlockTexture.values().length; t++) {
            for (int r = 0; r < NUMREGIONS; r++) {
                faceMap[t][r] = new HashSet<>();
                vertexListDirty[t][r] = true;
            }
        }

        // Initialize texture map cache
        for (BlockType type : BlockType.values()) {
            for (BlockFaceDirection dir : BlockFaceDirection.values()) {
                if (dir == BlockFaceDirection.MAXIMUM) continue;
                blockTextureMap[type.ordinal()][dir.ordinal()] = BlockInformation.getTexture(type, dir);
            }
        }
    }

    public BlockType[][][] getBlockList() {
        return blockList;
    }

    public BlockType getBlockAt(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= MAPSIZE || y >= MAPSIZE || z >= MAPSIZE) {
            return BlockType.NONE;
        }
        return blockList[x][y][z];
    }

    public void addBlock(int x, int y, int z, BlockType blockType) {
        if (x <= 0 || y <= 0 || z <= 0 || x >= MAPSIZE - 1 || y >= MAPSIZE - 1 || z >= MAPSIZE - 1) {
            return;
        }

        blockList[x][y][z] = blockType;

        processAddBlockFace(x, y, z, BlockFaceDirection.X_INCREASING, blockType, x + 1, y, z, BlockFaceDirection.X_DECREASING);
        processAddBlockFace(x, y, z, BlockFaceDirection.X_DECREASING, blockType, x - 1, y, z, BlockFaceDirection.X_INCREASING);
        processAddBlockFace(x, y, z, BlockFaceDirection.Y_INCREASING, blockType, x, y + 1, z, BlockFaceDirection.Y_DECREASING);
        processAddBlockFace(x, y, z, BlockFaceDirection.Y_DECREASING, blockType, x, y - 1, z, BlockFaceDirection.Y_INCREASING);
        processAddBlockFace(x, y, z, BlockFaceDirection.Z_INCREASING, blockType, x, y, z + 1, BlockFaceDirection.Z_DECREASING);
        processAddBlockFace(x, y, z, BlockFaceDirection.Z_DECREASING, blockType, x, y, z - 1, BlockFaceDirection.Z_INCREASING);
    }

    private void processAddBlockFace(int x, int y, int z, BlockFaceDirection dir, BlockType type, int x2, int y2, int z2, BlockFaceDirection dir2) {
        BlockType type2 = blockList[x2][y2][z2];
        // If the neighbor is solid and not translucent, hide the neighbor's face.
        // If the current block is translucent, we still show faces.
        boolean typeTrans = (type == BlockType.TRANS_RED || type == BlockType.TRANS_BLUE);
        boolean type2Trans = (type2 == BlockType.TRANS_RED || type2 == BlockType.TRANS_BLUE);

        if (type2 != BlockType.NONE && !typeTrans && !type2Trans) {
            hideQuad(x2, y2, z2, dir2, type2);
        } else {
            showQuad(x, y, z, dir, type);
        }
    }

    public void removeBlock(int x, int y, int z) {
        if (x <= 0 || y <= 0 || z <= 0 || x >= MAPSIZE - 1 || y >= MAPSIZE - 1 || z >= MAPSIZE - 1) {
            return;
        }

        BlockType type = blockList[x][y][z];
        if (type == BlockType.NONE) return;

        processRemoveBlockFace(x, y, z, BlockFaceDirection.X_INCREASING, x + 1, y, z, BlockFaceDirection.X_DECREASING);
        processRemoveBlockFace(x, y, z, BlockFaceDirection.X_DECREASING, x - 1, y, z, BlockFaceDirection.X_INCREASING);
        processRemoveBlockFace(x, y, z, BlockFaceDirection.Y_INCREASING, x, y + 1, z, BlockFaceDirection.Y_DECREASING);
        processRemoveBlockFace(x, y, z, BlockFaceDirection.Y_DECREASING, x, y - 1, z, BlockFaceDirection.Y_INCREASING);
        processRemoveBlockFace(x, y, z, BlockFaceDirection.Z_INCREASING, x, y, z + 1, BlockFaceDirection.Z_DECREASING);
        processRemoveBlockFace(x, y, z, BlockFaceDirection.Z_DECREASING, x, y, z - 1, BlockFaceDirection.Z_INCREASING);

        blockList[x][y][z] = BlockType.NONE;
    }

    private void processRemoveBlockFace(int x, int y, int z, BlockFaceDirection dir, int x2, int y2, int z2, BlockFaceDirection dir2) {
        BlockType type = blockList[x][y][z];
        BlockType type2 = blockList[x2][y2][z2];
        
        boolean typeTrans = (type == BlockType.TRANS_RED || type == BlockType.TRANS_BLUE);
        boolean type2Trans = (type2 == BlockType.TRANS_RED || type2 == BlockType.TRANS_BLUE);

        if (type2 != BlockType.NONE && !typeTrans && !type2Trans) {
            showQuad(x2, y2, z2, dir2, type2);
        } else {
            hideQuad(x, y, z, dir, type);
        }
    }

    private int encodeBlockFace(int x, int y, int z, BlockFaceDirection faceDir) {
        return x + y * MAPSIZE + z * MAPSIZE * MAPSIZE + faceDir.ordinal() * MAPSIZE * MAPSIZE * MAPSIZE;
    }

    private int getRegion(int x, int y, int z) {
        return (x / REGIONSIZE) + (y / REGIONSIZE) * REGIONRATIO + (z / REGIONSIZE) * REGIONRATIO * REGIONRATIO;
    }

    private void showQuad(int x, int y, int z, BlockFaceDirection faceDir, BlockType blockType) {
        if (blockType == BlockType.NONE) return;
        BlockTexture blockTexture = blockTextureMap[blockType.ordinal()][faceDir.ordinal()];
        if (blockTexture == BlockTexture.NONE) return;

        int blockFace = encodeBlockFace(x, y, z, faceDir);
        int region = getRegion(x, y, z);
        
        faceMap[blockTexture.ordinal()][region].add(blockFace);
        vertexListDirty[blockTexture.ordinal()][region] = true;
    }

    private void hideQuad(int x, int y, int z, BlockFaceDirection faceDir, BlockType blockType) {
        if (blockType == BlockType.NONE) return;
        BlockTexture blockTexture = blockTextureMap[blockType.ordinal()][faceDir.ordinal()];
        if (blockTexture == BlockTexture.NONE) return;

        int blockFace = encodeBlockFace(x, y, z, faceDir);
        int region = getRegion(x, y, z);
        
        faceMap[blockTexture.ordinal()][region].remove(blockFace);
        vertexListDirty[blockTexture.ordinal()][region] = true;
    }

    public Set<Integer> getVisibleFaces(BlockTexture texture, int region) {
        return faceMap[texture.ordinal()][region];
    }

    public boolean isRegionDirty(BlockTexture texture, int region) {
        return vertexListDirty[texture.ordinal()][region];
    }

    public void setRegionClean(BlockTexture texture, int region) {
        vertexListDirty[texture.ordinal()][region] = false;
    }
}
