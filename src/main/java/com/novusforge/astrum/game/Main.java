package com.novusforge.astrum.game;

import com.novusforge.astrum.core.*;
import com.novusforge.astrum.engine.*;
import com.novusforge.astrum.world.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Project Astrum - Pre-Classic (Cave Game)
 * Main Entry Point with Account System and IIV Verification
 * 
 * Flow:
 * 1. Account System (Login/Register)
 * 2. IIV Questionnaire (Identity Intent Verification)
 * 3. SafetyGuardian Initialization
 * 4. Launch Game
 */
public class Main {
    
    private static final String TITLE = "Astrum - Pre-Classic (Cave Game)";
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME = 1_000_000_000 / TARGET_FPS;
    private static final String IIV_FILE = System.getProperty("user.home") + "/iiv_result.dat";

    public static void main(String[] args) {
        // Print banner
        System.out.println("=".repeat(50));
        System.out.println("  ASTRUM - Pre-Classic (Cave Game)");
        System.out.println("  Project Astrum v0.0.1");
        System.out.println("  By Novusforge Studios");
        System.out.println("=".repeat(50));
        System.out.println();

        // Step 1: Account System Check or Launch
        checkAccountSession();

        // Step 2: IIV Check or Launch
        checkIdentityVerification();

        // Step 3: SafetyGuardian Init
        SafetyGuardian guardian = new SafetyGuardian();
        SafetyGuardian.ActionContext startupContext = new SafetyGuardian.ActionContext("startup", "system", "engine");
        if (guardian.validate(startupContext).decision() == SafetyGuardian.SafetyResult.Decision.BLOCK) {
            System.err.println("CRITICAL: SafetyGuardian blocked startup. Engine halted.");
            return;
        }
        System.out.println("  Player         : " + SessionManager.getUsername());
        System.out.println("  SafetyGuardian : ONLINE");
        System.out.println("  IIV Framework  : READY");
        System.out.println("=".repeat(50));
        System.out.println();

        // Step 4: Launch Game
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
                
                // Get visible meshes
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

    /**
     * Step 1: Account System - Login or Register
     */
    private static void checkAccountSession() {
        // Check if session already exists
        if (SessionManager.isLoggedIn()) {
            System.out.println("Account: Session found. Logged in as " + SessionManager.getUsername());
            return;
        }

        // Check DISPLAY for headless mode
        String display = System.getenv("DISPLAY");
        if (display == null || display.isEmpty()) {
            System.out.println("Account: DISPLAY not set. Creating dev session...");
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
                System.out.println("Account: Login cancelled. Creating dev session...");
                SessionManager.saveSession("DevPlayer", 0);
                System.out.println("Account: Dev session created. Logged in as DevPlayer");
            } else {
                System.out.println("Account: Login successful. Welcome " + SessionManager.getUsername());
            }
        } catch (java.awt.AWTError e) {
            System.out.println("Account: X11 connection failed. Creating dev session...");
            SessionManager.saveSession("DevPlayer", 0);
            System.out.println("Account: Dev session created. Logged in as DevPlayer");
        } catch (Exception e) {
            System.out.println("Account: Error. Creating dev session...");
            SessionManager.saveSession("DevPlayer", 0);
            System.out.println("Account: Dev session created. Logged in as DevPlayer");
        }
    }

    /**
     * Step 2: IIV Questionnaire - Identity Intent Verification
     */
    private static void checkIdentityVerification() {
        File file = new File(IIV_FILE);
        
        // Check if already verified
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

        // Check DISPLAY for headless mode
        String display = System.getenv("DISPLAY");
        if (display == null || display.isEmpty()) {
            System.out.println("IIV: DISPLAY not set. Skipping for development...");
            return;
        }

        // Launch IIV Questionnaire GUI
        try {
            System.out.println("IIV: Launching Identity Intent Verification...");
            SwingUtilities.invokeAndWait(() -> {
                new IIVQuestionnaire();
            });

            // Verify result saved
            if (!file.exists()) {
                System.out.println("IIV: Verification incomplete. Skipping...");
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
            System.out.println("IIV: X11 connection failed. Skipping for development...");
        } catch (Exception e) {
            System.out.println("IIV: Error. Skipping for development...");
        }
    }
}
