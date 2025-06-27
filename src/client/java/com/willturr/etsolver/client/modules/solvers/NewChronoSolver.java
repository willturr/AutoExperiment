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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewChronoSolver extends AbstractSolver {

    public NewChronoSolver(String containerName) {
        super(containerName);
    }

    /**
     * The list of items to remember, in order.
     */
    private final List<Item> chronomatronSlots = new ArrayList<>();
    /**
     * The index of the current item shown in the chain, used for remembering.
     */
    private int chronomatronChainLengthCount;
    /**
     * The slot id of the current item shown, used for detecting when the experiment finishes showing the current item.
     */
    private int chronomatronCurrentSlot;
    /**
     * The next index in the chain to click.
     */
    private int chronomatronCurrentOrdinal;

    //running variables
    private int clickCooldown = 0;
    private static final int CLICK_DELAY_TICKS = 10;


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
        switch (getCurrentState()) {
            case LISTENING -> {
                Inventory inventory = screen.getScreenHandler().getInventory();
                // Only try to look for items with enchantment glint if there is no item being currently shown.
                if (chronomatronCurrentSlot == 0) {
                    for (int index = 10; index < 43; index++) {
                        if (inventory.getStack(index).hasGlint()) {
                            // If the list of items is smaller than the index of the current item shown, add the item to the list and set the state to wait.
                            if (chronomatronSlots.size() <= chronomatronChainLengthCount) {
                                chronomatronSlots.add(TERRACOTTA_TO_GLASS.get(inventory.getStack(index).getItem()));
                                System.out.println(chronomatronSlots);
                                setCurrentState(State.DORMANT);
                            } else {
                                // If the item is already in the list, increment the current item shown index.
                                chronomatronChainLengthCount++;
                            }
                            // Remember the slot shown to detect when the experiment finishes showing the current item.
                            chronomatronCurrentSlot = index;
                            return;
                        }
                    }
                    // If the current item shown no longer has enchantment glint, the experiment finished showing the current item.
                } else if (!inventory.getStack(chronomatronCurrentSlot).hasGlint()) {
                    chronomatronCurrentSlot = 0;
                }
            }

            case RUNNING -> {
                // Check if we have more items to click in our remembered sequence.
                if (chronomatronSlots.isEmpty()) {
                    // We have clicked everything. Transition to the END state to check the result.
                    System.out.println("Finished clicking sequence. Moving to END state.");
                    setCurrentState(State.END);
                    return;
                }

                // If we are on cooldown from the last click, wait.
                if (clickCooldown > 0) {
                    return;
                }

                // Get the next item we need to click.
                Item itemToClick = chronomatronSlots.getLast();

                // Find the slot with that item and click it.
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (slot.getIndex() >= 10 && slot.getIndex() < 43 && slot.getStack().isOf(itemToClick)) {

                        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
                        if (interactionManager != null) {
                            System.out.println("Clicking " + itemToClick.toString() + " in slot " + slot.id);
                            interactionManager.clickSlot(
                                    screen.getScreenHandler().syncId,
                                    slot.id,
                                    0,
                                    SlotActionType.PICKUP,
                                    MinecraftClient.getInstance().player
                            );
                        }

                        // We've clicked, so move to the next item and start the cooldown.
                        System.out.println("Removing " + chronomatronSlots.getLast());
                        chronomatronSlots.removeLast();
                        clickCooldown = CLICK_DELAY_TICKS;
                        return; // Exit the tick to wait for the cooldown.
                    }
                }
            }


            case DORMANT -> {
                if (screen.getScreenHandler().getInventory().getStack(49).getName().getString().startsWith("Timer: ")) {
                    setCurrentState(State.RUNNING);
                }
            }
            case END -> {
                String name = screen.getScreenHandler().getInventory().getStack(49).getName().getString();
                if (!name.startsWith("Timer: ")) {
                    // Get ready for another round if the instructions say to remember the pattern.
                    if (name.equals("Remember the pattern!")) {
                        chronomatronChainLengthCount = 0;
                        chronomatronCurrentOrdinal = 0;
                        setCurrentState(State.LISTENING);
                        System.out.println("Returning to LISTENING state");
                    } else {
                        reset();
                    }
                }
            }
        }
    }

    public void reset() {
        chronomatronSlots.clear();
        chronomatronChainLengthCount = 0;
        chronomatronCurrentSlot = 0;
        chronomatronCurrentOrdinal = 0;
    }


}

