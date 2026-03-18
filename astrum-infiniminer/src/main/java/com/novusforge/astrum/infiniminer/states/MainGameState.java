package com.novusforge.astrum.infiniminer.states;

import com.novusforge.astrum.infiniminer.*;
import org.joml.Vector3f;

/**
 * MainGameState - The core gameplay state.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class MainGameState extends State {
    
    private boolean mouseInitialized = false;
    private final InfiniminerGame game;

    public MainGameState(InfiniminerGame game) {
        this.game = game;
    }

    @Override
    public void onEnter(String oldState) {
        System.out.println("Entering MainGameState");
        // In LWJGL3, we would disable the mouse cursor here
    }

    @Override
    public void onLeave(String newState) {
        System.out.println("Leaving MainGameState");
        _P.chatEntryBuffer = "";
        _P.chatMode = Defines.ChatMessageType.None;
    }

    @Override
    public String onUpdate(float deltaTime) {
        if (_P == null) return null;

        // Update the current screen effect counter
        _P.screenEffectCounter += deltaTime;

        // Update engines
        _P.skyplaneEngine.update(deltaTime);
        _P.playerEngine.update(deltaTime);
        _P.interfaceEngine.update(deltaTime);
        _P.particleEngine.update(deltaTime);

        // Count down the tool cooldown
        if (_P.playerToolCooldown > 0) {
            _P.playerToolCooldown -= deltaTime;
            if (_P.playerToolCooldown <= 0)
                _P.playerToolCooldown = 0;
        }

        // Handle Mouse Look (Conceptual - depends on InputHandler integration)
        handleMouseLook(deltaTime);

        // Handle Input Actions
        handleInputActions(deltaTime);

        // Update camera position to follow player
        _P.updateCamera();

        return null;
    }

    private void handleMouseLook(float deltaTime) {
        // Logic for mouse movement rotating the camera
        // In LWJGL3, we get deltaX/deltaY from GLFW
        // _P.playerCamera.rotationY -= deltaX * _P.mouseSensitivity;
        // _P.playerCamera.rotationX = clamp(..., _P.playerCamera.rotationX - deltaY * _P.mouseSensitivity);
    }

    private void handleInputActions(float deltaTime) {
        if (_P.playerDead) return;

        // Digging
        if (game.getInputHandler().isMouseDown(0) && _P.playerToolCooldown == 0) {
            if (_P.playerTools[_P.playerToolSelected] == PlayerTools.Pickaxe) {
                firePickaxe();
            }
        }

        // Radar
        if (_P.playerTools[_P.playerToolSelected] == PlayerTools.ProspectingRadar) {
            // Logic for radar sounds and distance reading
        }
    }

    private void firePickaxe() {
        // Conceptual implementation of firing pickaxe
        _P.playerToolCooldown = 0.55f; // Base cooldown
        if (_P.playerClass == PlayerClass.Miner) {
            _P.playerToolCooldown *= 0.4f;
        }
        
        int[] target = _P.playerEngine.getTargetBlock();
        if (target != null) {
            _P.blockEngine.removeBlock(target[0], target[1], target[2]);
            // Play dig sound, create particles
            _P.particleEngine.createExplosionDebris(new Vector3f(target[0]+0.5f, target[1]+0.5f, target[2]+0.5f));
        }
    }

    @Override
    public void onRender() {
        if (_P == null) return;

        // Render engines in order
        _P.skyplaneEngine.render();
        _P.particleEngine.render();
        // _P.playerEngine.render(); // Render other players
        // _P.blockEngine.render();  // Render world
        _P.interfaceEngine.render(); // Render UI
    }
}
