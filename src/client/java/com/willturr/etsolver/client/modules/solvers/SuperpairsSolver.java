package com.willturr.etsolver.client.modules.solvers;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;

public class SuperpairsSolver extends AbstractSolver {

    protected SuperpairsSolver(String containerName) {
        super(containerName);
    }

    int startingSeconds = -1;

    @Override
    public void tick(GenericContainerScreen screen) {
        Inventory inventory = screen.getScreenHandler().getInventory();



    }


}
