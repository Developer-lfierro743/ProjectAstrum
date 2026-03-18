package com.novusforge.astrum.infiniminer;

/**
 * State - Base class for game states.
 * Ported from C# StateMasher to Java 21.
 */
public abstract class State {
    protected StateMachine _SM;
    protected PropertyBag _P;

    public void setMachine(StateMachine machine) {
        this._SM = machine;
    }

    public void setPropertyBag(PropertyBag propertyBag) {
        this._P = propertyBag;
    }

    public abstract void onEnter(String oldState);
    public abstract void onLeave(String newState);
    public abstract String onUpdate(float deltaTime);
    public abstract void onRender();
}
