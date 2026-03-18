package com.novusforge.astrum.client.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UIManagerTest {

    @Test
    public void testUIStateTransition() {
        UIManager manager = new UIManager();
        assertEquals(UIState.TITLE_SCREEN, manager.getCurrentState());
        
        manager.setState(UIState.INFINIMINER_LEGACY);
        assertEquals(UIState.INFINIMINER_LEGACY, manager.getCurrentState());
    }
}
