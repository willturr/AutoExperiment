package com.willturr.etsolver.client.mixins;

import com.willturr.etsolver.client.screens.ConfigScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

/**
 * This mixin targets HandledScreen, which is the correct base class for GUIs
 * that involve inventories (like chests, merchants, anvils, etc.).
 * This version is updated to use DrawContext for modern Minecraft versions (1.19.4+).
 */
@Mixin(HandledScreen.class)
public abstract class ContainerScreenMixin extends Screen {

    // A list of screen titles where you want the button to appear.
    @Unique
    private static final List<String> TARGET_SCREEN_TITLES = Arrays.asList(
            "Reforge Anvil",
            "Craft Item",
            "Your Example Merchant",
            "Chest"
            // Add any other screen titles you want to target here.
    );

    // Using @Shadow to get access to protected fields from the target class.
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    // Store the button and related text to render them.
    @Unique
    private ButtonWidget modSettingsButton;
    @Unique
    private Text modInfoText;
    @Unique
    private boolean shouldRenderElements = false;

    protected ContainerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.shouldRenderElements = false;
        String screenTitle = this.title.getString();

        if (TARGET_SCREEN_TITLES.contains(screenTitle)) {
            this.shouldRenderElements = true;

            int buttonX = this.x + this.backgroundWidth + 5;
            int buttonY = this.y + 20;

            this.modSettingsButton = ButtonWidget.builder(Text.of("Mod Settings"), button -> {
                if (this.client != null) {
                    this.client.setScreen(new ConfigScreen(this));
                }
            }).dimensions(buttonX, buttonY, 100, 20).build();

            this.modInfoText = Text.of("QoL Mod Options");
            this.addDrawableChild(this.modSettingsButton);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.shouldRenderElements && this.modSettingsButton != null && this.textRenderer != null) {
            String displayText = " hello ";
            int textWidth = this.textRenderer.getWidth(displayText);
            int textX = this.modSettingsButton.getX() + (this.modSettingsButton.getWidth() - textWidth) / 2;
            int textY = this.modSettingsButton.getY() + this.modSettingsButton.getHeight() + 5;

            context.drawTextWithShadow(this.textRenderer, displayText, textX, textY, 0xFFFFFF);
        }
    }
}