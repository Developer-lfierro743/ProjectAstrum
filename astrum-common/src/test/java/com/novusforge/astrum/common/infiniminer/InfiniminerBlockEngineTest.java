package com.novusforge.astrum.common.infiniminer;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class InfiniminerBlockEngineTest {

    @Test
    public void testAddAndRemoveBlock() {
        InfiniminerBlockEngine engine = new InfiniminerBlockEngine();
        int x = 10, y = 10, z = 10;
        
        // Initial state
        assertEquals(BlockType.NONE, engine.getBlockAt(x, y, z));
        
        // Add a block
        engine.addBlock(x, y, z, BlockType.DIRT);
        assertEquals(BlockType.DIRT, engine.getBlockAt(x, y, z));
        
        // Check visible faces (all 6 should be visible if neighbors are air)
        int visibleCount = 0;
        for (BlockTexture texture : BlockTexture.values()) {
            if (texture == BlockTexture.NONE || texture == BlockTexture.MAXIMUM) continue;
            for (int r = 0; r < InfiniminerBlockEngine.NUMREGIONS; r++) {
                visibleCount += engine.getVisibleFaces(texture, r).size();
            }
        }
        // Dirt block has same texture for all faces
        assertEquals(6, visibleCount);
        
        // Remove the block
        engine.removeBlock(x, y, z);
        assertEquals(BlockType.NONE, engine.getBlockAt(x, y, z));
        
        // Check visible faces (should be 0 now)
        visibleCount = 0;
        for (BlockTexture texture : BlockTexture.values()) {
            if (texture == BlockTexture.NONE || texture == BlockTexture.MAXIMUM) continue;
            for (int r = 0; r < InfiniminerBlockEngine.NUMREGIONS; r++) {
                visibleCount += engine.getVisibleFaces(texture, r).size();
            }
        }
        assertEquals(0, visibleCount);
    }

    @Test
    public void testFaceCulling() {
        InfiniminerBlockEngine engine = new InfiniminerBlockEngine();
        
        // Add two adjacent blocks
        engine.addBlock(10, 10, 10, BlockType.DIRT);
        engine.addBlock(11, 10, 10, BlockType.DIRT);
        
        // Each block has 6 faces. 
        // 10,10,10 should have X+ culled by 11,10,10's X-
        // 11,10,10 should have X- culled by 10,10,10's X+
        // Total visible faces: (6-1) + (6-1) = 10
        
        int visibleCount = 0;
        for (BlockTexture texture : BlockTexture.values()) {
            if (texture == BlockTexture.NONE || texture == BlockTexture.MAXIMUM) continue;
            for (int r = 0; r < InfiniminerBlockEngine.NUMREGIONS; r++) {
                visibleCount += engine.getVisibleFaces(texture, r).size();
            }
        }
        assertEquals(10, visibleCount);
    }
}
