/*
 * Copyright (c) 2026 NovusForge Project Astrum. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.novusforge.astrum.common.world;

/**
 * High-performance chunk data storage using a 1D array as specified in the 2026 Formula.
 * Optimized for cache locality and minimal memory overhead.
 */
public class ChunkData {
    public static final int SIZE = 32;
    public static final int TOTAL_BLOCKS = SIZE * SIZE * SIZE;

    // Using short[] to allow for >256 BlockIDs (Formula Part 3: "exceeds upgrade chunk data blocks as shorts")
    private final short[] blocks;

    public ChunkData() {
        this.blocks = new short[TOTAL_BLOCKS];
    }

    /**
     * Maps 3D coordinates to a 1D index: (y * SIZE * SIZE) + (z * SIZE) + x
     * This order is optimized for vertical-first column processing.
     */
    private int getIndex(int x, int y, int z) {
        return (y << 10) | (z << 5) | x; // Optimized bit-shifting for 32x32x32
    }

    public void setBlock(int x, int y, int z, short blockId) {
        if (isValid(x, y, z)) {
            blocks[getIndex(x, y, z)] = blockId;
        }
    }

    public short getBlock(int x, int y, int z) {
        if (!isValid(x, y, z)) return 0;
        return blocks[getIndex(x, y, z)];
    }

    private boolean isValid(int x, int y, int z) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE;
    }

    public short[] getRawData() {
        return blocks;
    }
}
