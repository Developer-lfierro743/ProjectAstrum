package com.novusforge.astrum.infiniminer;

public enum BlockTexture {
    None,
    Dirt,
    Ore,
    Gold,
    Diamond,
    Rock,
    Jump,
    JumpTop,
    Ladder,
    LadderTop,
    Explosive,
    Spikes,
    HomeRed,
    HomeBlue,
    BankTopRed,
    BankTopBlue,
    BankFrontRed,
    BankFrontBlue,
    BankLeftRed,
    BankLeftBlue,
    BankRightRed,
    BankRightBlue,
    BankBackRed,
    BankBackBlue,
    TeleTop,
    TeleBottom,
    TeleSideA,
    TeleSideB,
    SolidRed,
    SolidBlue,
    Metal,
    DirtSign,
    Lava,
    Road,
    RoadTop,
    RoadBottom,
    BeaconRed,
    BeaconBlue,
    TransRed,
    TransBlue;

    public static BlockTexture getTexture(BlockType blockType, BlockFaceDirection faceDir) {
        return getTexture(blockType, faceDir, BlockType.None);
    }

    public static BlockTexture getTexture(BlockType blockType, BlockFaceDirection faceDir, BlockType blockAbove) {
        switch (blockType) {
            case Metal: return Metal;
            case Dirt: return Dirt;
            case Lava: return Lava;
            case Rock: return Rock;
            case Ore: return Ore;
            case Gold: return Gold;
            case Diamond: return Diamond;
            case DirtSign: return DirtSign;

            case BankRed:
                switch (faceDir) {
                    case XIncreasing: return BankFrontRed;
                    case XDecreasing: return BankBackRed;
                    case ZIncreasing: return BankLeftRed;
                    case ZDecreasing: return BankRightRed;
                    default: return BankTopRed;
                }

            case BankBlue:
                switch (faceDir) {
                    case XIncreasing: return BankFrontBlue;
                    case XDecreasing: return BankBackBlue;
                    case ZIncreasing: return BankLeftBlue;
                    case ZDecreasing: return BankRightBlue;
                    default: return BankTopBlue;
                }

            case BeaconRed:
            case BeaconBlue:
                switch (faceDir) {
                    case YDecreasing: return LadderTop;
                    case YIncreasing: return (blockType == BlockType.BeaconRed) ? BeaconRed : BeaconBlue;
                    case XDecreasing:
                    case XIncreasing: return TeleSideA;
                    case ZDecreasing:
                    case ZIncreasing: return TeleSideB;
                    default: return None;
                }

            case Road:
                if (faceDir == BlockFaceDirection.YIncreasing) return RoadTop;
                else if (faceDir == BlockFaceDirection.YDecreasing || blockAbove != BlockType.None) return RoadBottom;
                return Road;

            case Shock:
                switch (faceDir) {
                    case YDecreasing: return Spikes;
                    case YIncreasing: return TeleBottom;
                    case XDecreasing:
                    case XIncreasing: return TeleSideA;
                    case ZDecreasing:
                    case ZIncreasing: return TeleSideB;
                    default: return None;
                }

            case Jump:
                switch (faceDir) {
                    case YDecreasing: return TeleBottom;
                    case YIncreasing: return JumpTop;
                    case XDecreasing:
                    case XIncreasing: return Jump;
                    case ZDecreasing:
                    case ZIncreasing: return Jump;
                    default: return None;
                }

            case SolidRed: return SolidRed;
            case SolidBlue: return SolidBlue;
            case TransRed: return TransRed;
            case TransBlue: return TransBlue;

            case Ladder:
                if (faceDir == BlockFaceDirection.YDecreasing || faceDir == BlockFaceDirection.YIncreasing)
                    return LadderTop;
                else
                    return Ladder;

            case Explosive: return Explosive;
            default: return None;
        }
    }
}
