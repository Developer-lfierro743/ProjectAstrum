package com.novusforge.astrum.infiniminer;

import java.util.HashMap;
import java.util.Map;

/**
 * StateMachine - Handles game state transitions.
 * Ported from C# StateMasher to Java 21.
 */
public class StateMachine {
    private final Map<String, State> states = new HashMap<>();
    private State currentState = null;
    private final InfiniminerGame game;
    private final PropertyBag propertyBag;

    public StateMachine(InfiniminerGame game, PropertyBag propertyBag) {
        this.game = game;
        this.propertyBag = propertyBag;
    }

    public void addState(String name, State state) {
        state.setMachine(this);
        state.setPropertyBag(propertyBag);
        states.put(name, state);
    }

    public void changeState(String name) {
        if (currentState != null) {
            currentState.onLeave(name);
        }
        State nextState = states.get(name);
        if (nextState != null) {
            String oldStateName = (currentState != null) ? currentState.getClass().getName() : "";
            currentState = nextState;
            currentState.onEnter(oldStateName);
        }
    }

    public void update(float deltaTime) {
        if (currentState != null) {
            String nextState = currentState.onUpdate(deltaTime);
            if (nextState != null) {
                changeState(nextState);
            }
        }
    }

    public void render() {
        if (currentState != null) {
            currentState.onRender();
        }
    }
}
