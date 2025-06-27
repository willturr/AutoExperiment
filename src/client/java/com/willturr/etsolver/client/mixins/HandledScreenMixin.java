package com.willturr.etsolver.client.mixins;

import com.willturr.etsolver.client.EtsolverClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onScreenOpen(CallbackInfo ci) {
        // this is current handledscreen instance
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        if (screen instanceof GenericContainerScreen) {
            ScreenHandler handler = screen.getScreenHandler();
            System.out.println("handler " + handler);

            int count = 0;

            for (Slot slot : handler.slots) {
                count += 1;
                System.out.println("Processing slot." + slot.getStack() + slot.getIndex() + " " + count + " slots counted");

                ItemStack stack = slot.getStack();
                System.out.println("Stack: " + stack.toString());

            }
        }
    }
}
