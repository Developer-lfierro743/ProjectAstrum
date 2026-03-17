package com.novusforge.astrum.infiniminer;

import java.util.HashMap;
import java.util.Map;

/**
 * Block types for Infiniminer.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class BlockType {
    
    public static class BlockDef {
        public final String name;
        public final boolean transparent;
        public final boolean solid;
        public final int textureTop;
        public final int textureSide;
        public final int textureBottom;
        
        public BlockDef(String name, boolean transparent, boolean solid, int textureTop, int textureSide, int textureBottom) {
            this.name = name;
            this.transparent = transparent;
            this.solid = solid;
            this.textureTop = textureTop;
            this.textureSide = textureSide;
            this.textureBottom = textureBottom;
        }
    }
    
    private static final Map<Byte, BlockDef> BLOCKS = new HashMap<>();
    
    static {
        BLOCKS.put((byte) 0, new BlockDef("Air", true, false, 0, 0, 0));
        BLOCKS.put((byte) 1, new BlockDef("Stone", false, true, 0, 0, 0));
        BLOCKS.put((byte) 2, new BlockDef("Dirt", false, true, 1, 1, 1));
        BLOCKS.put((byte) 3, new BlockDef("Grass", false, true, 2, 3, 1));
        BLOCKS.put((byte) 4, new BlockDef("Coal Ore", false, true, 4, 4, 4));
        BLOCKS.put((byte) 5, new BlockDef("Iron Ore", false, true, 5, 5, 5));
        BLOCKS.put((byte) 6, new BlockDef("Gold Ore", false, true, 6, 6, 6));
        BLOCKS.put((byte) 7, new BlockDef("Lapis Ore", false, true, 7, 7, 7));
        BLOCKS.put((byte) 8, new BlockDef("Diamond Ore", false, true, 8, 8, 8));
        BLOCKS.put((byte) 9, new BlockDef("Obsidian", false, true, 9, 9, 9));
        BLOCKS.put((byte) 10, new BlockDef("Bedrock", false, true, 10, 10, 10));
        BLOCKS.put((byte) 11, new BlockDef("Water", true, false, 11, 11, 11));
        BLOCKS.put((byte) 12, new BlockDef("Lava", true, false, 12, 12, 12));
        BLOCKS.put((byte) 13, new BlockDef("Sand", false, true, 13, 13, 13));
        BLOCKS.put((byte) 14, new BlockDef("Gravel", false, true, 14, 14, 14));
        BLOCKS.put((byte) 15, new BlockDef("Wood", false, true, 15, 15, 15));
        BLOCKS.put((byte) 16, new BlockDef("Leaves", true, false, 16, 16, 16));
        
        // Infiniminer special blocks
        BLOCKS.put((byte) 100, new BlockDef("Home Block", false, true, 100, 100, 100));
        BLOCKS.put((byte) 101, new BlockDef("Team Base", false, true, 101, 101, 101));
        BLOCKS.put((byte) 102, new BlockDef("Road", false, true, 102, 102, 102));
        BLOCKS.put((byte) 103, new BlockDef("Solid Ore", false, true, 8, 8, 8));
    }
    
    public static BlockDef getBlock(byte id) {
        return BLOCKS.getOrDefault(id, new BlockDef("Unknown", false, true, 0, 0, 0));
    }
    
    public static boolean isTransparent(byte id) {
        BlockDef def = BLOCKS.get(id);
        return def != null && def.transparent;
    }
    
    public static boolean isSolid(byte id) {
        BlockDef def = BLOCKS.get(id);
        return def != null && def.solid;
    }
    
    public static String getName(byte id) {
        BlockDef def = BLOCKS.get(id);
        return def != null ? def.name : "Unknown";
    }
}
