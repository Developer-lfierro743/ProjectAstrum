package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's BlockType enum.
 * Originally defined in InfiniminerShared/BlockInformation.cs
 */
public enum BlockType {
    NONE,
    DIRT,
    ORE,
    GOLD,
    DIAMOND,
    ROCK,
    LADDER,
    EXPLOSIVE,
    JUMP,
    SHOCK,
    BANK_RED,
    BANK_BLUE,
    BEACON_RED,
    BEACON_BLUE,
    ROAD,
    SOLID_RED,
    SOLID_BLUE,
    METAL,
    DIRT_SIGN,
    LAVA,
    TRANS_RED,
    TRANS_BLUE,
    MAXIMUM;

    public byte toByte() {
        return (byte) this.ordinal();
    }

    public static BlockType fromByte(byte b) {
        if (b < 0 || b >= values().length) {
            return NONE;
        }
        return values()[b];
    }
}
