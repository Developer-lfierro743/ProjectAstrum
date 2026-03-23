package com.novusforge.astrum.engine;

import org.joml.Matrix4f;
import java.util.Map;

import com.novusforge.astrum.world.ChunkMesh;

/**
 * Universal Renderer abstraction for Project Astrum.
 * Automatically selects Vulkan or WebGPU based on platform.
 * 
 * Platform Support:
 * - Windows 10/11: Vulkan
 * - Linux: Vulkan
 * - macOS: Vulkan via MoltenVK
 * - Android: Vulkan
 * - Web Browsers: WebGPU (via TeaVM/WASM)
 * 
 * Usage:
 *   IRenderer renderer = RendererFactory.createRenderer();
 *   renderer.init();
 *   renderer.render(view, projection, meshes);
 *   renderer.cleanup();
 */
public interface IRenderer {

    /**
     * Initialize the renderer.
     * @return true if successful
     */
    boolean init();

    /**
     * Render a frame.
     * @param view View matrix
     * @param projection Projection matrix
     * @param meshes Chunk meshes to render
     */
    void render(Matrix4f view, Matrix4f projection, Map<Long, ChunkMesh> meshes);

    /**
     * Check if window should close.
     */
    boolean windowShouldClose();

    /**
     * Get window handle (0 for WebGPU).
     */
    long getWindow();

    /**
     * Get aspect ratio.
     */
    float getAspectRatio();

    /**
     * Cleanup renderer resources.
     */
    void cleanup();

    /**
     * Get the current platform.
     */
    PlatformSurface.Platform getPlatform();

    /**
     * Check if running on web platform.
     */
    boolean isWeb();

    /**
     * Resize renderer for new window size.
     */
    void resize(int width, int height);
}
