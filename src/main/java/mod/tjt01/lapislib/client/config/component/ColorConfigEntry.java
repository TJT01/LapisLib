package mod.tjt01.lapislib.client.config.component;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.screen.ColorPickerScreen;
import mod.tjt01.lapislib.util.ColorCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

public abstract class ColorConfigEntry<T> extends AbstractForgeConfigEntry<T>{
    private static final ResourceLocation TEXTURE = new ResourceLocation(LapisLib.MODID, "textures/gui/config/color_picker_button.png");

    protected final ColorPickerButton button;
    protected final ConfigChangeTracker tracker;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<T> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;
    protected final boolean hasAlpha;

    public ColorConfigEntry(
            Component label, Screen parent, ConfigChangeTracker tracker,
            ForgeConfigSpec.ConfigValue<T> configValue, ForgeConfigSpec.ValueSpec valueSpec, boolean hasAlpha
    ) {
        super(label, tracker, configValue, valueSpec);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;
        this.hasAlpha = hasAlpha;

        this.button = new ColorConfigEntry.ColorPickerButton(0, 0, 20, 20, parent, this);

        this.widgets.add(button);
    }

    public abstract int getCurrentColor();

    public abstract void setColor(int value);

    public void set(T value) {
        tracker.setValue(path, configValue, value);
    }

    public T get() {
        return tracker.getValue(path, configValue);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        button.setPosition(left + width - 60, top);
        button.render(guiGraphics, mouseX, mouseY, pPartialTick);
    }

    public static class ColorPickerButton extends AbstractButton {
        private static final ResourceLocation image = new ResourceLocation(
                "lapislib", "textures/gui/config/color_picker_button.png"
        );
        private final ColorConfigEntry<?> entry;
        private final Screen parent;

        public ColorPickerButton(int x, int y, int width, int height, Screen parent, ColorConfigEntry<?> entry) {
            super(x, y, width, height, Component.translatable("lapislib.common.config.open_picker"));
            this.parent = parent;
            this.entry = entry;
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, image);
            RenderSystem.setShaderColor(1, 1, 1, this.alpha);
            int uvY = 0;
            if (this.isActive()) {
                uvY = 40;
            } else if (this.isHoveredOrFocused()) {
                uvY = 20;
            }

            int color = entry.getCurrentColor();
            if (!entry.hasAlpha) color |= 0xFF000000;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, uvY, 20, 20, 64, 64);
            guiGraphics.fill(this.getX() + 5, this.getY() + 5, this.getX() + 15, this.getY() + 15, color);
        }

        @Override
        public void onPress() {
            Minecraft.getInstance().setScreen(new ColorPickerScreen(parent, this.entry.label, entry.hasAlpha, entry));
        }

        @Override
        public void updateWidgetNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {

        }
    }

    public static class StringColorConfigEntry extends ColorConfigEntry<String> {

        public StringColorConfigEntry(
                Component label, Screen parent,
                ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<String> configValue,
                ForgeConfigSpec.ValueSpec valueSpec, boolean hasAlpha)
        {
            super(label, parent, tracker, configValue, valueSpec, hasAlpha);
        }

        @Override
        public int getCurrentColor() {
            try {
                return ColorCodec.decode(get());
            } catch (NumberFormatException ignored) {
                return 0xFFFF00FF;
            }
        }

        @Override
        public void setColor(int value) {
            set(this.hasAlpha ? ColorCodec.encodeARGB(value, false) : ColorCodec.encodeRGB(value, false));
        }
    }

    public static class IntColorConfigEntry extends ColorConfigEntry<Integer> {

        public IntColorConfigEntry(
                Component label, Screen parent,
                ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Integer> configValue,
                ForgeConfigSpec.ValueSpec valueSpec, boolean hasAlpha)
        {
            super(label, parent, tracker, configValue, valueSpec, hasAlpha);
        }

        @Override
        public int getCurrentColor() {
            return get();
        }

        @Override
        public void setColor(int value) {
            set(value);
        }
    }
}
