package com.novusforge.astrum.infiniminer;

import org.joml.Vector3f;

/**
 * Player class for Infiniminer.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class Player {
    
    public int playerId;
    public String playerName = "Player";
    public Vector3f position = new Vector3f(0, 10, 0);
    public Vector3f velocity = new Vector3f(0, 0, 0);
    public float rotationY = 0; // Yaw
    public float rotationX = 0; // Pitch
    public PlayerTeam team = PlayerTeam.None;
    public PlayerClass playerClass = PlayerClass.Miner;
    public int health = 100;
    public int ore = 0;
    public boolean grounded = false;
    public boolean admin = false;
    public boolean alive = true;
    
    // Inventory
    public BlockType selectedBlock = BlockType.Rock;
    public BlockType[] inventory = new BlockType[9]; // 9 slots
    
    public Player() {
        inventory[0] = BlockType.Rock;
        inventory[1] = BlockType.Dirt;
        inventory[2] = BlockType.Dirt;
    }
    
    public void update(float deltaTime) {
        if (!alive) return;
        
        // Apply gravity
        velocity.y += Defines.GRAVITY * deltaTime;
        
        // Apply velocity to position
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        position.z += velocity.z * deltaTime;
        
        // Ground collision (simplified)
        if (position.y < 1) {
            position.y = 1;
            velocity.y = 0;
            grounded = true;
        } else {
            grounded = false;
        }
        
        // Friction
        velocity.x *= 0.9f;
        velocity.z *= 0.9f;
    }
    
    public void moveForward(float speed) {
        float sinY = (float) Math.sin(rotationY);
        float cosY = (float) Math.cos(rotationY);
        velocity.x += sinY * speed;
        velocity.z += cosY * speed;
    }
    
    public void moveRight(float speed) {
        float sinY = (float) Math.sin(rotationY + Math.PI / 2);
        float cosY = (float) Math.cos(rotationY + Math.PI / 2);
        velocity.x += sinY * speed;
        velocity.z += cosY * speed;
    }
    
    public void jump() {
        if (grounded) {
            velocity.y = Defines.JUMPVELOCITY;
            grounded = false;
        }
    }
    
    public Vector3f getDirection() {
        Vector3f dir = new Vector3f();
        dir.x = (float) (Math.sin(rotationY) * Math.cos(rotationX));
        dir.y = (float) Math.sin(rotationX);
        dir.z = (float) (Math.cos(rotationY) * Math.cos(rotationX));
        return dir;
    }
    
    public void selectBlock(int slot) {
        if (slot >= 0 && slot < inventory.length) {
            selectedBlock = inventory[slot];
        }
    }
}
