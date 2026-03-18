package astrum.game;

import astrum.core.ECS;
import astrum.core.SafetyGuardian;
import astrum.engine.VulkanRenderer;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Main Entry Point for Project Astrum.
 * Orchestrates the ECS, SafetyGuardian, and Vulkan Engine.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Project Astrum (Java 22 + Vulkan)...");

        // 1. Initialize SafetyGuardian
        SafetyGuardian guardian = new SafetyGuardian();
        if (!guardian.validateAction("startup")) {
            System.err.println("SafetyGuardian blocked startup.");
            return;
        }

        // 2. Initialize ECS
        ECS ecs = new ECS();

        // 3. Initialize Vulkan Renderer
        VulkanRenderer renderer = new VulkanRenderer();
        try {
            renderer.init();
            
            // Main Loop
            while (!renderer.windowShouldClose()) {
                glfwPollEvents();
                // Render logic here
                // For now, break immediately to avoid an empty window hang in tests
                break; 
            }
        } finally {
            renderer.cleanup();
        }
    }
}
