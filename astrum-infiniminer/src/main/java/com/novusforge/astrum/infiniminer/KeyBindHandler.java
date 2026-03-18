package com.novusforge.astrum.infiniminer;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * KeyBindHandler - Maps keys and mouse buttons to game actions.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class KeyBindHandler {
    private final Map<Integer, Defines.Buttons> keyMap = new HashMap<>();
    private final Map<Integer, Defines.Buttons> mouseMap = new HashMap<>();
    private final InputHandler inputHandler;

    public KeyBindHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        setupDefaultBinds();
    }

    private void setupDefaultBinds() {
        // Keyboard binds
        keyMap.put(GLFW_KEY_W, Defines.Buttons.Forward);
        keyMap.put(GLFW_KEY_S, Defines.Buttons.Backward);
        keyMap.put(GLFW_KEY_A, Defines.Buttons.Left);
        keyMap.put(GLFW_KEY_D, Defines.Buttons.Right);
        keyMap.put(GLFW_KEY_LEFT_SHIFT, Defines.Buttons.Sprint);
        keyMap.put(GLFW_KEY_SPACE, Defines.Buttons.Jump);
        keyMap.put(GLFW_KEY_LEFT_CONTROL, Defines.Buttons.Crouch);
        
        keyMap.put(GLFW_KEY_T, Defines.Buttons.SayAll);
        keyMap.put(GLFW_KEY_Y, Defines.Buttons.SayTeam);
        keyMap.put(GLFW_KEY_M, Defines.Buttons.ChangeClass);
        keyMap.put(GLFW_KEY_N, Defines.Buttons.ChangeTeam);
        keyMap.put(GLFW_KEY_P, Defines.Buttons.Ping);
        
        keyMap.put(GLFW_KEY_1, Defines.Buttons.Tool1);
        keyMap.put(GLFW_KEY_2, Defines.Buttons.Tool2);
        keyMap.put(GLFW_KEY_3, Defines.Buttons.Tool3);
        keyMap.put(GLFW_KEY_4, Defines.Buttons.Tool4);
        keyMap.put(GLFW_KEY_5, Defines.Buttons.Tool5);
        
        // Mouse binds
        mouseMap.put(GLFW_MOUSE_BUTTON_LEFT, Defines.Buttons.Fire);
        mouseMap.put(GLFW_MOUSE_BUTTON_RIGHT, Defines.Buttons.AltFire);
    }

    public boolean isPressed(Defines.Buttons button) {
        // Check keys
        for (Map.Entry<Integer, Defines.Buttons> entry : keyMap.entrySet()) {
            if (entry.getValue() == button && inputHandler.isKeyDown(entry.getKey())) {
                return true;
            }
        }
        // Check mouse
        for (Map.Entry<Integer, Defines.Buttons> entry : mouseMap.entrySet()) {
            if (entry.getValue() == button && inputHandler.isMouseDown(entry.getKey())) {
                return true;
            }
        }
        return false;
    }
    
    public Defines.Buttons getBound(int key) {
        return keyMap.getOrDefault(key, Defines.Buttons.None);
    }
    
    public Defines.Buttons getBoundMouse(int button) {
        return mouseMap.getOrDefault(button, Defines.Buttons.None);
    }
}
