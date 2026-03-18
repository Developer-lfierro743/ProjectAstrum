package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's InfiniminerMessage enum.
 * Originally defined in InfiniminerShared/GeneralEnums.cs
 */
public enum InfiniminerMessage {
    BLOCK_BULK_TRANSFER,      // x-value, y-value, followed by 64 bytes of blocktype
    BLOCK_SET,               // x, y, z, type
    USE_TOOL,                // position, heading, tool, blocktype
    SELECT_CLASS,            // class
    RESOURCE_UPDATE,         // ore, cash, weight, max ore, max weight, team ore, red cash, blue cash: ReliableInOrder1
    DEPOSIT_ORE,
    DEPOSIT_CASH,
    WITHDRAW_ORE,
    TRIGGER_EXPLOSION,       // position

    PLAYER_UPDATE,           // (uint id for server), position, heading, current tool, animate using (bool): UnreliableInOrder1
    PLAYER_JOINED,           // uint id, player name :ReliableInOrder2
    PLAYER_LEFT,             // uint id              :ReliableInOrder2
    PLAYER_SET_TEAM,          // (uint id for server), byte team   :ReliableInOrder2
    PLAYER_DEAD,             // (uint id for server) :ReliableInOrder2
    PLAYER_ALIVE,            // (uint id for server) :ReliableInOrder2
    PLAYER_PING,             // uint id

    CHAT_MESSAGE,            // byte type, string message : ReliableInOrder3
    GAME_OVER,               // byte team
    PLAY_SOUND,              // byte sound, bool isPositional, ?Vector3 location : ReliableUnordered
    TRIGGER_CONSTRUCTION_GUN_ANIMATION,
    SET_BEACON;              // vector3 position, string text ("" means remove)

    public byte toByte() {
        return (byte) this.ordinal();
    }

    public static InfiniminerMessage fromByte(byte b) {
        if (b < 0 || b >= values().length) {
            return BLOCK_SET; // Default?
        }
        return values()[b];
    }
}
