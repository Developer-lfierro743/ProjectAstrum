package com.novusforge.astrum.engine.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Utility for loading SPIR-V shaders into Vulkan shader modules.
 */
public class VulkanShaderLoader {

    public static long createShaderModule(VkDevice device, String resourcePath) {
        try {
            byte[] shaderCode = loadShaderResource(resourcePath);
            return createShaderModule(device, shaderCode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader resource: " + resourcePath, e);
        }
    }

    private static byte[] loadShaderResource(String path) throws IOException {
        try (InputStream is = VulkanShaderLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Resource not found: " + path);
            }
            return is.readAllBytes();
        }
    }

    private static long createShaderModule(VkDevice device, byte[] code) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.malloc(code.length);
            buffer.put(code);
            buffer.flip();

            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
            createInfo.pCode(buffer);

            LongBuffer pShaderModule = stack.mallocLong(1);
            if (vkCreateShaderModule(device, createInfo, null, pShaderModule) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create shader module");
            }

            return pShaderModule.get(0);
        }
    }
}
