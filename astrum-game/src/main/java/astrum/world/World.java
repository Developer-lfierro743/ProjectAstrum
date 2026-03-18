package astrum.world;

import io.github.auburn.FastNoiseLite;
import java.util.HashMap;
import java.util.Map;

/**
 * World management using FastNoiseLite for generation and 32x32x32 chunks.
 */
public class World {
    private final Map<Long, Chunk> chunks = new HashMap<>();
    private final FastNoiseLite noise;

    public World(int seed) {
        this.noise = new FastNoiseLite(seed);
        this.noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
    }

    public void generateChunk(int cx, int cy, int cz) {
        Chunk chunk = new Chunk();
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int z = 0; z < Chunk.SIZE; z++) {
                float height = noise.GetNoise(cx * Chunk.SIZE + x, cz * Chunk.SIZE + z) * 16 + 16;
                for (int y = 0; y < Chunk.SIZE; y++) {
                    if (cy * Chunk.SIZE + y < height) {
                        chunk.setBlock(x, y, z, (short) 1); // Set to stone
                    }
                }
            }
        }
        chunks.put(getChunkKey(cx, cy, cz), chunk);
    }

    private long getChunkKey(int x, int y, int z) {
        return ((long) x & 0xFFFFFF) | (((long) y & 0xFFFFFF) << 24) | (((long) z & 0xFFFFFF) << 48);
    }

    public Chunk getChunk(int x, int y, int z) {
        return chunks.get(getChunkKey(x, y, z));
    }
}
