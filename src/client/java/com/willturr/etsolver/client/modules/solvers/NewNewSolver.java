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
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class NewNewSolver extends AbstractSolver {

    public NewNewSolver(String containerName) {
        super(containerName);
    }

    private final List<Item> chronomatronSequence = new ArrayList<>();
    private int lastSeenSlot = -1; // Used to prevent re-adding the same glinting item.
    private int clickIndex = 0; // The index in the sequence to click next.
    private int clickCooldown = 0;
    private static final int CLICK_DELAY_TICKS = 8; // Slightly reduced delay for responsiveness.

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

    @Override
    protected void tick(GenericContainerScreen screen) {
        if (clickCooldown > 0) {
            clickCooldown--;
            return;
        }

        Inventory inventory = screen.getScreenHandler().getInventory();
        Text title = inventory.getStack(49).getName();
        String titleString = title != null ? title.getString() : "";

        // --- Centralized State Management ---
        // This is the most reliable way to determine what the solver should be doing.
        if (titleString.equals("Remember the pattern!")) {
            // If the state is not already LISTENING, it means a new round has started. Reset everything.
            if (getCurrentState() != State.LISTENING) {
                System.out.println("New round detected. Resetting and entering LISTENING state.");
                reset();
                setCurrentState(State.LISTENING);
            }
        } else if (titleString.startsWith("Timer: ")) {
            // If we have a sequence and are not already running, start.
            if (!chronomatronSequence.isEmpty() && getCurrentState() != State.RUNNING) {
                System.out.println("Timer detected. Entering RUNNING state.");
                setCurrentState(State.RUNNING);
            }
        } else if (!titleString.equals("Chronomatron")) { // Any other text means the round is over.
            if (getCurrentState() != State.DORMANT) {
                System.out.println("Game ended or in intermediate state. Going DORMANT.");
                setCurrentState(State.DORMANT); // Go idle until "Remember" appears again.
            }
        }


        // --- State-based Actions ---
        switch (getCurrentState()) {
            case LISTENING -> {
                // The goal is to find a glinting item.
                boolean foundGlintThisTick = false;
                for (int i = 10; i < 43; i++) {
                    if (inventory.getStack(i).hasGlint()) {
                        foundGlintThisTick = true;
                        // Only add the item if it's a *new* glint, not the same one from the last tick.
                        if (lastSeenSlot != i) {
                            Item item = TERRACOTTA_TO_GLASS.get(inventory.getStack(i).getItem());
                            if (item != null) {
                                chronomatronSequence.add(item);
                                System.out.println("Added to sequence: " + item.toString() + ". Sequence size: " + chronomatronSequence.size());
                            }
                            lastSeenSlot = i;
                        }
                        break; // Exit after finding the first glint to avoid issues.
                    }
                }
                // If no glint was found, reset lastSeenSlot so the next glint can be detected.
                if (!foundGlintThisTick) {
                    lastSeenSlot = -1;
                }
            }

            case RUNNING -> {
                // Check if we have clicked all items in the sequence.
                if (clickIndex >= chronomatronSequence.size()) {
                    System.out.println("Finished clicking sequence. Waiting for next round.");
                    setCurrentState(State.DORMANT); // Go to an idle state after finishing.
                    return;
                }

                // Get the next item to click based on the FIFO index.
                Item itemToClick = chronomatronSequence.get(clickIndex);

                for (Slot slot : screen.getScreenHandler().slots) {
                    // Find the correct glass pane to click.
                    if (slot.getIndex() >= 10 && slot.getIndex() < 43 && slot.getStack().isOf(itemToClick)) {
                        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
                        if (interactionManager != null) {
                            System.out.println("Clicking " + itemToClick.toString() + " in slot " + slot.id + " (Sequence index " + clickIndex + ")");
                            interactionManager.clickSlot(
                                    screen.getScreenHandler().syncId,
                                    slot.id,
                                    0,
                                    SlotActionType.PICKUP,
                                    MinecraftClient.getInstance().player
                            );
                        }

                        clickIndex++; // Move to the next item in the sequence.
                        clickCooldown = CLICK_DELAY_TICKS;
                        return; // Exit to respect the cooldown.
                    }
                }
            }
            // DORMANT and END states are now handled by the centralized logic at the top.
            // No action is needed here.
            case DORMANT, END -> {}
        }
    }

    public void reset() {
        chronomatronSequence.clear();
        lastSeenSlot = -1;
        clickIndex = 0;
        clickCooldown = 0;
    }
}