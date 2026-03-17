package com.novusforge.astrum.engine.buffer;

import java.nio.ByteBuffer;

/**
 * Unified buffer abstraction for cross-platform rendering.
 * Works with Vulkan, WebGPU, and OpenGL ES.
 */
public abstract class RenderBuffer {
    
    public enum BufferType {
        VERTEX_BUFFER,
        INDEX_BUFFER,
        UNIFORM_BUFFER,
        STORAGE_BUFFER,
        TEXTURE_BUFFER
    }
    
    public enum BufferUsage {
        STATIC,    // Rarely updated
        DYNAMIC,   // Updated frequently
        STREAM     // Updated every frame
    }
    
    protected final BufferType type;
    protected final BufferUsage usage;
    protected long size;
    protected boolean isMapped;
    
    public RenderBuffer(BufferType type, BufferUsage usage, long size) {
        this.type = type;
        this.usage = usage;
        this.size = size;
    }
    
    public abstract void upload(ByteBuffer data);
    public abstract void upload(ByteBuffer data, long offset);
    public abstract void destroy();
    
    public BufferType getType() { return type; }
    public BufferUsage getUsage() { return usage; }
    public long getSize() { return size; }
    public boolean isMapped() { return isMapped; }
    
    public abstract void bind();
    public abstract void bind(long offset);
}
