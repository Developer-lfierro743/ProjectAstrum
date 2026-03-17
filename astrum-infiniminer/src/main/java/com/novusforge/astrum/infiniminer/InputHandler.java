package com.novusforge.astrum.infiniminer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * InputHandler - Handles keyboard and mouse input.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class InputHandler {
    
    public interface KeyCallback {
        void onKeyPress(int keyCode);
        void onKeyRelease(int keyCode);
    }
    
    public interface MouseCallback {
        void onMouseMove(float deltaX, float deltaY);
        void onMousePress(int button);
        void onMouseRelease(int button);
    }
    
    private Map<Integer, Boolean> keyState = new HashMap<>();
    private Map<Integer, Boolean> prevKeyState = new HashMap<>();
    private Map<Integer, Boolean> mouseState = new HashMap<>();
    private Map<Integer, Boolean> prevMouseState = new HashMap<>();
    
    private float mouseX = 0, mouseY = 0;
    private float prevMouseX = 0, prevMouseY = 0;
    
    private KeyCallback keyCallback;
    private MouseCallback mouseCallback;
    
    // Key codes (simplified)
    public static final int KEY_W = 17;
    public static final int KEY_S = 31;
    public static final int KEY_A = 30;
    public static final int KEY_D = 32;
    public static final int KEY_SPACE = 57;
    public static final int KEY_ESCAPE = 1;
    public static final int KEY_1 = 2;
    public static final int KEY_2 = 3;
    public static final int KEY_3 = 4;
    public static final int KEY_4 = 5;
    public static final int KEY_5 = 6;
    public static final int KEY_6 = 7;
    public static final int KEY_7 = 8;
    public static final int KEY_8 = 9;
    public static final int KEY_9 = 10;
    public static final int KEY_0 = 11;
    
    public static final int MOUSE_LEFT = 0;
    public static final int MOUSE_RIGHT = 1;
    public static final int MOUSE_MIDDLE = 2;
    
    public InputHandler() {
        System.out.println("InputHandler initialized");
    }
    
    public void setKeyCallback(KeyCallback callback) {
        this.keyCallback = callback;
    }
    
    public void setMouseCallback(MouseCallback callback) {
        this.mouseCallback = callback;
    }
    
    public void update() {
        // Store previous state
        prevKeyState.clear();
        prevKeyState.putAll(keyState);
        
        prevMouseState.clear();
        prevMouseState.putAll(mouseState);
        
        // Calculate mouse delta
        float deltaX = mouseX - prevMouseX;
        float deltaY = mouseY - prevMouseY;
        
        if (mouseCallback != null && (deltaX != 0 || deltaY != 0)) {
            mouseCallback.onMouseMove(deltaX, deltaY);
        }
        
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }
    
    public void onKeyPress(int keyCode) {
        keyState.put(keyCode, true);
        
        if (keyCallback != null) {
            keyCallback.onKeyPress(keyCode);
        }
    }
    
    public void onKeyRelease(int keyCode) {
        keyState.put(keyCode, false);
        
        if (keyCallback != null) {
            keyCallback.onKeyRelease(keyCode);
        }
    }
    
    public void onMouseMove(float x, float y) {
        this.mouseX = x;
        this.mouseY = y;
    }
    
    public void onMousePress(int button) {
        mouseState.put(button, true);
        
        if (mouseCallback != null) {
            mouseCallback.onMousePress(button);
        }
    }
    
    public void onMouseRelease(int button) {
        mouseState.put(button, false);
        
        if (mouseCallback != null) {
            mouseCallback.onMouseRelease(button);
        }
    }
    
    public boolean isKeyDown(int keyCode) {
        return keyState.getOrDefault(keyCode, false);
    }
    
    public boolean isKeyPressed(int keyCode) {
        return isKeyDown(keyCode) && !prevKeyState.getOrDefault(keyCode, false);
    }
    
    public boolean isKeyReleased(int keyCode) {
        return !isKeyDown(keyCode) && prevKeyState.getOrDefault(keyCode, false);
    }
    
    public boolean isMouseDown(int button) {
        return mouseState.getOrDefault(button, false);
    }
    
    public boolean isMousePressed(int button) {
        return isMouseDown(button) && !prevMouseState.getOrDefault(button, false);
    }
    
    public float getMouseX() {
        return mouseX;
    }
    
    public float getMouseY() {
        return mouseY;
    }
    
    public float getMouseDeltaX() {
        return mouseX - prevMouseX;
    }
    
    public float getMouseDeltaY() {
        return mouseY - prevMouseY;
    }
    
    public void reset() {
        keyState.clear();
        prevKeyState.clear();
        mouseState.clear();
        prevMouseState.clear();
        mouseX = 0;
        mouseY = 0;
        prevMouseX = 0;
        prevMouseY = 0;
    }
}
