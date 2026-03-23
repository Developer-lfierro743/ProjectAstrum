package com.novusforge.astrum.engine;

import org.lwjgl.glfw.*;
import org.lwjgl.vulkan.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Cross-platform surface creation for Project Astrum.
 * Supports: Windows, Linux, macOS (MoltenVK), Android, Web (WebGPU bridge)
 * 
 * Platform detection and surface creation based on Khronos Vulkan Guide.
 */
public class PlatformSurface {

    public enum Platform {
        WINDOWS,
        LINUX,
        MACOS,
        ANDROID,
        WEB,
        UNKNOWN
    }

    private static Platform currentPlatform;

    /**
     * Detect the current platform at runtime.
     */
    public static Platform getPlatform() {
        if (currentPlatform != null) {
            return currentPlatform;
        }

        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            currentPlatform = Platform.WINDOWS;
        } else if (os.contains("linux")) {
            currentPlatform = Platform.LINUX;
        } else if (os.contains("mac")) {
            currentPlatform = Platform.MACOS;
        } else if (os.contains("android")) {
            currentPlatform = Platform.ANDROID;
        } else {
            currentPlatform = Platform.UNKNOWN;
        }
        
        return currentPlatform;
    }

    /**
     * Get required instance extensions for the current platform.
     * Returns array of extension names needed for surface creation.
     */
    public static String[] getRequiredInstanceExtensions() {
        Platform platform = getPlatform();
        
        switch (platform) {
            case WINDOWS:
                return new String[] {
                    VK_KHR_SURFACE_EXTENSION_NAME,
                    VK_KHR_WIN32_SURFACE_EXTENSION_NAME
                };
            case LINUX:
                // Try Wayland first, fallback to X11
                return new String[] {
                    VK_KHR_SURFACE_EXTENSION_NAME,
                    VK_KHR_XCB_SURFACE_EXTENSION_NAME,
                    VK_KHR_XLIB_SURFACE_EXTENSION_NAME
                };
            case MACOS:
                // MoltenVK requires specific extensions
                return new String[] {
                    VK_KHR_SURFACE_EXTENSION_NAME,
                    "VK_MVK_macos_surface",
                    VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME,
                    VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME
                };
            case ANDROID:
                return new String[] {
                    VK_KHR_SURFACE_EXTENSION_NAME,
                    VK_KHR_ANDROID_SURFACE_EXTENSION_NAME
                };
            case WEB:
                // WebGPU doesn't use Vulkan instance
                return new String[] {};
            default:
                return new String[] {
                    VK_KHR_SURFACE_EXTENSION_NAME
                };
        }
    }

    /**
     * Check if MoltenVK is needed (macOS/iOS).
     */
    public static boolean requiresMoltenVK() {
        Platform platform = getPlatform();
        return platform == Platform.MACOS;
    }

    /**
     * Load MoltenVK library on macOS.
     * Must be called before vkCreateInstance on macOS.
     */
    public static void loadMoltenVK() {
        if (!requiresMoltenVK()) {
            return;
        }
        
        try {
            // Try to load MoltenVK dynamic library
            System.loadLibrary("MoltenVK");
            System.out.println("[Platform] MoltenVK loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            // Try framework loading on macOS
            try {
                System.load("/Library/Frameworks/MoltenVK.framework/MoltenVK");
                System.out.println("[Platform] MoltenVK loaded from Framework");
            } catch (Exception e2) {
                System.err.println("[Platform] Warning: MoltenVK not found. Vulkan may not work on macOS.");
            }
        }
    }

    /**
     * Create platform-specific surface.
     * 
     * @param instance Vulkan instance
     * @param window GLFW window handle
     * @return Surface handle
     */
    public static long createSurface(VkInstance instance, long window) {
        Platform platform = getPlatform();
        
        switch (platform) {
            case MACOS:
                return createMacOSSurface(instance, window);
            case WINDOWS:
                return createWin32Surface(instance, window);
            case LINUX:
                return createLinuxSurface(instance, window);
            case ANDROID:
                return createAndroidSurface(instance, window);
            case WEB:
                // WebGPU uses different path
                return 0;
            default:
                throw new RuntimeException("Unsupported platform: " + platform);
        }
    }

    /**
     * Create macOS surface via MoltenVK.
     */
    private static long createMacOSSurface(VkInstance instance, long window) {
        // GLFW handles MoltenVK surface creation automatically
        // Just need to ensure MoltenVK is loaded
        loadMoltenVK();
        
        long[] surface = new long[1];
        int err = GLFWVulkan.glfwCreateWindowSurface(instance, window, null, surface);
        
        if (err != VK_SUCCESS) {
            throw new RuntimeException("Failed to create macOS surface: " + err);
        }
        
        System.out.println("[Platform] macOS surface created via MoltenVK");
        return surface[0];
    }

    /**
     * Create Windows Win32 surface.
     */
    private static long createWin32Surface(VkInstance instance, long window) {
        long[] surface = new long[1];
        int err = GLFWVulkan.glfwCreateWindowSurface(instance, window, null, surface);
        
        if (err != VK_SUCCESS) {
            throw new RuntimeException("Failed to create Win32 surface: " + err);
        }
        
        System.out.println("[Platform] Windows surface created");
        return surface[0];
    }

    /**
     * Create Linux surface (X11/Wayland).
     */
    private static long createLinuxSurface(VkInstance instance, long window) {
        long[] surface = new long[1];
        int err = GLFWVulkan.glfwCreateWindowSurface(instance, window, null, surface);
        
        if (err != VK_SUCCESS) {
            throw new RuntimeException("Failed to create Linux surface: " + err);
        }
        
        System.out.println("[Platform] Linux surface created");
        return surface[0];
    }

    /**
     * Create Android surface.
     * Requires ANativeWindow from Android NDK.
     */
    private static long createAndroidSurface(VkInstance instance, long window) {
        // On Android, we need to use ANativeWindow
        // This requires Android-specific JNI code
        // For now, use GLFW as fallback (works with Android GLFW port)
        long[] surface = new long[1];
        int err = GLFWVulkan.glfwCreateWindowSurface(instance, window, null, surface);
        
        if (err != VK_SUCCESS) {
            throw new RuntimeException("Failed to create Android surface: " + err);
        }
        
        System.out.println("[Platform] Android surface created");
        return surface[0];
    }

    /**
     * Get WebGPU context for web browsers.
     * This is a placeholder for WebGPU integration.
     */
    public static Object getWebGPUContext() {
        if (getPlatform() != Platform.WEB) {
            return null;
        }
        
        // WebGPU context creation would happen here via JNI/WebAssembly
        System.out.println("[Platform] WebGPU context requested");
        return null;
    }

    /**
     * Check if Vulkan is supported on this platform.
     */
    public static boolean isVulkanSupported() {
        Platform platform = getPlatform();
        
        switch (platform) {
            case WINDOWS:
            case LINUX:
            case ANDROID:
                return true;
            case MACOS:
                // Requires MoltenVK
                return true; // With MoltenVK
            case WEB:
                // Uses WebGPU instead
                return false;
            default:
                return false;
        }
    }

    /**
     * Get recommended present mode for platform.
     */
    public static int getRecommendedPresentMode() {
        Platform platform = getPlatform();
        
        switch (platform) {
            case ANDROID:
                // Android often has issues with MAILBOX
                return VK_PRESENT_MODE_FIFO_KHR;
            case MACOS:
                // MoltenVK works best with FIFO
                return VK_PRESENT_MODE_FIFO_KHR;
            default:
                // Desktop: prefer MAILBOX (triple buffering)
                return VK_PRESENT_MODE_MAILBOX_KHR;
        }
    }

    /**
     * Get optimal image count for platform.
     */
    public static int getOptimalImageCount(int minImageCount) {
        Platform platform = getPlatform();
        
        switch (platform) {
            case ANDROID:
                // Android benefits from more images
                return Math.max(minImageCount, 3);
            case MACOS:
                // MoltenVK works well with 2-3 images
                return Math.max(minImageCount, 2);
            default:
                // Desktop: standard triple buffering
                return Math.max(minImageCount, 3);
        }
    }
}
