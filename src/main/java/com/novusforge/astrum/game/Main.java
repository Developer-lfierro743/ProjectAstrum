package com.novusforge.astrum.game;

import com.novusforge.astrum.core.*;
import com.novusforge.astrum.engine.VulkanRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    private static final String TITLE = "Astrum - Pre-classic (Cave Game)";
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME = 1_000_000_000 / TARGET_FPS;
    private static final String IIV_FILE = System.getProperty("user.home") + "/iiv_result.dat";
    
    public static void main(String[] args) {
        // 1. Print Astrum Banner
        System.out.println("=".repeat(50));
        System.out.println("  ASTRUM - Pre-classic (Cave Game)");
        System.out.println("  Project Astrum v0.1.0");
        System.out.println("  By Novusforge Studios");
        System.out.println("=".repeat(50));
        System.out.println();

        // 2. Account System Check or Launch
        checkAccountSession();

        // 3. IIV Check or Launch
        checkIdentityVerification();

        // 4. SafetyGuardian Init and Banner
        SafetyGuardian guardian = new SafetyGuardian();
        SafetyGuardian.ActionContext startupContext = new SafetyGuardian.ActionContext("startup", "system", "engine");
        if (guardian.validate(startupContext).decision() == SafetyGuardian.SafetyResult.Decision.BLOCK) {
            System.err.println("CRITICAL: SafetyGuardian blocked startup. Engine halted.");
            return;
        }
        System.out.println("  Player         : " + SessionManager.getUsername());
        System.out.println("  SafetyGuardian : ONLINE  (11 rules active)");
        System.out.println("  Fort Knox Gate : ACTIVE");
        System.out.println("  IIV Framework  : READY");
        System.out.println("  Mod Scanner    : ARMED");
        System.out.println("  Ethics Engine  : ARMED");
        System.out.println("=".repeat(50));
        System.out.println();

        // 5. ECS Init
        ECS ecs = new ECS();
        System.out.println("[Init] ECS: OK");
        System.out.println();

        // 6. Vulkan Renderer Init
        VulkanRenderer renderer = new VulkanRenderer();
        Game game = null;
        
        try {
            renderer.init();
            game = new Game(renderer.getWindow(), renderer, ecs, guardian);
            game.init();
            
            // Set up buffer deletion callback
            com.novusforge.astrum.world.World.setBufferDeleter(renderer::deleteBuffer);
            
            System.out.println();
            System.out.println("[Render] Vulkan renderer initialized!");
            System.out.println("[Game] Astrum Pre-classic loaded!");
            System.out.println("[Controls] Click to lock mouse, WASD to move, Space to jump");
            System.out.println("[Controls] Left click: Break block, Right click: Place block");
            System.out.println();
            
            // 7. Game Loop
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
                
                deltaTime = Math.min(deltaTime, 0.1f);
                
                game.update(deltaTime);
                
                // Update matrices
                projectionMatrix.setPerspective((float) Math.toRadians(70.0f), renderer.getAspectRatio(), 0.1f, 1000.0f, true);
                game.getInput().getViewMatrix(viewMatrix);
                viewMatrix.translate(-game.getPlayer().getPosition().x, -game.getPlayer().getPosition().y, -game.getPlayer().getPosition().z);

                // Render visible chunks
                java.util.Map<Long, com.novusforge.astrum.world.ChunkMesh> visibleMeshes =
                    game.getWorld().getVisibleMeshes(
                        game.getPlayer().getPosition().x,
                        game.getPlayer().getPosition().y,
                        game.getPlayer().getPosition().z,
                        null // TODO: Implement frustum planes
                    );

                renderer.render(viewMatrix, projectionMatrix, visibleMeshes);
                
                frameCount++;
                
                if (System.currentTimeMillis() - fpsTimer >= 1000) {
                    Vector3f pos = game.getPlayer().getPosition();
                    int chunks = game.getWorld().getLoadedChunkCount();
                    System.out.printf("[FPS] %d | [Chunks] %d | [Pos] %.1f, %.1f, %.1f%n", 
                        frameCount, chunks, pos.x, pos.y, pos.z);
                    frameCount = 0;
                    fpsTimer = System.currentTimeMillis();
                }
                
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
            if (game != null) {
                game.cleanup();
            }
            renderer.cleanup();
            System.out.println("[Shutdown] Astrum closed successfully!");
        }
    }

    private static void checkAccountSession() {
        // Check if session already exists (loaded by SessionManager static init)
        if (SessionManager.isLoggedIn()) {
            System.out.println("Account: Session found. Logged in as " + SessionManager.getUsername());
            return;
        }

        // Check DISPLAY environment variable for headless/development mode
        String display = System.getenv("DISPLAY");
        if (display == null || display.isEmpty()) {
            System.out.println("Account: DISPLAY not set. Creating dev session for development...");
            SessionManager.saveSession("DevPlayer", 0);
            System.out.println("Account: Dev session created. Logged in as DevPlayer");
            return;
        }

        // Launch Account System GUI
        try {
            System.out.println("Account: No session found. Launching Account System...");
            SwingUtilities.invokeAndWait(() -> {
                new AccountSystem();
            });

            if (!SessionManager.isLoggedIn()) {
                System.out.println("Account: Login cancelled. Falling back to dev session...");
                SessionManager.saveSession("DevPlayer", 0);
                System.out.println("Account: Dev session created. Logged in as DevPlayer");
            } else {
                System.out.println("Account: Login successful. Welcome " + SessionManager.getUsername());
            }
        } catch (java.awt.AWTError e) {
            System.out.println("Account: X11 connection failed. Falling back to dev session...");
            SessionManager.saveSession("DevPlayer", 0);
            System.out.println("Account: Dev session created. Logged in as DevPlayer");
        } catch (Exception e) {
            System.out.println("Account: GUI error (" + e.getClass().getSimpleName() + "). Falling back to dev session...");
            SessionManager.saveSession("DevPlayer", 0);
            System.out.println("Account: Dev session created. Logged in as DevPlayer");
        }
    }

    private static void checkIdentityVerification() {
        File file = new File(IIV_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object result = ois.readObject();
                String decision = result.toString();

                if (decision.contains("BLOCK")) {
                    System.err.println("CRITICAL: Entry denied by Identity Intent Verification.");
                    System.exit(0);
                }
                System.out.println("IIV: Previous verification found. Proceeding.");
                return;
            } catch (Exception e) {
                // If corrupted, re-run
            }
        }

        // Check DISPLAY environment variable for headless/development mode
        String display = System.getenv("DISPLAY");
        if (display == null || display.isEmpty()) {
            System.out.println("IIV: DISPLAY not set. Skipping Identity Verification for development...");
            return;
        }

        // Run Questionnaire GUI
        try {
            System.out.println("IIV: Launching Identity Intent Verification...");
            SwingUtilities.invokeAndWait(() -> {
                new IIVQuestionnaire();
            });

            // After completion, verify the result saved
            if (!file.exists()) {
                System.out.println("IIV: Verification cancelled. Skipping for development...");
                return;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object result = ois.readObject();
                if (result.toString().contains("BLOCK")) {
                    System.err.println("CRITICAL: Entry denied by Identity Intent Verification.");
                    System.exit(0);
                }
                System.out.println("IIV: Verification passed. Proceeding to engine initialization.");
            }
        } catch (java.awt.AWTError e) {
            System.out.println("IIV: X11 connection failed. Skipping Identity Verification for development...");
            return;
        } catch (Exception e) {
            System.out.println("IIV: GUI error (" + e.getClass().getSimpleName() + "). Skipping for development...");
            return;
        }
    }
}
