package com.willturr.etsolver.client.modules.solvers;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

public class CycleBasedChrono extends AbstractSolver {

    public CycleBasedChrono(String containerName) {
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

    //required to place click inputs
    MinecraftClient client = MinecraftClient.getInstance();

    //core detection logic variables
    boolean glintFound = false;
    int glintFoundAt = -1;
    List<Item> clickStack = new ArrayList<>();

    //used to catch edge cases, chronomatron updates mode indicators at the same time as the last entry in a sequence
    //by comparing changes in cycle no. to changes in mode slot, we can differentiate between the 2 different edge cases
    int lastCycle = 0;
    int currentCycle = 0;
    Item lastModeItem = null;

    //used to delay beginning of sequence entry
    int startSeconds = -1;

    //lazily creating another variable for very similar functionality #goinggoated
    int pauseIndex = -1;

    @Override
    public void tick(GenericContainerScreen screen) {
        Inventory inventory = screen.getScreenHandler().getInventory();
        currentCycle = getChronoCycle(inventory);
        Item currentModeItem = inventory.getStack(49).getItem();

        // if cycle valid and in remembering mode OR if last mode was different but cycle didnt change
        if ((getChronoCycle(inventory) > 0 && currentModeItem == Items.GLOWSTONE) || (currentCycle == lastCycle && currentModeItem != lastModeItem)) {
            startSeconds = -1; //#optimisation
            if (!glintFound) {
                for (int i = 10; i < 43; i++) {
                    if (inventory.getStack(i).hasGlint()) { //if block is part of sequence
                        glintFound = true; //block becomes focus, and cycle repeats when block is no longer glinting
                        glintFoundAt = i;
                        clickStack.add(TERRACOTTA_TO_GLASS.get(inventory.getStack(i).getItem()));
                        //System.out.println(clickStack);
                        break;
                    }
                }
            } else if (!inventory.getStack(glintFoundAt).hasGlint()) {
                glintFound = false;
                glintFoundAt = -1;
            }
        } else { //when chrono is waiting for inputs, we enter them!
            if (startSeconds == -1) {
                startSeconds = inventory.getStack(49).getCount();
            }
            if (inventory.getStack(49).getCount() < startSeconds) {
                inputSequence(inventory, screen);
            }
        }

/*        if (currentCycle > lastCycle) { //may keep or delete, depends how click works
            resetClickStack();
        }*/
        lastCycle = currentCycle;
        lastModeItem = currentModeItem;
    }

    private void inputSequence(Inventory inventory, GenericContainerScreen screen) {
        //System.out.println(client.player.currentScreenHandler.getCursorStack());

        if (client.player.currentScreenHandler.getCursorStack().getItem() == Items.AIR) {
            for (int i = 10; i < 43; i++) {
                if (!clickStack.isEmpty()) {
                    if (inventory.getStack(i).getItem() == clickStack.getFirst()) {
                        pauseIndex = i;
                        ClientPlayerInteractionManager interactionManager = client.interactionManager;
                        //System.out.println("Attempting to click " + clickStack.getFirst());
                        interactionManager.clickSlot(
                                screen.getScreenHandler().syncId,
                                i,
                                0,
                                SlotActionType.PICKUP,
                                client.player
                        );
                        clickStack.removeFirst();
                        break;
                    }
                }
            }
        }
    }


    private int getChronoCycle(Inventory inventory) {
        return inventory.getStack(4).getCount();
    }

    public void resetClickStack() {
        clickStack.clear();
    }

}
