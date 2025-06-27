package com.willturr.etsolver.client.modules;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ContainerManager {

    private static boolean hasReceivedContents = false;
    private static boolean hasClickedDiamond = false;

    public static void init() {
        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> { //called upon every screen render

            if (screen instanceof GenericContainerScreen) { //if screen is a container

                hasReceivedContents = false;

                ScreenEvents.afterRender(screen).register((itemScreen, context, mouseX, mouseY, delta) -> { //called every frame container is open
                    if (!hasReceivedContents) {
                        GenericContainerScreen genericContainerScreen = (GenericContainerScreen) itemScreen;

                        int containerRows = genericContainerScreen.getScreenHandler().getRows();
                        int containerSlots = containerRows * 9;

                        List<Slot> containerItems = genericContainerScreen.getScreenHandler().slots.subList(0, containerSlots);

                        System.out.println("---CHEST CONTENTS---");
                        for (Slot slot : containerItems) {
                            System.out.println(slot.getStack().toString());
                        }

                        hasReceivedContents = true;
                    }

                    if (!hasClickedDiamond) {
                        GenericContainerScreen genericContainerScreen = (GenericContainerScreen) itemScreen;
                        System.out.println("Running clickItem.");
                        clickItem(client, genericContainerScreen);
                    }
                });

                ScreenEvents.remove(screen).register((closedScreen) -> {
                    hasReceivedContents = false;
                    hasClickedDiamond = false;
                });

            }

        });
    }

    private static void clickItem(MinecraftClient client, GenericContainerScreen screen) {

        int containerSlotCount = screen.getScreenHandler().getRows() * 9;
        List<Slot> slots = screen.getScreenHandler().slots.subList(0, containerSlotCount);

        for (Slot slot : slots) {
            System.out.println(slot.getStack().getItem().toString());
            if (slot.getStack().getItem() == Items.DIAMOND) {
                System.out.println("Diamond Found, slot. " + slot.id);

                //obtain player interactions and ensure not null
                ClientPlayerInteractionManager interactionManager = client.interactionManager;
                if (interactionManager == null) {
                    System.out.println("null interactionManager");
                    return;
                };

                System.out.println("Going for clickslot...");
                interactionManager.clickSlot(
                        screen.getScreenHandler().syncId,
                        slot.id,
                        0,
                        SlotActionType.PICKUP,
                        client.player
                );

                showMessage(client, "Diamond Clicked! §6[MVP§f++§6]", Formatting.AQUA);
                hasClickedDiamond = true;
                break;

            }

        }
    }

    private static void showMessage(MinecraftClient client, String message, Formatting color) {
        if (client.player == null) return;

        Text text = Text.literal(message).formatted(color);

        client.player.sendMessage(text, false);
    }

}
