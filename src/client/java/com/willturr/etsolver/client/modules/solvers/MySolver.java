package com.willturr.etsolver.client.modules.solvers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MySolver extends AbstractSolver {

    public MySolver(String containerName) {
        super(containerName);
    }

    MinecraftClient client = MinecraftClient.getInstance();

    boolean glintFound = false;
    int glintFoundAt = 0;

    List<Item> clickStack = new ArrayList<>();

    private boolean wasRemembering = false;
    Item lastSot = null;

    int chronomatronCount = 0;

    @Override
    protected void tick(GenericContainerScreen screen) {
        Inventory inventory = screen.getScreenHandler().getInventory();

        boolean isRemembering = inventory.getStack(49).getName().getString().equals("Remember the pattern!");

        Item currentSot = inventory.getStack(49).getItem();

        //counter
        if (currentSot == Items.GLOWSTONE && lastSot == Items.CLOCK) {
            chronomatronCount++;
            System.out.println("Chronomatron count: " + chronomatronCount);
        }



        if (isRemembering && !wasRemembering) {
            // Just entered remembering mode, reset state
            glintFound = false;
            glintFoundAt = 0;
        }

        if (lastSot != currentSot && lastSot == Items.CLOCK) {
            System.out.println("Skipping, clock last run");
            lastSot = currentSot;
            return;
        } else if (isRemembering) {
            if (!glintFound) {
                for (int i = 10; i < 43; i++) {
                    if (inventory.getStack(i).hasGlint()) {
                        glintFound = true;
                        glintFoundAt = i;
                        clickStack.add(inventory.getStack(i).getItem());
                        System.out.println(clickStack);
                        break;
                    }
                }
            } else {
                if (!inventory.getStack(glintFoundAt).hasGlint()) {
                    glintFound = false;
                    glintFoundAt = 0;
                }
            }
        } else if (wasRemembering) {
            // Just exited remembering mode, check for any remaining glint
            for (int i = 10; i < 43; i++) {
                if (inventory.getStack(i).hasGlint()) {
                    clickStack.add(inventory.getStack(i).getItem());
                    System.out.println(clickStack);
                    break;
                }
            }
            glintFound = false;
            glintFoundAt = 0;
        }

        if (clickStack.size() > chronomatronCount) {
            clickStack.removeLast();
            System.out.println("Removed erroneous entry");
        }

        wasRemembering = isRemembering;
        lastSot = currentSot;
    }

    public void clearClickStack() {
        clickStack.clear();
        glintFound = false;
        glintFoundAt = 0;
    }
}