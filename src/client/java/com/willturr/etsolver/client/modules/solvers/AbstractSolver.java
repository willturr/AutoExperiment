package com.willturr.etsolver.client.modules.solvers;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

public abstract class AbstractSolver {

    enum State {
        LISTENING,
        RUNNING,
        DORMANT,
        END
    }

    private static State currentState = State.DORMANT;

    protected AbstractSolver(String containerName) {
        super();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public void start(GenericContainerScreen screen) {
        System.out.println("Starting solver.");
        currentState = State.LISTENING;
        ScreenEvents.afterTick(screen).register(ignored -> tick(screen));
    }

    protected void tick(GenericContainerScreen screen) {
    }
}
