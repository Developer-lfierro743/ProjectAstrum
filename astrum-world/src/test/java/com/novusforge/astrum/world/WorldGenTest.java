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
package com.novusforge.astrum.world;

import com.novusforge.astrum.world.generator.ChunkGenerator;
import com.novusforge.astrum.engine.threading.ThreadingManager;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.List;

/**
 * Performance benchmark for multi-threaded world generation (The Formula Part 5).
 * Remade to ensure clean imports and resolve editor corruption.
 */
public class WorldGenTest {

    @Test
    public void benchmarkWorldGen() throws Exception {
        int seed = 1337;
        ChunkGenerator generator = new ChunkGenerator(seed);
        ThreadingManager threadingManager = new ThreadingManager();
        
        int chunksToGenerate = 1000;
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        
        System.out.println("[Benchmark] Generating " + chunksToGenerate + " chunks (32x32x32) using ThreadingManager...");
        
        for (int i = 0; i < chunksToGenerate; i++) {
            final int chunkIdx = i;
            tasks.add(CompletableFuture.runAsync(() -> {
                generator.generateChunk(chunkIdx, 0, 0);
            }, threadingManager::submitWorldGen));
        }
        
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("[Benchmark] Generated " + chunksToGenerate + " chunks in " + totalTime + "ms");
        System.out.println("[Benchmark] Avg Time Per Chunk: " + (double)totalTime / chunksToGenerate + "ms");
        
        threadingManager.shutdown();
    }
}
