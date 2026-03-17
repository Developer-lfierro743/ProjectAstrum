package com.novusforge.astrum.infiniminer.engines;

import com.novusforge.astrum.infiniminer.BlockType;
import com.novusforge.astrum.infiniminer.Defines;
import com.novusforge.astrum.infiniminer.Player;
import org.joml.Vector3f;

/**
 * PlayerEngine - Handles player physics and collision.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class PlayerEngine {
    
    private BlockEngine blockEngine;
    private Player player;
    
    private boolean[] keyState = new boolean[256];
    private boolean mouseLocked = false;
    
    // Player physics constants
    private static final float MOVE_SPEED = 5.0f;
    private static final float SPRINT_SPEED = 8.0f;
    private static final float JUMP_FORCE = 6.5f;
    private static final float GRAVITY = 20.0f;
    private static final float PLAYER_RADIUS = 0.3f;
    private static final float PLAYER_HEIGHT = 1.8f;
    
    public PlayerEngine(BlockEngine blockEngine, Player player) {
        this.blockEngine = blockEngine;
        this.player = player;
        System.out.println("PlayerEngine initialized");
    }
    
    public void update(float deltaTime) {
        // Handle movement input
        float moveSpeed = MOVE_SPEED;
        
        float moveX = 0, moveZ = 0;
        
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
        float sinY = (float) Math.sin(player.rotationY);
        float cosY = (float) Math.cos(player.rotationY);
        
        player.velocity.x = (sinY * moveZ + cosY * moveX) * moveSpeed;
        player.velocity.z = (cosY * moveZ - sinY * moveX) * moveSpeed;
        
        // Apply gravity
        player.velocity.y -= GRAVITY * deltaTime;
        
        // Move and collide
        movePlayer(deltaTime);
        
        // Keep player in bounds
        clampPosition();
    }
    
    private void movePlayer(float deltaTime) {
        float newX = player.position.x + player.velocity.x * deltaTime;
        float newY = player.position.y + player.velocity.y * deltaTime;
        float newZ = player.position.z + player.velocity.z * deltaTime;
        
        // Check X collision
        if (!checkCollision(newX, player.position.y, player.position.z)) {
            player.position.x = newX;
        } else {
            player.velocity.x = 0;
        }
        
        // Check Y collision
        if (!checkCollision(player.position.x, newY, player.position.z)) {
            player.position.y = newY;
        } else {
            if (player.velocity.y < 0) {
                player.grounded = true;
            }
            player.velocity.y = 0;
        }
        
        // Check Z collision
        if (!checkCollision(player.position.x, player.position.y, newZ)) {
            player.position.z = newZ;
        } else {
            player.velocity.z = 0;
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
                    byte block = blockEngine.getBlock(ix + dx, iy + dy, iz + dz);
                    if (BlockType.isSolid(block)) {
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
        player.position.x = Math.max(0.5f, Math.min(Defines.MAP_SIZE_X - 0.5f, player.position.x));
        player.position.y = Math.max(0, Math.min(Defines.MAP_SIZE_Y - 1, player.position.y));
        player.position.z = Math.max(0.5f, Math.min(Defines.MAP_SIZE_Z - 0.5f, player.position.z));
    }
    
    public void onKeyPress(int keyCode) {
        if (keyCode >= 0 && keyCode < 256) {
            keyState[keyCode] = true;
        }
        
        // Jump
        if (keyCode == 57 && player.grounded) { // Space
            player.velocity.y = JUMP_FORCE;
            player.grounded = false;
        }
    }
    
    public void onKeyRelease(int keyCode) {
        if (keyCode >= 0 && keyCode < 256) {
            keyState[keyCode] = false;
        }
    }
    
    public void onMouseMove(float deltaX, float deltaY) {
        if (mouseLocked) {
            player.rotationY += deltaX * Defines.MOUSE_SENSITIVITY;
            player.rotationX -= deltaY * Defines.MOUSE_SENSITIVITY;
            
            // Clamp pitch
            player.rotationX = Math.max(-(float) Math.PI / 2 + 0.1f, 
                Math.min((float) Math.PI / 2 - 0.1f, player.rotationX));
        }
    }
    
    public void setMouseLocked(boolean locked) {
        this.mouseLocked = locked;
    }
    
    public boolean isMouseLocked() {
        return mouseLocked;
    }
    
    public Vector3f getEyePosition() {
        return new Vector3f(player.position.x, player.position.y + PLAYER_HEIGHT - 0.2f, player.position.z);
    }
    
    public Vector3f getLookDirection() {
        Vector3f dir = new Vector3f();
        dir.x = (float) (Math.sin(player.rotationY) * Math.cos(player.rotationX));
        dir.y = (float) Math.sin(player.rotationX);
        dir.z = (float) (Math.cos(player.rotationY) * Math.cos(player.rotationX));
        return dir;
    }
    
    public int[] getTargetBlock() {
        Vector3f eyePos = getEyePosition();
        Vector3f dir = getLookDirection();
        
        // Raycast 5 blocks ahead
        for (int i = 1; i <= 5; i++) {
            int x = (int) (eyePos.x + dir.x * i);
            int y = (int) (eyePos.y + dir.y * i);
            int z = (int) (eyePos.z + dir.z * i);
            
            byte block = blockEngine.getBlock(x, y, z);
            if (BlockType.isSolid(block)) {
                return new int[]{x, y, z};
            }
        }
        
        return null;
    }
    
    public int[] getTargetBlockFace() {
        Vector3f eyePos = getEyePosition();
        Vector3f dir = getLookDirection();
        
        for (int i = 1; i <= 5; i++) {
            int x = (int) (eyePos.x + dir.x * i);
            int y = (int) (eyePos.y + dir.y * i);
            int z = (int) (eyePos.z + dir.z * i);
            
            byte block = blockEngine.getBlock(x, y, z);
            if (BlockType.isSolid(block)) {
                // Return the face we're looking at
                return new int[]{x, y, z};
            }
        }
        
        return null;
    }
}
