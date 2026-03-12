package com.novusforge.astrum.engine.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages specialized thread pools for Project Astrum (Formula Part 3).
 * Separates WorldGen and MeshGeneration from the Main Render Thread.
 */
public class ThreadingManager {
    private final ExecutorService worldGenPool;
    private final ExecutorService meshGenPool;

    public ThreadingManager() {
        // Use a fixed pool based on available cores minus one for the main thread
        int cores = Runtime.getRuntime().availableProcessors();
        int poolSize = Math.max(1, cores - 1);

        this.worldGenPool = Executors.newFixedThreadPool(poolSize, r -> {
            Thread t = new Thread(r, "WorldGen-Thread");
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        });

        this.meshGenPool = Executors.newFixedThreadPool(poolSize, r -> {
            Thread t = new Thread(r, "MeshGen-Thread");
            t.setPriority(Thread.MAX_PRIORITY); // Meshing is high priority for visual updates
            return t;
        });
    }

    public void submitWorldGen(Runnable task) {
        worldGenPool.submit(task);
    }

    public void submitMeshGen(Runnable task) {
        meshGenPool.submit(task);
    }

    public void shutdown() {
        worldGenPool.shutdown();
        meshGenPool.shutdown();
        try {
            if (!worldGenPool.awaitTermination(5, TimeUnit.SECONDS)) worldGenPool.shutdownNow();
            if (!meshGenPool.awaitTermination(5, TimeUnit.SECONDS)) meshGenPool.shutdownNow();
        } catch (InterruptedException e) {
            worldGenPool.shutdownNow();
            meshGenPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
