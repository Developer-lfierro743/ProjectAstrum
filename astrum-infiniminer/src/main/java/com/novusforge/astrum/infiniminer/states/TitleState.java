package com.novusforge.astrum.infiniminer.states;

import com.novusforge.astrum.infiniminer.*;

/**
 * TitleState - The initial splash/menu screen.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class TitleState extends State {
    
    private final InfiniminerGame game;

    public TitleState(InfiniminerGame game) {
        this.game = game;
    }

    @Override
    public void onEnter(String oldState) {
        System.out.println("Entering TitleState");
    }

    @Override
    public void onLeave(String newState) {
        System.out.println("Leaving TitleState");
    }

    @Override
    public String onUpdate(float deltaTime) {
        // In the original, any click moves to the next screen
        if (game.getInputHandler().isMousePressed(0)) {
            return "com.novusforge.astrum.infiniminer.states.TeamSelectionState";
        }
        return null;
    }

    @Override
    public void onRender() {
        // Render background texture
    }
}
