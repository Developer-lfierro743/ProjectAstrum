package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's PlayerTeam enum.
 * Originally defined in InfiniminerShared/Player.cs
 */
public enum InfiniminerTeam {
    NONE,
    RED,
    BLUE;

    public byte toByte() {
        return (byte) this.ordinal();
    }

    public static InfiniminerTeam fromByte(byte b) {
        if (b < 0 || b >= values().length) {
            return NONE;
        }
        return values()[b];
    }
}
