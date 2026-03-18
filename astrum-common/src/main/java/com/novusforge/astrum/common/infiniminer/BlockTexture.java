package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's BlockTexture enum.
 * Originally defined in InfiniminerShared/BlockInformation.cs
 */
public enum BlockTexture {
    NONE,
    DIRT,
    ORE,
    GOLD,
    DIAMOND,
    ROCK,
    JUMP,
    JUMP_TOP,
    LADDER,
    LADDER_TOP,
    EXPLOSIVE,
    SPIKES,
    HOME_RED,
    HOME_BLUE,
    BANK_TOP_RED,
    BANK_TOP_BLUE,
    BANK_FRONT_RED,
    BANK_FRONT_BLUE,
    BANK_LEFT_RED,
    BANK_LEFT_BLUE,
    BANK_RIGHT_RED,
    BANK_RIGHT_BLUE,
    BANK_BACK_RED,
    BANK_BACK_BLUE,
    TELE_TOP,
    TELE_BOTTOM,
    TELE_SIDE_A,
    TELE_SIDE_B,
    SOLID_RED,
    SOLID_BLUE,
    METAL,
    DIRT_SIGN,
    LAVA,
    ROAD,
    ROAD_TOP,
    ROAD_BOTTOM,
    BEACON_RED,
    BEACON_BLUE,
    TRANS_RED,   // THESE MUST BE THE LAST TWO TEXTURES
    TRANS_BLUE,
    MAXIMUM;

    public byte toByte() {
        return (byte) this.ordinal();
    }
}
