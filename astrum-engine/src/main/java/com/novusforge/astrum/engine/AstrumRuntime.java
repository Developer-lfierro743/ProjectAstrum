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
package com.novusforge.astrum.engine;

import com.novusforge.astrum.engine.vulkan.VulkanContext;
import com.novusforge.astrum.engine.threading.ThreadingManager;
import com.novusforge.astrum.core.ecs.World;
import java.util.concurrent.CompletableFuture;
import org.lwjgl.system.Configuration;

/**
 * The central runtime glue for Project Astrum (The Formula).
 * Manages the lifecycle of the engine across Native (Vulkan) and Web (WebGPU/WASM-GC).
 */
public class AstrumRuntime {

    private static AstrumRuntime instance;
    
    private final RenderingContext renderingContext;
    private final ThreadingManager threadingManager;
    private final World ecsWorld;
    
    private boolean running = false;

    private AstrumRuntime() {
        this.renderingContext = new VulkanContext();
        this.threadingManager = new ThreadingManager();
        this.ecsWorld = new World();
        
        Configuration.STACK_SIZE.set(256); // 256KB stack
    }

    public static AstrumRuntime getInstance() {
        if (instance == null) {
            instance = new AstrumRuntime();
        }
        return instance;
    }

    public CompletableFuture<Boolean> bootstrap() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("[Astrum] Bootstrapping Rendering...");
                renderingContext.init();
                running = true;
                return true;
            } catch (Exception e) {
                System.err.println("[Astrum] Bootstrap Failed: " + e.getMessage());
                return false;
            }
        }, threadingManager::submitWorldGen);
    }

    public void stop() {
        running = false;
        renderingContext.cleanup();
        threadingManager.shutdown();
    }

    public boolean isRunning() {
        return running;
    }

    public RenderingContext getRendering() {
        return renderingContext;
    }

    public ThreadingManager getThreads() {
        return threadingManager;
    }

    public World getWorld() {
        return ecsWorld;
    }
}
