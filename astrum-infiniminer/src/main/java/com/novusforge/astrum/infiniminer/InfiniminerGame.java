package com.novusforge.astrum.infiniminer;

import com.novusforge.astrum.engine.RenderBackend;
import com.novusforge.astrum.engine.EngineFactory;
import com.novusforge.astrum.engine.WindowProvider;
import com.novusforge.astrum.infiniminer.states.*;
import org.joml.Vector3f;

/**
 * Main Infiniminer Game - Ported from C# XNA to Java 21 LWJGL3!
 */
public class InfiniminerGame implements RenderBackend {
    
    public enum GameState {
        MENU,
        LOADING,
        PLAYING,
        PAUSED,
        GAME_OVER
    }
    
    private StateMachine stateMachine;
    private PropertyBag propertyBag;
    private InputHandler inputHandler;
    private KeyBindHandler keyBindHandler;
    private RenderBackend backend;
    
    // Settings
    public String playerName = "Player";
    public boolean invertMouseY = false;
    public float mouseSensitivity = 0.005f;
    
    public InfiniminerGame() {
        System.out.println("===========================================");
        System.out.println("   INFINIMINER: LEGACY EDITION");
        System.out.println("   Ported to Java 21 + LWJGL3");
        System.out.println("===========================================");
        
        inputHandler = new InputHandler();
        keyBindHandler = new KeyBindHandler(inputHandler);
        propertyBag = new PropertyBag(this);
        stateMachine = new StateMachine(this, propertyBag);
        
        // Add states
        stateMachine.addState("com.novusforge.astrum.infiniminer.states.TitleState", new TitleState(this));
        stateMachine.addState("com.novusforge.astrum.infiniminer.states.TeamSelectionState", new TeamSelectionState(this));
        stateMachine.addState("com.novusforge.astrum.infiniminer.states.ClassSelectionState", new ClassSelectionState(this));
        stateMachine.addState("com.novusforge.astrum.infiniminer.states.MainGameState", new MainGameState(this));
        
        // Initial state
        stateMachine.changeState("com.novusforge.astrum.infiniminer.states.TitleState");
    }
    
    public void start() {
        // backend = EngineFactory.createBackend();
        // backend.initialize();
        
        generateTerrain();
        
        System.out.println("\n=== INFINIMINER STARTED ===");
    }
    
    private void generateTerrain() {
        for (int x = 0; x < Defines.MAP_SIZE_X; x++) {
            for (int z = 0; z < Defines.MAP_SIZE_Z; z++) {
                int height = 10 + (int)(Math.sin(x * 0.1) * 5 + Math.cos(z * 0.1) * 5);
                for (int y = 0; y < Defines.MAP_SIZE_Y; y++) {
                    if (y < height - 3) propertyBag.blockEngine.setBlock(x, y, z, BlockType.Rock);
                    else if (y < height) propertyBag.blockEngine.setBlock(x, y, z, BlockType.Dirt);
                    else propertyBag.blockEngine.setBlock(x, y, z, BlockType.None);
                }
            }
        }
    }
    
    public void update(float deltaTime) {
        inputHandler.update();
        stateMachine.update(deltaTime);
    }
    
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public KeyBindHandler getKeyBinds() {
        return keyBindHandler;
    }

    // RenderBackend implementation
    @Override public void initialize() {}
    @Override public void render() { stateMachine.render(); }
    @Override public void shutdown() {}
    @Override public void resize(int width, int height) {}
    @Override public void clear(float r, float g, float b, float a) {}
    @Override public void setLegacyMode(boolean enabled) {}
    @Override public WindowProvider getWindowProvider() { return null; }
    @Override public RenderAPI getAPI() { return RenderAPI.OPENGL_ES; }
    @Override public FeatureLevel getFeatureLevel() { return FeatureLevel.MEDIUM; }
    @Override public boolean supportsGeometryShaders() { return false; }
    @Override public boolean supportsComputeShaders() { return false; }
    @Override public boolean supportsTessellation() { return false; }
    @Override public long getAllocatedVRAM() { return 0; }
    @Override public long getRecommendedChunkMeshSize() { return 16; }
    
    public static void main(String[] args) {
        InfiniminerGame game = new InfiniminerGame();
        game.start();
        
        // Simulate a few frames
        for (int i = 0; i < 10; i++) {
            game.update(0.016f);
        }
    }
}
