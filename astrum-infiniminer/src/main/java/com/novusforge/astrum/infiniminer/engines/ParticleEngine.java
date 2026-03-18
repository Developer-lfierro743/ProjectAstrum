package com.novusforge.astrum.infiniminer.engines;

import com.novusforge.astrum.infiniminer.InfiniminerGame;
import com.novusforge.astrum.infiniminer.PropertyBag;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ParticleEngine - Handles particle systems (debris, blood).
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class ParticleEngine {
    
    public static class Particle {
        public Vector3f position = new Vector3f();
        public Vector3f velocity = new Vector3f();
        public float size;
        public Vector4f color = new Vector4f();
        public boolean flaggedForDeletion = false;
    }
    
    private final InfiniminerGame gameInstance;
    private PropertyBag _P;
    private final List<Particle> particleList = new ArrayList<>();
    private final Random randGen = new Random();
    
    public ParticleEngine(InfiniminerGame gameInstance) {
        this.gameInstance = gameInstance;
        System.out.println("ParticleEngine initialized");
    }
    
    public void setPropertyBag(PropertyBag p) {
        this._P = p;
    }
    
    public void update(float deltaTime) {
        if (_P == null) return;
        
        for (int i = 0; i < particleList.size(); i++) {
            Particle p = particleList.get(i);
            p.position.add(p.velocity.x * deltaTime, p.velocity.y * deltaTime, p.velocity.z * deltaTime);
            p.velocity.y -= 8 * deltaTime; // Gravity for particles
            
            if (_P.blockEngine.solidAtPointForPlayer(p.position)) {
                p.flaggedForDeletion = true;
            }
        }
        
        particleList.removeIf(p -> p.flaggedForDeletion);
    }
    
    public void createExplosionDebris(Vector3f explosionPosition) {
        for (int i = 0; i < 50; i++) {
            Particle p = new Particle();
            p.color.set(90/255f, 60/255f, 40/255f, 1.0f);
            p.size = randGen.nextFloat() * 0.4f + 0.05f;
            p.position.set(explosionPosition);
            p.position.y += randGen.nextFloat() - 0.5f;
            p.velocity.set(randGen.nextFloat() * 8 - 4, randGen.nextFloat() * 8, randGen.nextFloat() * 8 - 4);
            particleList.add(p);
        }
    }
    
    public void createBloodSplatter(Vector3f playerPosition, Vector4f color) {
        for (int i = 0; i < 30; i++) {
            Particle p = new Particle();
            p.color.set(color);
            p.size = randGen.nextFloat() * 0.2f + 0.05f;
            p.position.set(playerPosition);
            p.position.y -= randGen.nextFloat();
            p.velocity.set(randGen.nextFloat() * 5 - 2.5f, randGen.nextFloat() * 4f, randGen.nextFloat() * 5 - 2.5f);
            particleList.add(p);
        }
    }
    
    public void render() {
        if (_P == null) return;
        // Batch rendering of particles using a cube mesh and instancing or dynamic buffers
    }
}
