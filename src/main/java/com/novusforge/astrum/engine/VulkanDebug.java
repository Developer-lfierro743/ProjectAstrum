package com.novusforge.astrum.engine;

/**
 * Simple Vulkan debug utilities.
 */
public class VulkanDebug {

    /**
     * Translate Vulkan result code to human-readable string.
     */
    public static String translateResult(int result) {
        switch (result) {
            case 0: return "SUCCESS";
            case 1: return "NOT_READY";
            case 2: return "TIMEOUT";
            case 3: return "EVENT_SET";
            case 4: return "EVENT_RESET";
            case 5: return "INCOMPLETE";
            case -1: return "OUT_OF_HOST_MEMORY";
            case -2: return "OUT_OF_DEVICE_MEMORY";
            case -3: return "INITIALIZATION_FAILED";
            case -4: return "DEVICE_LOST";
            case -5: return "MEMORY_MAP_FAILED";
            case -6: return "LAYER_NOT_PRESENT";
            case -7: return "EXTENSION_NOT_PRESENT";
            case -8: return "FEATURE_NOT_PRESENT";
            case -9: return "INCOMPATIBLE_DRIVER";
            case -1000001000: return "OUT_OF_DATE_KHR";
            case 1000001003: return "SUBOPTIMAL_KHR";
            default: return "UNKNOWN(" + result + ")";
        }
    }
}
