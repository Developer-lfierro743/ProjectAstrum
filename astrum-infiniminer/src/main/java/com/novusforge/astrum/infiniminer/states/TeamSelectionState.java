package com.novusforge.astrum.infiniminer.states;

import com.novusforge.astrum.infiniminer.*;

/**
 * TeamSelectionState - Menu to choose a team.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class TeamSelectionState extends State {
    
    private final InfiniminerGame game;

    public TeamSelectionState(InfiniminerGame game) {
        this.game = game;
    }

    @Override
    public void onEnter(String oldState) {
        System.out.println("Entering TeamSelectionState");
    }

    @Override
    public void onLeave(String newState) {
        System.out.println("Leaving TeamSelectionState");
    }

    @Override
    public String onUpdate(float deltaTime) {
        // Simplified selection
        if (game.getInputHandler().isMousePressed(0)) {
            _P.playerTeam = PlayerTeam.Red;
            return "com.novusforge.astrum.infiniminer.states.ClassSelectionState";
        }
        return null;
    }

    @Override
    public void onRender() {
        // Render team selection menu
    }
}
