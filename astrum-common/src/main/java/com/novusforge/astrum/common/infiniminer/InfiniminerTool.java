package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's PlayerTools enum.
 * Originally defined in InfiniminerShared/Player.cs
 */
public enum InfiniminerTool {
    PICKAXE,
    CONSTRUCTION_GUN,
    DECONSTRUCTION_GUN,
    PROSPECTING_RADAR,
    DETONATOR;

    public byte toByte() {
        return (byte) this.ordinal();
    }

    public static InfiniminerTool fromByte(byte b) {
        if (b < 0 || b >= values().length) {
            return PICKAXE;
        }
        return values()[b];
    }
}
