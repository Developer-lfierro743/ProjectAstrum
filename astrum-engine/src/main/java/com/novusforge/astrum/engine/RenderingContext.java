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

/**
 * Common interface for all rendering backends (Vulkan, WebGPU, etc.)
 */
public interface RenderingContext {
    
    /**
     * Initializes the graphics API and platform window.
     */
    void init();
    
    /**
     * Cleans up all GPU-side resources.
     */
    void cleanup();
    
    /**
     * Updates the rendering state (swapchain, etc.) for the current frame.
     */
    void update();
    
    /**
     * Creates a new GPU buffer with the specified size and usage.
     * @param size The size in bytes.
     * @param usage Usage flags (Vertex, Index, etc.)
     * @return The created GPU buffer.
     */
    GPUBuffer createBuffer(long size, int usage);
    
    /**
     * @return The name of the rendering API (e.g., "Vulkan", "WebGPU")
     */
    String getApiName();
}
