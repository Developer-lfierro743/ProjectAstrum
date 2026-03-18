package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's BlockFaceDirection enum.
 * Originally defined in InfiniminerShared/BlockInformation.cs
 */
public enum BlockFaceDirection {
    X_INCREASING,
    X_DECREASING,
    Y_INCREASING,
    Y_DECREASING,
    Z_INCREASING,
    Z_DECREASING,
    MAXIMUM;

    public byte toByte() {
        return (byte) this.ordinal();
    }
}
