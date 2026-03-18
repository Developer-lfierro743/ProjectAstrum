package com.novusforge.astrum.infiniminer.engines;

import com.novusforge.astrum.infiniminer.BlockType;
import com.novusforge.astrum.infiniminer.Defines;
import com.novusforge.astrum.infiniminer.PropertyBag;
import org.joml.Vector3f;

/**
 * PlayerEngine - Handles player physics and collision.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class PlayerEngine {
    
    private BlockEngine blockEngine;
    private PropertyBag _P;
    
    private final boolean[] keyState = new boolean[256];
    private boolean mouseLocked = false;
    
    // Player physics constants (Moved to Defines, but kept here for convenience if needed)
    private static final float PLAYER_RADIUS = 0.3f;
    private static final float PLAYER_HEIGHT = 1.8f;
    
    public PlayerEngine(BlockEngine blockEngine, PropertyBag propertyBag) {
        this.blockEngine = blockEngine;
        this._P = propertyBag;
        System.out.println("PlayerEngine initialized");
    }
    
    public void update(float deltaTime) {
        if (_P.playerDead) return;

        float moveSpeed = Defines.MOVESPEED;
        
        float moveX = 0, moveZ = 0;
        
        // Basic input mapping for now
        if (keyState[17]) moveZ -= 1; // W
        if (keyState[31]) moveZ += 1; // S
        if (keyState[30]) moveX -= 1; // A
        if (keyState[32]) moveX += 1; // D
        
        // Normalize diagonal movement
        if (moveX != 0 && moveZ != 0) {
            float len = (float) Math.sqrt(moveX * moveX + moveZ * moveZ);
            moveX /= len;
            moveZ /= len;
        }
        
        // Apply movement relative to player rotation
        float sinY = (float) Math.sin(_P.playerCamera.rotationY);
        float cosY = (float) Math.cos(_P.playerCamera.rotationY);
        
        _P.playerVelocity.x = (sinY * moveZ + cosY * moveX) * moveSpeed;
        _P.playerVelocity.z = (cosY * moveZ - sinY * moveX) * moveSpeed;
        
        // Apply gravity
        _P.playerVelocity.y += Defines.GRAVITY * deltaTime;
        
        // Move and collide
        movePlayer(deltaTime);
        
        // Keep player in bounds
        clampPosition();
    }
    
    private void movePlayer(float deltaTime) {
        float newX = _P.playerPosition.x + _P.playerVelocity.x * deltaTime;
        float newY = _P.playerPosition.y + _P.playerVelocity.y * deltaTime;
        float newZ = _P.playerPosition.z + _P.playerVelocity.z * deltaTime;
        
        // Check X collision
        if (!checkCollision(newX, _P.playerPosition.y, _P.playerPosition.z)) {
            _P.playerPosition.x = newX;
        } else {
            _P.playerVelocity.x = 0;
        }
        
        // Check Y collision
        if (!checkCollision(_P.playerPosition.x, newY, _P.playerPosition.z)) {
            _P.playerPosition.y = newY;
        } else {
            if (_P.playerVelocity.y < 0) {
                // grounded = true; // Should be in PropertyBag or managed here
            }
            _P.playerVelocity.y = 0;
        }
        
        // Check Z collision
        if (!checkCollision(_P.playerPosition.x, _P.playerPosition.y, newZ)) {
            _P.playerPosition.z = newZ;
        } else {
            _P.playerVelocity.z = 0;
        }
    }
    
    private boolean checkCollision(float x, float y, float z) {
        int ix = (int) Math.floor(x);
        int iy = (int) Math.floor(y);
        int iz = (int) Math.floor(z);
        
        // Check multiple points around player
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockType block = blockEngine.getBlock(ix + dx, iy + dy, iz + dz);
                    if (block.isSolid()) {
                        // Simple AABB collision
                        float blockMinX = ix + dx;
                        float blockMaxX = blockMinX + 1;
                        float blockMinY = iy + dy;
                        float blockMaxY = blockMinY + 1;
                        float blockMinZ = iz + dz;
                        float blockMaxZ = blockMinZ + 1;
                        
                        if (x + PLAYER_RADIUS > blockMinX && x - PLAYER_RADIUS < blockMaxX &&
                            y > blockMinY && y - PLAYER_HEIGHT < blockMaxY &&
                            z + PLAYER_RADIUS > blockMinZ && z - PLAYER_RADIUS < blockMaxZ) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private void clampPosition() {
        _P.playerPosition.x = Math.max(0.5f, Math.min(Defines.MAP_SIZE_X - 0.5f, _P.playerPosition.x));
        _P.playerPosition.y = Math.max(-30, Math.min(Defines.MAP_SIZE_Y + 10, _P.playerPosition.y));
        _P.playerPosition.z = Math.max(0.5f, Math.min(Defines.MAP_SIZE_Z - 0.5f, _P.playerPosition.z));
    }
    
    public void onKeyPress(int keyCode) {
        if (keyCode >= 0 && keyCode < 256) {
            keyState[keyCode] = true;
        }
        
        // Jump
        if (keyCode == 57) { // Space
             _P.playerVelocity.y = Defines.JUMPVELOCITY;
        }
    }
    
    public void onKeyRelease(int keyCode) {
        if (keyCode >= 0 && keyCode < 256) {
            keyState[keyCode] = false;
        }
    }
    
    public void onMouseMove(float deltaX, float deltaY) {
        if (mouseLocked) {
            _P.playerCamera.rotationY += deltaX * _P.mouseSensitivity;
            _P.playerCamera.rotationX -= deltaY * _P.mouseSensitivity;
            
            // Clamp pitch
            _P.playerCamera.rotationX = Math.max(-(float) Math.PI / 2 + 0.1f, 
                Math.min((float) Math.PI / 2 - 0.1f, _P.playerCamera.rotationX));
        }
    }
    
    public void setMouseLocked(boolean locked) {
        this.mouseLocked = locked;
    }
    
    public boolean isMouseLocked() {
        return mouseLocked;
    }
    
    public Vector3f getEyePosition() {
        return new Vector3f(_P.playerPosition.x, _P.playerPosition.y + PLAYER_HEIGHT - 0.2f, _P.playerPosition.z);
    }
    
    public Vector3f getLookDirection() {
        return _P.playerCamera.getForward();
    }
    
    public int[] getTargetBlock() {
        Vector3f eyePos = getEyePosition();
        Vector3f dir = getLookDirection();
        
        // Raycast 5 blocks ahead
        for (float i = 0.5f; i <= 5; i += 0.5f) {
            int x = (int) Math.floor(eyePos.x + dir.x * i);
            int y = (int) Math.floor(eyePos.y + dir.y * i);
            int z = (int) Math.floor(eyePos.z + dir.z * i);
            
            BlockType block = blockEngine.getBlock(x, y, z);
            if (block.isSolid()) {
                return new int[]{x, y, z};
            }
        }
        
        return null;
    }
}
