package com.novusforge.astrum.infiniminer;

/**
 * Block types for Infiniminer.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public enum BlockType {
    None,
    Dirt,
    Ore,
    Gold,
    Diamond,
    Rock,
    Ladder,
    Explosive,
    Jump,
    Shock,
    BankRed,
    BankBlue,
    BeaconRed,
    BeaconBlue,
    Road,
    SolidRed,
    SolidBlue,
    Metal,
    DirtSign,
    Lava,
    TransRed,
    TransBlue;

    public static BlockType fromByte(byte b) {
        if (b >= 0 && b < values().length) {
            return values()[b];
        }
        return None;
    }

    public byte toByte() {
        return (byte) ordinal();
    }

    public boolean isSolid() {
        switch (this) {
            case None:
            case Lava:
                return false;
            default:
                return true;
        }
    }

    public boolean isTransparent() {
        switch (this) {
            case None:
            case TransRed:
            case TransBlue:
            case Lava:
                return true;
            default:
                return false;
        }
    }

    public int getCost() {
        switch (this) {
            case BankRed:
            case BankBlue:
            case BeaconRed:
            case BeaconBlue:
                return 50;
            case SolidRed:
            case SolidBlue:
            case Road:
                return 10;
            case TransRed:
            case TransBlue:
            case Jump:
            case Ladder:
                return 25;
            case Shock:
                return 50;
            case Explosive:
                return 100;
            default:
                return 1000;
        }
    }
}
