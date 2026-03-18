package com.novusforge.astrum.infiniminer.states;

import com.novusforge.astrum.infiniminer.*;

/**
 * ClassSelectionState - Menu to choose a player class.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class ClassSelectionState extends State {
    
    private final InfiniminerGame game;

    public ClassSelectionState(InfiniminerGame game) {
        this.game = game;
    }

    @Override
    public void onEnter(String oldState) {
        System.out.println("Entering ClassSelectionState");
    }

    @Override
    public void onLeave(String newState) {
        System.out.println("Leaving ClassSelectionState");
    }

    @Override
    public String onUpdate(float deltaTime) {
        // Simplified selection
        if (game.getInputHandler().isMousePressed(0)) {
            _P.playerClass = PlayerClass.Miner;
            _P.equipWeps(); // Update inventory
            _P.respawnPlayer();
            return "com.novusforge.astrum.infiniminer.states.MainGameState";
        }
        return null;
    }

    @Override
    public void onRender() {
        // Render class selection menu
    }
}
