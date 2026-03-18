package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's PlayerClass enum.
 * Originally defined in InfiniminerShared/Player.cs
 */
public enum InfiniminerClass {
    PROSPECTOR,
    MINER,
    ENGINEER,
    SAPPER;

    public byte toByte() {
        return (byte) this.ordinal();
    }

    public static InfiniminerClass fromByte(byte b) {
        if (b < 0 || b >= values().length) {
            return PROSPECTOR;
        }
        return values()[b];
    }
}
