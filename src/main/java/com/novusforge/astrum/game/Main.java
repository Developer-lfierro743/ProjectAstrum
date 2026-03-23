package com.novusforge.astrum.game;

import com.novusforge.astrum.engine.*;
import com.novusforge.astrum.world.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Project Astrum - Pre-Classic (Cave Game)
 * Following Notch's original development approach
 * 
 * Stage: Pre-Classic
 * Features:
 * - Basic voxel rendering
 * - Infinite terrain generation
 * - First-person camera
 * - Block placement/destruction
 * 
 * Tech Stack:
 * - Java 21
 * - Vulkan (LWJGL 3)
 * - 32×32×32 chunks
 * - FastNoiseLite for terrain
 */
public class Main {
    
    private static final String TITLE = "Astrum - Pre-Classic (Cave Game)";
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME = 1_000_000_000 / TARGET_FPS;

    public static void main(String[] args) {
        // Print banner
        System.out.println("=".repeat(50));
        System.out.println("  ASTRUM - Pre-Classic (Cave Game)");
        System.out.println("  Project Astrum v0.0.1");
        System.out.println("  By Novusforge Studios");
        System.out.println("=".repeat(50));
        System.out.println();

        // Create renderer
        IRenderer renderer = new VulkanRenderer();
        Game game = null;
        
        try {
            // Initialize renderer
            if (!renderer.init()) {
                System.err.println("[ERROR] Renderer initialization failed!");
                System.exit(1);
            }
            
            // Create game
            game = new Game(renderer.getWindow(), renderer);
            game.init();
            
            // Set buffer deleter callback
            World.setBufferDeleter(renderer::deleteBuffer);
            
            System.out.println();
            System.out.println("[Render] " + renderer.getRendererName() + " initialized!");
            System.out.println("[Game] Astrum Pre-Classic loaded!");
            System.out.println("[Controls] WASD = Move, Space = Jump");
            System.out.println("[Controls] Mouse = Look, Click = Lock");
            System.out.println("[Controls] L-Click = Break, R-Click = Place");
            System.out.println();

            // Game Loop
            long lastTime = System.nanoTime();
            int frameCount = 0;
            long fpsTimer = System.currentTimeMillis();
            
            Matrix4f projectionMatrix = new Matrix4f();
            Matrix4f viewMatrix = new Matrix4f();
            
            while (!renderer.windowShouldClose() && game.isRunning()) {
                glfwPollEvents();
                
                long currentTime = System.nanoTime();
                float deltaTime = (currentTime - lastTime) / 1_000_000_000f;
                lastTime = currentTime;
                
                // Cap delta time
                deltaTime = Math.min(deltaTime, 0.1f);
                
                // Update
                game.update(deltaTime);
                
                // Update matrices
                projectionMatrix.setPerspective(
                    (float) Math.toRadians(70.0f), 
                    renderer.getAspectRatio(), 
                    0.1f, 
                    1000.0f, 
                    true
                );
                game.getInput().getViewMatrix(viewMatrix);
                Vector3f pos = game.getPlayer().getPosition();
                viewMatrix.translate(-pos.x, -pos.y, -pos.z);
                
                // Get visible meshes (with frustum culling)
                java.util.Map<Long, ChunkMesh> visibleMeshes =
                    game.getWorld().getVisibleMeshes(pos.x, pos.y, pos.z, null);
                
                // Render
                renderer.render(viewMatrix, projectionMatrix, visibleMeshes);
                
                // FPS counter
                frameCount++;
                if (System.currentTimeMillis() - fpsTimer >= 1000) {
                    int chunks = game.getWorld().getLoadedChunkCount();
                    System.out.printf("[FPS] %d | [Chunks] %d | [Pos] %.1f, %.1f, %.1f%n",
                        frameCount, chunks, pos.x, pos.y, pos.z);
                    frameCount = 0;
                    fpsTimer = System.currentTimeMillis();
                }
                
                // Frame timing
                long elapsed = System.nanoTime() - currentTime;
                if (elapsed < FRAME_TIME) {
                    try {
                        Thread.sleep((FRAME_TIME - elapsed) / 1_000_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            System.out.println("[Shutdown] Closing Astrum...");
            
        } finally {
            if (game != null) game.cleanup();
            if (renderer != null) renderer.cleanup();
            System.out.println("[Shutdown] Done!");
        }
    }
}
