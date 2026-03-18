package com.novusforge.astrum.client.ui;

/**
 * Handles the layout and rendering of the Title Screen.
 */
public class TitleScreen {
    private final UIManager manager;
    private static final String VERSION = "version 0.0.1";
    private static final String COPYRIGHT = "copyright novusforge studios. Do not Distribute!";

    public TitleScreen(UIManager manager) {
        this.manager = manager;
    }

    public void render() {
        System.out.println("--- PROJECT ASTRUM HUB ---");
        System.out.println("1. Singleplayer (Voxel Core)");
        System.out.println("2. Multiplayer (Astrum Net)");
        System.out.println("3. Minigame (Infiniminer Legacy)");
        System.out.println("4. Exit");
        
        // Corner branding as requested
        renderCornerText();
    }

    private void renderCornerText() {
        // Logically these would be rendered to the screen corners in a graphical context
        System.out.println("[BOTTOM-LEFT]  " + VERSION);
        System.out.println("[BOTTOM-RIGHT] " + COPYRIGHT);
    }

    public void handleInput(int choice) {
        switch (choice) {
            case 1 -> manager.setState(UIState.SINGLEPLAYER);
            case 2 -> manager.setState(UIState.MULTIPLAYER);
            case 3 -> manager.setState(UIState.INFINIMINER_LEGACY);
            case 4 -> System.exit(0);
        }
    }
}
