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
package com.novusforge.astrum.world.generator;

import com.novusforge.astrum.common.world.BlockIds;
import com.novusforge.astrum.common.world.ChunkData;
import io.github.auburn.FastNoiseLite;

/**
 * High-performance infinite world generator using FastNoiseLite.
 * Optimized for 32x32x32 chunks (The Formula Part 5).
 */
public class ChunkGenerator {

    private final FastNoiseLite noise;
    private final int seed;

    public ChunkGenerator(int seed) {
        this.seed = seed;
        this.noise = new FastNoiseLite(seed);
        this.noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.noise.SetFrequency(0.01f); // Adjust for terrain smoothness
    }

    /**
     * Generates terrain for a single 32x32x32 chunk.
     * @param chunkX World chunk X coordinate
     * @param chunkY World chunk Y coordinate
     * @param chunkZ World chunk Z coordinate
     * @return Populated ChunkData
     */
    public ChunkData generateChunk(int chunkX, int chunkY, int chunkZ) {
        ChunkData chunk = new ChunkData();
        int worldXBase = chunkX * ChunkData.SIZE;
        int worldYBase = chunkY * ChunkData.SIZE;
        int worldZBase = chunkZ * ChunkData.SIZE;

        for (int x = 0; x < ChunkData.SIZE; x++) {
            for (int z = 0; z < ChunkData.SIZE; z++) {
                // Calculate world-space height
                float noiseVal = noise.GetNoise(worldXBase + x, worldZBase + z);
                int height = (int) ((noiseVal + 1) * 32); // Scale noise to height

                for (int y = 0; y < ChunkData.SIZE; y++) {
                    int worldY = worldYBase + y;
                    
                    if (worldY < height - 3) {
                        chunk.setBlock(x, y, z, BlockIds.STONE);
                    } else if (worldY < height - 1) {
                        chunk.setBlock(x, y, z, BlockIds.DIRT);
                    } else if (worldY < height) {
                        chunk.setBlock(x, y, z, BlockIds.GRASS);
                    } else {
                        chunk.setBlock(x, y, z, BlockIds.AIR);
                    }
                    
                    // Add Bedrock at the very bottom (e.g., worldY == 0)
                    if (worldY == 0) {
                        chunk.setBlock(x, y, z, BlockIds.BEDROCK);
                    }
                }
            }
        }
        return chunk;
    }
}
