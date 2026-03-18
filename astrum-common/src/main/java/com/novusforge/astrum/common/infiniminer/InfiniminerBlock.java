package com.novusforge.astrum.common.infiniminer;

/**
 * A container for Infiniminer block data.
 */
public class InfiniminerBlock {
    private BlockType type;

    public InfiniminerBlock(BlockType type) {
        this.type = type;
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InfiniminerBlock{" +
                "type=" + type +
                '}';
    }
}
