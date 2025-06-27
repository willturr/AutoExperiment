package com.willturr.etsolver.client.modules.solvers;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

public class ChronomatronSolver extends AbstractSolver {

    public ChronomatronSolver(String containerName) {
        super(containerName);
    }

    public static final Object2ObjectMap<Item, Item> TERRACOTTA_TO_GLASS = Object2ObjectMaps.unmodifiable(
            new Object2ObjectArrayMap<>(
                    new Item[]{
                            Items.RED_TERRACOTTA, Items.ORANGE_TERRACOTTA, Items.YELLOW_TERRACOTTA, Items.LIME_TERRACOTTA, Items.GREEN_TERRACOTTA, Items.CYAN_TERRACOTTA, Items.LIGHT_BLUE_TERRACOTTA, Items.BLUE_TERRACOTTA, Items.PURPLE_TERRACOTTA, Items.PINK_TERRACOTTA
                    },
                    new Item[]{
                            Items.RED_STAINED_GLASS, Items.ORANGE_STAINED_GLASS, Items.YELLOW_STAINED_GLASS, Items.LIME_STAINED_GLASS, Items.GREEN_STAINED_GLASS, Items.CYAN_STAINED_GLASS, Items.LIGHT_BLUE_STAINED_GLASS, Items.BLUE_STAINED_GLASS, Items.PURPLE_STAINED_GLASS, Items.PINK_STAINED_GLASS
                    }
            )
    );

    MinecraftClient client = MinecraftClient.getInstance();
    ClientPlayerInteractionManager interactionManager = client.interactionManager;

    private final List<Item> chronomatronSlots = new ArrayList<>();
    private int chronomatronLength;
    private int currentSlot = 0;

    State lastState = State.DORMANT;


    /*protected void tick(GenericContainerScreen screen) {
        switch (getCurrentState()) {
            case LISTENING -> {
                Inventory inventory = screen.getScreenHandler().getInventory();
                if (currentSlot == 0) {
                    for (int index = 10; index < 43; index++) {
                        if (inventory.getStack(index).hasGlint()) {
                            if (chronomatronSlots.size() <= chronomatronLength) {
                                chronomatronSlots.add(inventory.getStack(index).getItem());
                                System.out.println(chronomatronSlots.toString());
                                //setCurrentState(State.DORMANT);
                            } else {
                                chronomatronLength++;
                            }
                            currentSlot = index;
                            return;
                        }
                    }
                } else if (!inventory.getStack(currentSlot).hasGlint()) {
                    currentSlot = 0;
                    setCurrentState(State.LISTENING);
                }
            }
        }
    }*/


    protected void tick(GenericContainerScreen screen) {
        // 1. Get the true state from the game GUI
        State currentState = getGameState(screen);
        setCurrentState(currentState);

        // 2. Check if the state has just changed
        if (currentState != lastState) {
            System.out.println("State changed from " + lastState + " to " + currentState);
            // If we just entered the DORMANT state, it means a round just ended.
            if (currentState == State.DORMANT) {
                resetState(); // Reset the solver for the next round.
            }
        }
        this.lastState = currentState; // Remember the state for the next tick.
        switch (getGameState(screen)) {
            case LISTENING -> {
                Inventory inventory = screen.getScreenHandler().getInventory();
                if (currentSlot == 0) {
                    for (int index = 10; index < 43; index++) {
                        if (inventory.getStack(index).hasGlint()) {
                            if (chronomatronSlots.size() >= chronomatronLength) {
                                chronomatronSlots.add(TERRACOTTA_TO_GLASS.get(inventory.getStack(index).getItem()));
                                System.out.println(chronomatronSlots);
                            } else {
                                chronomatronLength++;
                            }

                            currentSlot = index;
                            return;

                        }
                    }
                } else if (!inventory.getStack(currentSlot).hasGlint()) {
                    currentSlot = 0;
                }
            }

            case RUNNING -> {
                return;
                /*Inventory inventory = screen.getScreenHandler().getInventory();
                if (currentSlot == 0) {
                    for (int index = 10; index < 43; index++) {
                        if (chronomatronSlots.getLast() == inventory.getStack(index).getItem()) {
                            interactionManager.clickSlot(
                                    screen.getScreenHandler().syncId,
                                    10,
                                    0,
                                    SlotActionType.PICKUP,
                                    client.player
                            );
                        }
                    }
                }*/
            }

        }
    }

    private State getGameState(GenericContainerScreen screen) {
        String name = screen.getScreenHandler().getInventory().getStack(49).getName().getString();

        if (name.equals("Remember the pattern!")) {
            return State.LISTENING;
        }

        if (name.startsWith("Timer: ")) {
            return State.RUNNING;
        }

        // For any other text ("Game Over", etc.), the game is in a dormant state.
        return State.DORMANT;
    }

    public void resetState() {
        chronomatronLength = 0;
        currentSlot = 0;
        chronomatronSlots.clear();
    }

}
