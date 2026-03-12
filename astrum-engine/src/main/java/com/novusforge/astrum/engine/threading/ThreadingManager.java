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
package com.novusforge.astrum.engine.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The high-performance threading engine for Project Astrum.
 * Manages separate pools for World Generation (normal) and Mesh Generation (high priority).
 */
public class ThreadingManager {

    private final ExecutorService worldGenPool;
    private final ExecutorService meshGenPool;

    public ThreadingManager() {
        this.worldGenPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new AstrumThreadFactory("WorldGen", Thread.NORM_PRIORITY)
        );
        this.meshGenPool = Executors.newFixedThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors() / 2),
            new AstrumThreadFactory("MeshGen", Thread.MAX_PRIORITY)
        );
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
    }

    private static class AstrumThreadFactory implements ThreadFactory {
        private final String name;
        private final int priority;

        public AstrumThreadFactory(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, name + "-" + System.nanoTime());
            t.setPriority(priority);
            t.setDaemon(true);
            return t;
        }
    }
}
