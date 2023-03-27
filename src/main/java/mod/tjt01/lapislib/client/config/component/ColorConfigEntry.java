package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.screen.ColorPickerScreen;
import mod.tjt01.lapislib.util.ColorCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ColorConfigEntry<T> extends LabeledConfigEntry{
    protected final ColorPickerButton button;
    protected final ConfigChangeTracker tracker;
    protected final ImmutableList<ColorPickerButton> buttons;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<T> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;
    protected final boolean hasAlpha;

    public ColorConfigEntry(
            Component label, Screen parent, ConfigChangeTracker tracker,
            ForgeConfigSpec.ConfigValue<T> configValue, ForgeConfigSpec.ValueSpec valueSpec, boolean hasAlpha
    ) {
        super(label);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;
        this.hasAlpha = hasAlpha;

        this.button = new ColorConfigEntry.ColorPickerButton(0, 0, 20, 20, parent, this);

        buttons = ImmutableList.of(button);
    }

    public abstract int getColor();

    public abstract void setColor(int value);

    public void set(T value) {
        tracker.setValue(path, configValue, value);
    }

    public T get() {
        return tracker.getValue(path, configValue);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        button.x = left + width - 20;
        button.y = top;
        button.render(poseStack, mouseX, mouseY, pPartialTick);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return buttons;
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return buttons;
    }

    public static class ColorPickerButton extends AbstractButton {
        private static final ResourceLocation image = new ResourceLocation(
                "lapislib", "textures/gui/config/color_picker_button.png"
        );
        private final ColorConfigEntry<?> entry;
        private final Screen parent;

        public ColorPickerButton(int x, int y, int width, int height, Screen parent, ColorConfigEntry<?> entry) {
            super(x, y, width, height, new TranslatableComponent("lapislib.common.config.open_picker"));
            this.parent = parent;
            this.entry = entry;
        }

        @Override
        public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, image);
            RenderSystem.setShaderColor(1, 1, 1, this.alpha);
            int uvY = this.getYImage(this.isHoveredOrFocused());
            int color = entry.getColor();
            if (!entry.hasAlpha) color |= 0xFF000000;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(poseStack, this.x, this.y, 0, uvY * 20, 20, 20, 64, 64);
            fill(poseStack, this.x + 5, this.y + 5, this.x + 15, this.y + 15, color);
        }

        @Override
        public void onPress() {
            Minecraft.getInstance().setScreen(new ColorPickerScreen(parent, entry.hasAlpha, entry));
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {

        }
    };

    public static class StringColorConfigEntry extends ColorConfigEntry<String> {

        public StringColorConfigEntry(
                Component label, Screen parent,
                ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<String> configValue,
                ForgeConfigSpec.ValueSpec valueSpec, boolean hasAlpha)
        {
            super(label, parent, tracker, configValue, valueSpec, hasAlpha);
        }

        @Override
        public int getColor() {
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
        public int getColor() {
            return get();
        }

        @Override
        public void setColor(int value) {
            set(value);
        }
    }
}
