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
package com.novusforge.astrum.engine.vulkan;

import com.novusforge.astrum.engine.GPUBuffer;
import com.novusforge.astrum.engine.RenderingContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import java.nio.IntBuffer;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Initializes the Vulkan Instance and Device (Formula Part 1).
 */
public class VulkanContext implements RenderingContext {
    private VkInstance instance;
    private VkPhysicalDevice physicalDevice;
    private VkDevice device;

    @Override
    public void init() {
        createInstance();
        pickPhysicalDevice();
        createLogicalDevice();
    }

    @Override
    public void update() {
        // Frame logic
    }

    @Override
    public GPUBuffer createBuffer(long size, int usage) {
        return null; // Stub
    }

    @Override
    public String getApiName() {
        return "Vulkan";
    }

    private void createInstance() {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8("Project Astrum"))
                    .applicationVersion(VK_MAKE_VERSION(0, 1, 0))
                    .pEngineName(stack.UTF8("AstrumEngine"))
                    .engineVersion(VK_MAKE_VERSION(0, 1, 0))
                    .apiVersion(VK_API_VERSION_1_0);

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
            PointerBuffer pPhysicalDevices = stack.mallocPointer(pDeviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, pDeviceCount, pPhysicalDevices);
            physicalDevice = new VkPhysicalDevice(pPhysicalDevices.get(0), instance);
        }
    }

    private void createLogicalDevice() {
        try (MemoryStack stack = stackPush()) {
            VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                    .queueFamilyIndex(0)
                    .pQueuePriorities(stack.floats(1.0f));

            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(queueCreateInfo)
                    .pEnabledFeatures(VkPhysicalDeviceFeatures.calloc(stack));

            PointerBuffer pDevice = stack.mallocPointer(1);
            if (vkCreateDevice(physicalDevice, createInfo, null, pDevice) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create logical device");
            }
            device = new VkDevice(pDevice.get(0), physicalDevice, createInfo);
        }
    }

    @Override
    public void cleanup() {
        vkDestroyDevice(device, null);
        vkDestroyInstance(instance, null);
    }
}
