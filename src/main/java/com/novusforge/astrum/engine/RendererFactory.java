package com.novusforge.astrum.engine;

import org.joml.Matrix4f;
import java.util.Map;

import com.novusforge.astrum.world.ChunkMesh;

/**
 * Factory for creating the appropriate renderer based on platform.
 * 
 * Usage:
 *   IRenderer renderer = RendererFactory.createRenderer();
 *   renderer.init();
 */
public class RendererFactory {

    private static IRenderer instance;

    /**
     * Create renderer for current platform.
     * Automatically selects Vulkan or WebGPU.
     */
    public static IRenderer createRenderer() {
        if (instance != null) {
            return instance;
        }

        PlatformSurface.Platform platform = PlatformSurface.getPlatform();
        System.out.println("[RendererFactory] Detected platform: " + platform);

        switch (platform) {
            case WEB:
                // Use WebGPU for web browsers
                System.out.println("[RendererFactory] Creating WebGPU renderer");
                WebGPURenderer webgpu = new WebGPURenderer();
                instance = new WebGPUWrapper(webgpu);
                return instance;

            case WINDOWS:
            case LINUX:
            case MACOS:
            case ANDROID:
            default:
                // Use Vulkan for all native platforms
                System.out.println("[RendererFactory] Creating Vulkan renderer");
                VulkanRenderer vulkan = new VulkanRenderer();
                instance = new VulkanWrapper(vulkan);
                return instance;
        }
    }

    /**
     * Get existing renderer instance.
     */
    public static IRenderer getInstance() {
        return instance;
    }

    /**
     * Vulkan wrapper implementing IRenderer.
     */
    private static class VulkanWrapper implements IRenderer {
        private final VulkanRenderer vulkan;

        public VulkanWrapper(VulkanRenderer vulkan) {
            this.vulkan = vulkan;
        }

        @Override
        public boolean init() {
            vulkan.init();
            return true;
        }

        @Override
        public void render(Matrix4f view, Matrix4f projection, Map<Long, ChunkMesh> meshes) {
            vulkan.render(view, projection, meshes);
        }

        @Override
        public boolean windowShouldClose() {
            return vulkan.windowShouldClose();
        }

        @Override
        public long getWindow() {
            return vulkan.getWindow();
        }

        @Override
        public float getAspectRatio() {
            return vulkan.getAspectRatio();
        }

        @Override
        public void cleanup() {
            vulkan.cleanup();
        }

        @Override
        public PlatformSurface.Platform getPlatform() {
            return PlatformSurface.getPlatform();
        }

        @Override
        public boolean isWeb() {
            return false;
        }

        @Override
        public void resize(int width, int height) {
            // Vulkan handles resize via framebuffer callback
        }
    }

    /**
     * WebGPU wrapper implementing IRenderer.
     */
    private static class WebGPUWrapper implements IRenderer {
        private final WebGPURenderer webgpu;

        public WebGPUWrapper(WebGPURenderer webgpu) {
            this.webgpu = webgpu;
        }

        @Override
        public boolean init() {
            return webgpu.init();
        }

        @Override
        public void render(Matrix4f view, Matrix4f projection, Map<Long, ChunkMesh> meshes) {
            webgpu.beginFrame();
            // Render meshes via WebGPU
            // (implementation would convert ChunkMesh to WebGPU buffers)
            webgpu.endFrame();
        }

        @Override
        public boolean windowShouldClose() {
            // Web doesn't have window close in same way
            return false;
        }

        @Override
        public long getWindow() {
            return 0; // No GLFW window for WebGPU
        }

        @Override
        public float getAspectRatio() {
            return (float) 1280 / 720;
        }

        @Override
        public void cleanup() {
            webgpu.cleanup();
        }

        @Override
        public PlatformSurface.Platform getPlatform() {
            return PlatformSurface.WEB;
        }

        @Override
        public boolean isWeb() {
            return true;
        }

        @Override
        public void resize(int width, int height) {
            webgpu.resize(width, height);
        }
    }
}
