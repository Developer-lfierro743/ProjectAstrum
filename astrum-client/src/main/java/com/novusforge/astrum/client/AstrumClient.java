package com.novusforge.astrum.client;

import com.novusforge.astrum.core.world.World;
import com.novusforge.astrum.engine.RenderBackend;
import com.novusforge.astrum.engine.EngineFactory;
import com.novusforge.astrum.security.guardian.SafetyGuardian;
import com.novusforge.astrum.client.ui.UIManager;
import com.novusforge.astrum.client.ui.UIState;

/**
 * Main entry point for the Astrum game client.
 */
public class AstrumClient {
    @SuppressWarnings("unused")
    private final World world;
    private final RenderBackend engine;
    @SuppressWarnings("unused")
    private final SafetyGuardian guardian;
    private final UIManager uiManager;

    public AstrumClient() {
        this.world = new World();
        this.engine = EngineFactory.createBackend();
        this.guardian = new SafetyGuardian();
        this.uiManager = new UIManager();
    }

    public void start() {
        System.out.println("Starting Project Astrum...");
        
        // Initialize systems
        engine.initialize();
        
        // Game loop simulation
        System.out.println("Astrum Hub initialized.");
        
        // Render current state
        uiManager.render(engine);
        
        // Simulate transition to minigame for verification
        uiManager.setState(UIState.INFINIMINER_LEGACY);
        uiManager.render(engine);
        
        System.out.println("Astrum is running. Press Ctrl+C to stop.");
    }

    public static void main(String[] args) {
        new AstrumClient().start();
    }
}
