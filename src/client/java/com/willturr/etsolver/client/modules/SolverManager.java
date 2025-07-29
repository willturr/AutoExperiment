package com.willturr.etsolver.client.modules;

import com.willturr.etsolver.client.modules.solvers.*;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class SolverManager {

    private static final CycleBasedChrono chronomatronSolver = new CycleBasedChrono("Chronomatron");
    private static final UltrasequencerSolver ultrasequencerSolver = new UltrasequencerSolver("Ultrasequencer");

    public static void init() {
        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> {

            if (screen instanceof GenericContainerScreen genericContainerScreen) {
                //System.out.println("Screen name " + genericContainerScreen.getTitle().getString());
                if (genericContainerScreen.getTitle().getString().startsWith("Chronomatron (")) { //ignores chronomatron selection screen
                    chronomatronSolver.start(genericContainerScreen);
                } else if (genericContainerScreen.getTitle().getString().startsWith("Ultrasequencer (")) {
                    ultrasequencerSolver.start(genericContainerScreen);
                } else {
                    //System.out.println("Not a Chronomatron screen");
                    chronomatronSolver.resetClickStack();
                    ultrasequencerSolver.clearItemStack();
                }
            }

        });
    }

    public static void starterMotor() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        ScreenHandler screenHandler = client.player.currentScreenHandler;

        for (int i = 24; i > 20; i--) {
            if (!(screenHandler.slots.get(i).getStack().getItem() == Items.GRAY_DYE)) {
                interactionManager.clickSlot(
                        screenHandler.syncId,
                        i,
                        0,
                        SlotActionType.PICKUP,
                        client.player
                );
            }
        }
    }

}
