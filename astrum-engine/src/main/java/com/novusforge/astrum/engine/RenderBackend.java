package com.novusforge.astrum.engine;

/**
 * Unified interface for hardware-resilient rendering backends (Vulkan, WebGPU, OpenGL ES).
 * Cross-platform abstraction layer as per "The Formula".
 */
public interface RenderBackend {
    
    enum RenderAPI {
        VULKAN,
        WEBGPU,
        OPENGL_ES,
        DIRECTX
    }
    
    enum FeatureLevel {
        LOW,      // Adreno 710 / integrated graphics
        MEDIUM,   // Mid-range discrete
        HIGH      // High-end / RTX
    }
    
    void initialize();
    void render();
    void shutdown();
    
    // Unified Resource & Lifecycle Management
    void resize(int width, int height);
    void clear(float r, float g, float b, float a);
    
    // Abstracted Pipeline State
    void setLegacyMode(boolean enabled);
    
    // Query capabilities
    RenderAPI getAPI();
    FeatureLevel getFeatureLevel();
    boolean supportsGeometryShaders();
    boolean supportsComputeShaders();
    boolean supportsTessellation();
    
    // Memory management
    long getAllocatedVRAM();
    long getRecommendedChunkMeshSize();
    
    WindowProvider getWindowProvider();
}

