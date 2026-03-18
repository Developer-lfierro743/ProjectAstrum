package com.novusforge.astrum.common.infiniminer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InfiniminerCaveGeneratorTest {

    @Test
    public void testGenerateCaveSystem() {
        int size = 64;
        BlockType[][][] map = InfiniminerCaveGenerator.generateCaveSystem(size, true, 10);
        
        assertNotNull(map);
        assertEquals(size, map.length);
        assertEquals(size, map[0].length);
        assertEquals(size, map[0][0].length);

        // Check if we have some dirt or air
        int dirtCount = 0;
        int noneCount = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    if (map[x][y][z] == BlockType.DIRT) dirtCount++;
                    if (map[x][y][z] == BlockType.NONE) noneCount++;
                }
            }
        }
        
        System.out.println("Dirt count: " + dirtCount);
        System.out.println("None count: " + noneCount);
        
        assertTrue(dirtCount > 0, "Map should contain some dirt");
        assertTrue(noneCount > 0, "Map should contain some empty space (caves/sky)");
    }
}
