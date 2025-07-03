package com.willturr.etsolver.client.screens;

import com.willturr.etsolver.client.config.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import net.minecraft.client.util.math.MatrixStack;

public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.of("AutoExperiment Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        //title text
        TextWidget titleWidget = new TextWidget((this.width / 2), 20, 150, 20, this.title, this.textRenderer);
        titleWidget.setPosition((this.width / 2) - (titleWidget.getWidth() / 2), 20);
        this.addDrawableChild(titleWidget);

        //slider for ultrasequencer tick delay setting
        this.addDrawableChild(new SliderWidget(this.width / 4 - 100, 90, 200, 20, Text.of("Ultrasequencer Tick Delay: " + Config.ultrasequencerTickDelay), Config.ultrasequencerTickDelay / 20) {
            @Override
            protected void updateMessage() {
                setMessage(Text.of("Ultrasequencer Tick Delay: " + Config.ultrasequencerTickDelay));
            }

            @Override
            protected void applyValue() {
                Config.ultrasequencerTickDelay = (int)(this.value * 10);
                updateMessage();
            }
        });


        //done button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Done"), button -> {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(this.width / 2 - 100, this.height - 40, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void close() {
        if(this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
