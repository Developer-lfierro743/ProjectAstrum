package com.novusforge.astrum.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Utility class for generating a unit cube mesh for Vulkan rendering.
 * Cube is axis-aligned from (0,0,0) to (1,1,1).
 * Each face consists of 2 triangles (6 vertices).
 * Total: 6 faces × 6 vertices = 36 vertices.
 * 
 * Vertex format: position (x,y,z) + color (r,g,b) = 6 floats = 24 bytes
 * Matches the VulkanRenderer vertex input layout.
 */
public class CubeMesh {

    // Vertex stride: 6 floats per vertex (3 pos + 3 color)
    public static final int VERTEX_SIZE = 6;
    
    // Total vertices for a complete cube (6 faces × 2 triangles × 3 vertices)
    public static final int TOTAL_VERTICES = 36;
    
    // Total indices (if using indexed rendering)
    public static final int TOTAL_INDICES = 36;

    /**
     * Generates a complete cube with all 6 faces.
     * Each face has a different color for easy identification.
     * 
     * @param x World X position
     * @param y World Y position
     * @param z World Z position
     * @return FloatBuffer containing vertex data (position + color)
     */
    public static FloatBuffer generateCube(float x, float y, float z) {
        return generateCube(x, y, z, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Generates a complete cube with all 6 faces and custom colors per face.
     * 
     * @param x World X position
     * @param y World Y position  
     * @param z World Z position
     * @param size Cube size (scale)
     * @return FloatBuffer containing vertex data
     */
    public static FloatBuffer generateCube(float x, float y, float z, float size) {
        return generateCube(x, y, z, size, size, size, 1.0f);
    }

    /**
     * Generates a complete cube with custom position, size and brightness.
     * 
     * @param x World X position
     * @param y World Y position
     * @param z World Z position
     * @param width Width (X scale)
     * @param height Height (Y scale)
     * @param depth Depth (Z scale)
     * @param brightness Overall brightness multiplier (0.0 - 1.0)
     * @return FloatBuffer containing vertex data
     */
    public static FloatBuffer generateCube(float x, float y, float z, 
                                           float width, float height, float depth, 
                                           float brightness) {
        float[] vertices = new float[TOTAL_VERTICES * VERTEX_SIZE];
        int idx = 0;

        // Face colors (RGB) with brightness applied
        float[] frontColor  = { 1.0f, 0.0f, 0.0f };  // Red
        float[] backColor   = { 0.0f, 1.0f, 0.0f };  // Green
        float[] topColor    = { 0.0f, 0.0f, 1.0f };  // Blue
        float[] bottomColor = { 1.0f, 1.0f, 0.0f };  // Yellow
        float[] leftColor   = { 1.0f, 0.0f, 1.0f };  // Magenta
        float[] rightColor  = { 0.0f, 1.0f, 1.0f };  // Cyan

        // FRONT face (+Z) - 2 triangles
        // Triangle 1
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = frontColor[0] * brightness; vertices[idx++] = frontColor[1] * brightness; vertices[idx++] = frontColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = frontColor[0] * brightness; vertices[idx++] = frontColor[1] * brightness; vertices[idx++] = frontColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = frontColor[0] * brightness; vertices[idx++] = frontColor[1] * brightness; vertices[idx++] = frontColor[2] * brightness;
        
        // Triangle 2
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = frontColor[0] * brightness; vertices[idx++] = frontColor[1] * brightness; vertices[idx++] = frontColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = frontColor[0] * brightness; vertices[idx++] = frontColor[1] * brightness; vertices[idx++] = frontColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = frontColor[0] * brightness; vertices[idx++] = frontColor[1] * brightness; vertices[idx++] = frontColor[2] * brightness;

        // BACK face (-Z) - 2 triangles
        // Triangle 1
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = backColor[0] * brightness; vertices[idx++] = backColor[1] * brightness; vertices[idx++] = backColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = backColor[0] * brightness; vertices[idx++] = backColor[1] * brightness; vertices[idx++] = backColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = backColor[0] * brightness; vertices[idx++] = backColor[1] * brightness; vertices[idx++] = backColor[2] * brightness;
        
        // Triangle 2
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = backColor[0] * brightness; vertices[idx++] = backColor[1] * brightness; vertices[idx++] = backColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = backColor[0] * brightness; vertices[idx++] = backColor[1] * brightness; vertices[idx++] = backColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = backColor[0] * brightness; vertices[idx++] = backColor[1] * brightness; vertices[idx++] = backColor[2] * brightness;

        // TOP face (+Y) - 2 triangles
        // Triangle 1
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = topColor[0] * brightness; vertices[idx++] = topColor[1] * brightness; vertices[idx++] = topColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = topColor[0] * brightness; vertices[idx++] = topColor[1] * brightness; vertices[idx++] = topColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = topColor[0] * brightness; vertices[idx++] = topColor[1] * brightness; vertices[idx++] = topColor[2] * brightness;
        
        // Triangle 2
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = topColor[0] * brightness; vertices[idx++] = topColor[1] * brightness; vertices[idx++] = topColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = topColor[0] * brightness; vertices[idx++] = topColor[1] * brightness; vertices[idx++] = topColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = topColor[0] * brightness; vertices[idx++] = topColor[1] * brightness; vertices[idx++] = topColor[2] * brightness;

        // BOTTOM face (-Y) - 2 triangles
        // Triangle 1
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = bottomColor[0] * brightness; vertices[idx++] = bottomColor[1] * brightness; vertices[idx++] = bottomColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = bottomColor[0] * brightness; vertices[idx++] = bottomColor[1] * brightness; vertices[idx++] = bottomColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = bottomColor[0] * brightness; vertices[idx++] = bottomColor[1] * brightness; vertices[idx++] = bottomColor[2] * brightness;
        
        // Triangle 2
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = bottomColor[0] * brightness; vertices[idx++] = bottomColor[1] * brightness; vertices[idx++] = bottomColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = bottomColor[0] * brightness; vertices[idx++] = bottomColor[1] * brightness; vertices[idx++] = bottomColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = bottomColor[0] * brightness; vertices[idx++] = bottomColor[1] * brightness; vertices[idx++] = bottomColor[2] * brightness;

        // LEFT face (-X) - 2 triangles
        // Triangle 1
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = leftColor[0] * brightness; vertices[idx++] = leftColor[1] * brightness; vertices[idx++] = leftColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = leftColor[0] * brightness; vertices[idx++] = leftColor[1] * brightness; vertices[idx++] = leftColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = leftColor[0] * brightness; vertices[idx++] = leftColor[1] * brightness; vertices[idx++] = leftColor[2] * brightness;
        
        // Triangle 2
        vertices[idx++] = x;              vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = leftColor[0] * brightness; vertices[idx++] = leftColor[1] * brightness; vertices[idx++] = leftColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = leftColor[0] * brightness; vertices[idx++] = leftColor[1] * brightness; vertices[idx++] = leftColor[2] * brightness;
        
        vertices[idx++] = x;              vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = leftColor[0] * brightness; vertices[idx++] = leftColor[1] * brightness; vertices[idx++] = leftColor[2] * brightness;

        // RIGHT face (+X) - 2 triangles
        // Triangle 1
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = rightColor[0] * brightness; vertices[idx++] = rightColor[1] * brightness; vertices[idx++] = rightColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z;
        vertices[idx++] = rightColor[0] * brightness; vertices[idx++] = rightColor[1] * brightness; vertices[idx++] = rightColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = rightColor[0] * brightness; vertices[idx++] = rightColor[1] * brightness; vertices[idx++] = rightColor[2] * brightness;
        
        // Triangle 2
        vertices[idx++] = x + width;      vertices[idx++] = y;              vertices[idx++] = z + depth;
        vertices[idx++] = rightColor[0] * brightness; vertices[idx++] = rightColor[1] * brightness; vertices[idx++] = rightColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z;
        vertices[idx++] = rightColor[0] * brightness; vertices[idx++] = rightColor[1] * brightness; vertices[idx++] = rightColor[2] * brightness;
        
        vertices[idx++] = x + width;      vertices[idx++] = y + height;     vertices[idx++] = z + depth;
        vertices[idx++] = rightColor[0] * brightness; vertices[idx++] = rightColor[1] * brightness; vertices[idx++] = rightColor[2] * brightness;

        // Create FloatBuffer
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertices.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        buffer.put(vertices);
        buffer.flip();
        
        return buffer;
    }

    /**
     * Generates cube vertex data for a Minecraft-style block with face shading.
     * Top face is brightest, sides are medium, bottom is darkest.
     * 
     * @param x World X position
     * @param y World Y position
     * @param z World Z position
     * @param r Base color red (0-1)
     * @param g Base color green (0-1)
     * @param b Base color blue (0-1)
     * @return FloatBuffer containing vertex data
     */
    public static FloatBuffer generateTexturedCube(float x, float y, float z, float r, float g, float b) {
        float[] vertices = new float[TOTAL_VERTICES * VERTEX_SIZE];
        int idx = 0;

        // Face brightness for simple lighting (top brightest, bottom darkest)
        float topBright    = 1.0f;
        float sideBright   = 0.8f;
        float bottomBright = 0.6f;

        // FRONT face (+Z)
        addFace(vertices, idx, 
            x, y, z + 1,  x + 1, y, z + 1,  x + 1, y + 1, z + 1,  x, y + 1, z + 1,
            r * sideBright, g * sideBright, b * sideBright);
        idx += 24;

        // BACK face (-Z)
        addFace(vertices, idx,
            x + 1, y, z,  x, y, z,  x, y + 1, z,  x + 1, y + 1, z,
            r * sideBright, g * sideBright, b * sideBright);
        idx += 24;

        // TOP face (+Y)
        addFace(vertices, idx,
            x, y + 1, z + 1,  x + 1, y + 1, z + 1,  x + 1, y + 1, z,  x, y + 1, z,
            r * topBright, g * topBright, b * topBright);
        idx += 24;

        // BOTTOM face (-Y)
        addFace(vertices, idx,
            x, y, z,  x + 1, y, z,  x + 1, y, z + 1,  x, y, z + 1,
            r * bottomBright, g * bottomBright, b * bottomBright);
        idx += 24;

        // LEFT face (-X)
        addFace(vertices, idx,
            x, y, z,  x, y, z + 1,  x, y + 1, z + 1,  x, y + 1, z,
            r * sideBright, g * sideBright, b * sideBright);
        idx += 24;

        // RIGHT face (+X)
        addFace(vertices, idx,
            x + 1, y, z + 1,  x + 1, y, z,  x + 1, y + 1, z,  x + 1, y + 1, z + 1,
            r * sideBright, g * sideBright, b * sideBright);

        FloatBuffer buffer = ByteBuffer.allocateDirect(vertices.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        buffer.put(vertices);
        buffer.flip();
        
        return buffer;
    }

    /**
     * Helper method to add a quad (2 triangles) to the vertex array.
     */
    private static void addFace(float[] vertices, int startIdx,
                                float x0, float y0, float z0,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float r, float g, float b) {
        int idx = startIdx;
        
        // Triangle 1
        vertices[idx++] = x0; vertices[idx++] = y0; vertices[idx++] = z0;
        vertices[idx++] = r;  vertices[idx++] = g;  vertices[idx++] = b;
        
        vertices[idx++] = x1; vertices[idx++] = y1; vertices[idx++] = z1;
        vertices[idx++] = r;  vertices[idx++] = g;  vertices[idx++] = b;
        
        vertices[idx++] = x2; vertices[idx++] = y2; vertices[idx++] = z2;
        vertices[idx++] = r;  vertices[idx++] = g;  vertices[idx++] = b;
        
        // Triangle 2
        vertices[idx++] = x0; vertices[idx++] = y0; vertices[idx++] = z0;
        vertices[idx++] = r;  vertices[idx++] = g;  vertices[idx++] = b;
        
        vertices[idx++] = x2; vertices[idx++] = y2; vertices[idx++] = z2;
        vertices[idx++] = r;  vertices[idx++] = g;  vertices[idx++] = b;
        
        vertices[idx++] = x3; vertices[idx++] = y3; vertices[idx++] = z3;
        vertices[idx++] = r;  vertices[idx++] = g;  vertices[idx++] = b;
    }

    /**
     * Generates index data for a cube (if using indexed rendering).
     * 6 faces × 6 indices = 36 indices total.
     */
    public static IntBuffer generateIndices() {
        int[] indices = new int[TOTAL_INDICES];
        
        for (int i = 0; i < TOTAL_INDICES; i++) {
            indices[i] = i;
        }
        
        IntBuffer buffer = ByteBuffer.allocateDirect(indices.length * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer();
        buffer.put(indices);
        buffer.flip();
        
        return buffer;
    }
}
