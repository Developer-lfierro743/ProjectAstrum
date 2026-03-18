package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's BlockInformation utility class.
 * Originally defined in InfiniminerShared/BlockInformation.cs
 */
public class BlockInformation {

    public static long getCost(BlockType blockType) {
        return switch (blockType) {
            case BANK_RED, BANK_BLUE, BEACON_RED, BEACON_BLUE -> 50;
            case SOLID_RED, SOLID_BLUE -> 10;
            case TRANS_RED, TRANS_BLUE -> 25;
            case ROAD -> 10;
            case JUMP -> 25;
            case LADDER -> 25;
            case SHOCK -> 50;
            case EXPLOSIVE -> 100;
            default -> 1000;
        };
    }

    public static int getMatteColor(BlockType blockType) {
        return switch (blockType) {
            case DIRT -> InfiniminerConstants.COLOR_DIRT;
            case ORE -> InfiniminerConstants.COLOR_FERROUS;
            case GOLD -> InfiniminerConstants.COLOR_AURUM;
            case DIAMOND -> InfiniminerConstants.COLOR_ARGENTUM;
            case ROCK -> InfiniminerConstants.COLOR_ROCK;
            case LAVA -> InfiniminerConstants.COLOR_LAVA;
            case BANK_RED, BEACON_RED, SOLID_RED, TRANS_RED -> InfiniminerConstants.IM_RED;
            case BANK_BLUE, BEACON_BLUE, SOLID_BLUE, TRANS_BLUE -> InfiniminerConstants.IM_BLUE;
            case SHOCK -> 0xFF808080; // Legacy Gray
            default -> 0xFFFFFFFF; // Default white
        };
    }

    public static BlockTexture getTexture(BlockType blockType, BlockFaceDirection faceDir) {
        return getTexture(blockType, faceDir, BlockType.NONE);
    }

    public static BlockTexture getTexture(BlockType blockType, BlockFaceDirection faceDir, BlockType blockAbove) {
        return switch (blockType) {
            case METAL -> BlockTexture.METAL;
            case DIRT -> BlockTexture.DIRT;
            case LAVA -> BlockTexture.LAVA;
            case ROCK -> BlockTexture.ROCK;
            case ORE -> BlockTexture.ORE;
            case GOLD -> BlockTexture.GOLD;
            case DIAMOND -> BlockTexture.DIAMOND;
            case DIRT_SIGN -> BlockTexture.DIRT_SIGN;

            case BANK_RED -> switch (faceDir) {
                case X_INCREASING -> BlockTexture.BANK_FRONT_RED;
                case X_DECREASING -> BlockTexture.BANK_BACK_RED;
                case Z_INCREASING -> BlockTexture.BANK_LEFT_RED;
                case Z_DECREASING -> BlockTexture.BANK_RIGHT_RED;
                default -> BlockTexture.BANK_TOP_RED;
            };

            case BANK_BLUE -> switch (faceDir) {
                case X_INCREASING -> BlockTexture.BANK_FRONT_BLUE;
                case X_DECREASING -> BlockTexture.BANK_BACK_BLUE;
                case Z_INCREASING -> BlockTexture.BANK_LEFT_BLUE;
                case Z_DECREASING -> BlockTexture.BANK_RIGHT_BLUE;
                default -> BlockTexture.BANK_TOP_BLUE;
            };

            case BEACON_RED, BEACON_BLUE -> switch (faceDir) {
                case Y_DECREASING -> BlockTexture.LADDER_TOP;
                case Y_INCREASING -> blockType == BlockType.BEACON_RED ? BlockTexture.BEACON_RED : BlockTexture.BEACON_BLUE;
                case X_DECREASING, X_INCREASING -> BlockTexture.TELE_SIDE_A;
                case Z_DECREASING, Z_INCREASING -> BlockTexture.TELE_SIDE_B;
                default -> BlockTexture.NONE;
            };

            case ROAD -> {
                if (faceDir == BlockFaceDirection.Y_INCREASING) {
                    yield BlockTexture.ROAD_TOP;
                } else if (faceDir == BlockFaceDirection.Y_DECREASING || blockAbove != BlockType.NONE) {
                    yield BlockTexture.ROAD_BOTTOM;
                }
                yield BlockTexture.ROAD;
            }

            case SHOCK -> switch (faceDir) {
                case Y_DECREASING -> BlockTexture.SPIKES;
                case Y_INCREASING -> BlockTexture.TELE_BOTTOM;
                case X_DECREASING, X_INCREASING -> BlockTexture.TELE_SIDE_A;
                case Z_DECREASING, Z_INCREASING -> BlockTexture.TELE_SIDE_B;
                default -> BlockTexture.NONE;
            };

            case JUMP -> switch (faceDir) {
                case Y_DECREASING -> BlockTexture.TELE_BOTTOM;
                case Y_INCREASING -> BlockTexture.JUMP_TOP;
                case X_DECREASING, X_INCREASING, Z_DECREASING, Z_INCREASING -> BlockTexture.JUMP;
                default -> BlockTexture.NONE;
            };

            case SOLID_RED -> BlockTexture.SOLID_RED;
            case SOLID_BLUE -> BlockTexture.SOLID_BLUE;
            case TRANS_RED -> BlockTexture.TRANS_RED;
            case TRANS_BLUE -> BlockTexture.TRANS_BLUE;

            case LADDER -> (faceDir == BlockFaceDirection.Y_DECREASING || faceDir == BlockFaceDirection.Y_INCREASING)
                    ? BlockTexture.LADDER_TOP
                    : BlockTexture.LADDER;

            case EXPLOSIVE -> BlockTexture.EXPLOSIVE;

            default -> BlockTexture.NONE;
        };
    }
}
