package astrum.engine;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Vulkan Renderer for Project Astrum.
 * High-performance, RAW Vulkan bootstrap using LWJGL 3.
 */
public class VulkanRenderer {
    private long window;
    private VkInstance instance;

    public void init() {
        initWindow();
        initVulkan();
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public long getWindow() {
        return window;
    }

    private void initWindow() {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(1280, 720, "Project Astrum", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create GLFW window");
        }
    }

    private void initVulkan() {
        createInstance();
    }

    private void createInstance() {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8Safe("Project Astrum"))
                    .applicationVersion(VK_MAKE_VERSION(1, 0, 0))
                    .pEngineName(stack.UTF8Safe("Astrum Engine"))
                    .engineVersion(VK_MAKE_VERSION(1, 0, 0))
                    .apiVersion(VK_API_VERSION_1_0);

            PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
            if (requiredExtensions == null) {
                throw new RuntimeException("Failed to find required Vulkan extensions");
            }

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(appInfo)
                    .ppEnabledExtensionNames(requiredExtensions);

            PointerBuffer pInstance = stack.mallocPointer(1);
            if (vkCreateInstance(createInfo, null, pInstance) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Vulkan instance");
            }
            instance = new VkInstance(pInstance.get(0), createInfo);
            System.out.println("Vulkan Instance created successfully.");
        }
    }

    public void cleanup() {
        vkDestroyInstance(instance, null);
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
