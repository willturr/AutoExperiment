package com.willturr.etsolver.client.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.willturr.etsolver.client.EtsolverClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.compress.harmony.pack200.NewAttributeBands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Inject(method = "syncState", at = @At("TAIL"))
    private void updateToClient(CallbackInfo ci, @Local(name="list") List<ItemStack> capturedList) {
        ScreenHandler screenHandler = (ScreenHandler) (Object) this;
        System.out.println("We have a handler.");

        System.out.println(capturedList);


    }

}