package com.willturr.etsolver.client.modules;

import com.willturr.etsolver.client.modules.solvers.*;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

public class SolverManager {

    private static final CycleBasedChrono chronomatronSolver = new CycleBasedChrono("Chronomatron");

    public static void init() {
        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> {

            if (screen instanceof GenericContainerScreen genericContainerScreen) {
                System.out.println("Screen name " + genericContainerScreen.getTitle().getString());
                if (genericContainerScreen.getTitle().getString().startsWith("Chronomatron (")) { //ignores chronomatron selection screen
                    chronomatronSolver.start(genericContainerScreen);
                } else {
                    System.out.println("Not a Chronomatron screen");
                    chronomatronSolver.resetClickStack();
                }
            }

        });
    }
}
