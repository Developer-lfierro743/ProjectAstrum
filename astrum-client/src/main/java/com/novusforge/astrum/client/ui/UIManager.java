package com.novusforge.astrum.client.ui;

import com.novusforge.astrum.common.infiniminer.InfiniminerBlockEngine;
import com.novusforge.astrum.common.infiniminer.InfiniminerCaveGenerator;
import com.novusforge.astrum.common.infiniminer.BlockType;
import com.novusforge.astrum.engine.RenderBackend;

/**
 * Orchestrates the game UI and transitions between states.
 */
public class UIManager {
    private UIState currentState;
    private final TitleScreen titleScreen;
    
    // Legacy Minigame state
    private InfiniminerBlockEngine minigameEngine;

    public UIManager() {
        this.currentState = UIState.TITLE_SCREEN;
        this.titleScreen = new TitleScreen(this);
    }

    public void setState(UIState state) {
        System.out.println("UI: Transitioning to state " + state);
        this.currentState = state;
        
        if (state == UIState.INFINIMINER_LEGACY) {
            startMinigame();
        }
    }

    public UIState getCurrentState() {
        return currentState;
    }

    private void startMinigame() {
        System.out.println("Initializing Infiniminer Legacy...");
        this.minigameEngine = new InfiniminerBlockEngine();
        
        // Use ported generator
        BlockType[][][] map = InfiniminerCaveGenerator.generateCaveSystem(64, true, 10);
        
        // Load into engine
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                for (int z = 0; z < 64; z++) {
                    if (map[x][y][z] != BlockType.NONE) {
                        minigameEngine.addBlock(x, y, z, map[x][y][z]);
                    }
                }
            }
        }
        System.out.println("Infiniminer Legacy Ready!");
    }

    public void render(RenderBackend backend) {
        switch (currentState) {
            case TITLE_SCREEN -> titleScreen.render();
            case INFINIMINER_LEGACY -> renderMinigame(backend);
            default -> System.out.println("Rendering state: " + currentState);
        }
    }

    private void renderMinigame(RenderBackend backend) {
        // Placeholder for legacy rendering logic
        backend.render();
    }
}
