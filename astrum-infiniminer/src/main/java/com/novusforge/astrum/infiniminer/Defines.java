package com.novusforge.astrum.infiniminer;

/**
 * Infiniminer Defines - Core constants and definitions.
 * Ported from C# XNA to Java 21 LWJGL3.
 * Original: Infiniminer/Configuration/Blocks/BlockTypes.cs
 */
public class Defines {
    
    // Version
    public static final String INFINIMINER_VERSION = "0.3.4";
    
    // Colors
    public static final int IM_RED = 0xFF0000;
    public static final int IM_BLUE = 0x0000FF;
    public static final int IM_RED_DARK = 0xAA0000;
    public static final int IM_BLUE_DARK = 0x0000AA;
    
    // Map dimensions
    public static final int MAP_SIZE_X = 64;
    public static final int MAP_SIZE_Y = 64;
    public static final int MAP_SIZE_Z = 64;
    
    // Network
    public static final int DEFAULT_PORT = 5565;
    
    // Player
    public static final float PLAYER_HEIGHT = 1.8f;
    public static final float PLAYER_SPEED = 5.0f;
    public static final float PLAYER_JUMP = 6.5f;
    public static final float PLAYER_GRAVITY = 20.0f;
    public static final float MOUSE_SENSITIVITY = 0.005f;
    
    // Blocks
    public static final byte BLOCK_AIR = 0;
    public static final byte BLOCK_STONE = 1;
    public static final byte BLOCK_DIRT = 2;
    public static final byte BLOCK_GRASS = 3;
    public static final byte BLOCK_COAL = 4;
    public static final byte BLOCK_IRON = 5;
    public static final byte BLOCK_GOLD = 6;
    public static final byte BLOCK_LAPIS = 7;
    public static final byte BLOCK_DIAMOND = 8;
    public static final byte BLOCK_OBSIDIAN = 9;
    public static final byte BLOCK_BEDROCK = 10;
    public static final byte BLOCK_WATER = 11;
    public static final byte BLOCK_LAVA = 12;
    public static final byte BLOCK_SAND = 13;
    public static final byte BLOCK_GRAVEL = 14;
    public static final byte BLOCK_WOOD = 15;
    public static final byte BLOCK_LEAVES = 16;
    
    // Team colors
    public static final int TEAM_RED = 0;
    public static final int TEAM_BLUE = 1;
    public static final int TEAM_NONE = 2;
    
    // Game modes
    public static final int MODE_SANDBOX = 0;
    public static final int MODE_CTF = 1; // Capture The Flag
    public static final int MODE_TEAM_DEATHMATCH = 2;
    public static final int MODE_PURGE = 3;
    
    // Player classes
    public static final int CLASS_MASON = 0;
    public static final int CLASS_PROSPECTOR = 1;
    public static final int CLASS_MINER = 2;
    public static final int CLASS_MERCENARY = 3;
    public static final int CLASS_ENGINEER = 4;
    public static final int CLASS_SNIPER = 5;
    
    // Block update types
    public static final byte BLOCK_UPDATE_ADD = 0;
    public static final byte BLOCK_UPDATE_REMOVE = 1;
}
