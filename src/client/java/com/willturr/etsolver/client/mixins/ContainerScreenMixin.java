package com.willturr.etsolver.client.mixins;

import com.willturr.etsolver.client.config.Config;
import com.willturr.etsolver.client.config.ConfigManager;
import com.willturr.etsolver.client.modules.SolverManager;

import com.willturr.etsolver.client.screens.ConfigScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * This mixin targets HandledScreen, which is the correct base class for GUIs
 * that involve inventories (like chests, merchants, anvils, etc.).
 * This version is updated to use DrawContext for modern Minecraft versions (1.19.4+).
 */
@Mixin(HandledScreen.class)
public abstract class ContainerScreenMixin extends Screen {

    //container titles where options will appear
    @Unique
    private static final List<String> TARGET_SCREEN_TITLES = Arrays.asList(
            //"etsolver debug",
            //"Experimentation Table",
            "Chronomatron ➜ Stakes",
            "Ultrasequencer ➜ Stakes"
    );

    //use @Shadow to get access to protected fields from target class
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    @Unique
    private ButtonWidget modSettingsButton;
    @Unique
    private SliderWidget tickSlider;
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
            int buttonY = this.y;

            this.modSettingsButton = ButtonWidget.builder(Text.of("Start!"), button -> {
                if (this.client != null) {
                    //forgoing this in favour of an all-next-to-container gui. may change?
                    //this.client.setScreen(new ConfigScreen(this));
                    SolverManager.starterMotor();
                }
            }).dimensions(buttonX, buttonY, 100, 20).build();

            this.tickSlider = new SliderWidget(buttonX, (buttonY + 25), 100, 20, Text.of(""), Config.universalTickDelay/10) {

                //instance initialiser so the slider has text from the getgo
                {
                    updateMessage();
                }

                @Override
                protected void updateMessage() {
                    setMessage(Text.of("Tick Delay: " + (int)(10 * this.value)));
                }

                @Override
                protected void applyValue() {
                    Config.universalTickDelay = (int)(10 * this.value);
                    ConfigManager.saveConfig();
                }
            };

            this.addDrawableChild(this.modSettingsButton);

            this.addDrawableChild(this.tickSlider);
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