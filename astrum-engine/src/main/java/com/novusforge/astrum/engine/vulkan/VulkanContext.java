package com.novusforge.astrum.engine.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import java.nio.IntBuffer;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Initializes the Vulkan Instance and Device (Formula Part 1).
 * Foundation for high-performance graphics on modern GPUs and RTX cards.
 */
public class VulkanContext {
    private VkInstance instance;
    private VkPhysicalDevice physicalDevice;
    private VkDevice device;

    public void init() {
        createInstance();
        pickPhysicalDevice();
        createLogicalDevice();
    }

    private void createInstance() {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8("Project Astrum"))
                    .applicationVersion(VK_MAKE_VERSION(0, 1, 0))
                    .pEngineName(stack.UTF8("AstrumEngine"))
                    .engineVersion(VK_MAKE_VERSION(0, 1, 0))
                    .apiVersion(VK_API_VERSION_1_0); // Use 1.0 for maximum compatibility in initial setup

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(appInfo);

            PointerBuffer pInstance = stack.mallocPointer(1);
            if (vkCreateInstance(createInfo, null, pInstance) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Vulkan instance");
            }
            instance = new VkInstance(pInstance.get(0), createInfo);
        }
    }

    private void pickPhysicalDevice() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pDeviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, pDeviceCount, null);

            if (pDeviceCount.get(0) == 0) {
                throw new RuntimeException("Failed to find GPUs with Vulkan support");
            }

            PointerBuffer pPhysicalDevices = stack.mallocPointer(pDeviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, pDeviceCount, pPhysicalDevices);
            
            // For now, pick the first discrete GPU or the first available
            physicalDevice = new VkPhysicalDevice(pPhysicalDevices.get(0), instance);
        }
    }

    private void createLogicalDevice() {
        try (MemoryStack stack = stackPush()) {
            // Initial simple queue setup
            VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                    .queueFamilyIndex(0) // Simplified
                    .pQueuePriorities(stack.floats(1.0f));

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            
            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(queueCreateInfo)
                    .pEnabledFeatures(deviceFeatures);

            PointerBuffer pDevice = stack.mallocPointer(1);
            if (vkCreateDevice(physicalDevice, createInfo, null, pDevice) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create logical device");
            }
            device = new VkDevice(pDevice.get(0), physicalDevice, createInfo);
        }
    }

    public void cleanup() {
        vkDestroyDevice(device, null);
        vkDestroyInstance(instance, null);
    }
}
