package mod.tjt01.lapislib.client.config.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.component.ColorConfigEntry;
import mod.tjt01.lapislib.util.ColorCodec;
import mod.tjt01.lapislib.util.ColorSequence;
import mod.tjt01.lapislib.util.client.ExtraGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerScreen extends Screen {
    private static final ResourceLocation CHECKERBOARD_TEXTURE = new ResourceLocation(
            LapisLib.MODID, "textures/gui/config/checkerboard.png"
    );

    public static final TranslatableComponent title = new TranslatableComponent("lapislib.common.config.color_picker");

    private final Screen parent;
    private final ColorConfigEntry<?> entry;

    private SaturationValueBox saturationValueBox;
    private GradientSlider redSlider;
    private GradientSlider greenSlider;
    private GradientSlider blueSlider;
    private GradientSlider alphaSlider;

    private EditBox hexCodeBox;

    private HueSlider hueSlider;

    public final boolean useAlpha;

    public int color = 0xFFFFFFFF;

    public float hue = 1;
    public float sat = 1;
    public float val = 1;

    public int alpha = 255;

    public ColorPickerScreen(Screen parent, boolean hasAlpha, ColorConfigEntry<?> entry) {
        super(title);
        this.parent = parent;
        this.useAlpha = hasAlpha;
        this.entry = entry;

        this.color = entry.getColor();

        this.RGBChanged();
    }

    protected void saveAndExit() {

        this.getMinecraft().setScreen(parent);
    }

    protected void RGBChanged() {
        if (useAlpha) {
            this.alpha = color >>> 24 & 0xFF;
        }
        int red = (color >>> 16 & 0xFF);
        int green = (color >>> 8 & 0xFF);
        int blue = (color & 0xFF);

        float[] hsv = Color.RGBtoHSB(red, green, blue, null);
        hue = hsv[0];
        sat = hsv[1];
        val = hsv[2];

        if (this.hueSlider != null) {
            hueSlider.value = hue;
        }
        if (this.saturationValueBox != null) {
            saturationValueBox.setHue(hue);
            saturationValueBox.sat = this.sat;
            saturationValueBox.val = this.val;
        }

        updateSliderColors();

    }

    protected void updateSliderColors() {
        if (redSlider != null) {
            redSlider.startColor = color & (~0xFFFF0000) | 0xFF000000;
            redSlider.endColor = color & (~0xFFFF0000) | 0xFFFF0000;
            this.redSlider.value = (this.color >> 16 & 255) / 255.0D;
        }
        if (greenSlider != null) {
            greenSlider.startColor = color & (~0xFF00FF00) | 0xFF000000;
            greenSlider.endColor = color & (~0xFF00FF00) | 0xFF00FF00;
            this.greenSlider.value = (this.color >> 8 & 255) / 255.0D;
        }
        if (blueSlider != null) {
            blueSlider.startColor = color & (~0xFF0000FF) | 0xFF000000;
            blueSlider.endColor = color & (~0xFF0000FF) | 0xFF0000FF;
            this.blueSlider.value = (this.color & 255) / 255.0D;
        }
    };

    @Override
    public void tick() {
        if (this.hexCodeBox != null) {
            this.hexCodeBox.tick();
        }
    }

    protected void HSVChanged() {
        this.color = Mth.hsvToRgb(hue % 1, Mth.clamp(sat, 0, 1), Mth.clamp(val, 0, 1));
        if (useAlpha) {
            this.color |= (alpha << 24);
        }
        updateSliderColors();
        this.entry.setColor(this.color);
    }

    public void setHue(float hue) {
        this.hue = hue % 1.0F;
        HSVChanged();
        saturationValueBox.setHue(hue);
    }

    public void setSaturation(float sat) {
        this.sat = sat;
        HSVChanged();
    }

    public void setValue(float val) {
        this.val = val;
        HSVChanged();
    }

    @Override
    protected void init() {
        this.addRenderableWidget(
                new Button(
                        this.width/2 - 64, this.height - 32, 128, 20, CommonComponents.GUI_DONE,
                        button -> this.getMinecraft().setScreen(parent)
                )
        );

        this.saturationValueBox = new SaturationValueBox(
                this,
                this.width/2 - 100 - 4, this.height/2 - 58, 100, 116 - 24,
                CommonComponents.GUI_YES
        );
        this.saturationValueBox.setHue(this.hue);
        this.saturationValueBox.sat = this.sat;
        this.saturationValueBox.val = this.val;
        this.addRenderableWidget(this.saturationValueBox);

        double red = (this.color >> 16 & 255) / 255.0D;
        double green = (this.color >> 8 & 255) / 255.0D;
        double blue = (this.color & 255) / 255.0D;

        this.redSlider = new GradientSlider(
                this.width/2 + 4, this.height/2 - 58, 100, 20,
                CommonComponents.GUI_YES,
                red, 0, 0,
                (value, gradientSlider) -> {
                    this.color = color & 0xFF00FFFF | ((int) (value*255) << 16);
                    this.entry.setColor(this.color);
                    RGBChanged();
                }
        );

        this.greenSlider = new GradientSlider(
                this.width/2 + 4, this.height/2 - 34, 100, 20,
                CommonComponents.GUI_YES,
                green, 0, 0,
                (value, gradientSlider) -> {
                    this.color = color & 0xFFFF00FF | ((int) (value*255) << 8);
                    this.entry.setColor(this.color);
                    RGBChanged();
                }
        );

        this.blueSlider = new GradientSlider(
                this.width/2 + 4, this.height/2 - 10, 100, 20,
                CommonComponents.GUI_YES,
                blue, 0, 0,
                (value, gradientSlider) -> {
                    this.color = color & 0xFFFFFF00 | (int) (value*255);
                    this.entry.setColor(this.color);
                    RGBChanged();
                }
        );

        this.hueSlider = new HueSlider(
                this.width/2 - 100 - 4, this.height/2 + 58 - 20, 100, 20,
                CommonComponents.GUI_BACK, this.hue, this
        );

        this.hexCodeBox = new EditBox(
                this.font, this.width/2 + 4, this.height/2 + 58 - 20, 76, 20,
                new TextComponent("")
        );

        this.hexCodeBox.setValue(
                this.useAlpha ?
                        ColorCodec.encodeARGB(color, true) :
                        ColorCodec.encodeRGB(color, true)
        );

        this.hexCodeBox.setMaxLength(useAlpha ? 9 : 7);

        this.hexCodeBox.setFilter(s -> {
            if (s == null) {
                return false;
            }
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!((c == '#' && i == 0) || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))) {
                    return false;
                }
            }
            return true;
        });

        this.hexCodeBox.setResponder(s -> {
            try {
                if (this.hexCodeBox.isFocused()) {
                    color = ColorCodec.decode(s);
                    this.entry.setColor(this.color);
                    this.RGBChanged();
                }
            } catch (NumberFormatException ignored) {}
        });

        this.addRenderableWidget(this.hueSlider);
        this.addRenderableWidget(this.redSlider);
        this.addRenderableWidget(this.greenSlider);
        this.addRenderableWidget(this.blueSlider);
        this.addRenderableWidget(this.hexCodeBox);

        if (this.useAlpha) {
            this.alphaSlider = new GradientSlider(
                    this.width/2 + 4, this.height/2 + 14, 100, 20,
                    CommonComponents.GUI_YES,
                    alpha / 255.0F, -1, 0,
                    (value, gradientSlider) -> {
                        this.color = color & 0x00FFFFFF | ((int) (value * 255) << 24);
                        this.alpha = (int) (value * 255);
                        this.entry.setColor(this.color);
                    }
            );

            this.addRenderableWidget(this.alphaSlider);
        }

        this.updateSliderColors();
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int displayColor = this.color;
        if (!useAlpha) displayColor |= 0xFF000000;
        fill(
                poseStack, this.width/2 + 4 + 80 - 1, this.height/2 + 58 - 20 - 1,
                this.width/2 + 4 + 100 + 1, this.height/2 + 58 + 1, 0xFF000000
        );

        RenderSystem.setShaderTexture(0, CHECKERBOARD_TEXTURE);

        blit(poseStack, this.width/2 + 4 + 80, this.height/2 + 58 - 20, 0, 0, 20, 20, 32, 32);

        fill(
                poseStack, this.width/2 + 4 + 80, this.height/2 + 58 - 20,
                this.width/2 + 4 + 100, this.height/2 + 58, displayColor
        );

        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.getMinecraft().setScreen(parent);
    }

    private static class GradientSlider extends AbstractWidget {
        public BiConsumer<Double, GradientSlider> onChanged;
        public int startColor;
        public int endColor;

        private double value;

        public GradientSlider(
                int x, int y, int width, int height, Component message, double value,
                int startColor, int endColor, BiConsumer<Double, GradientSlider> onChanged
                ) {
            super(x, y, width, height, message);
            this.value = value;
            this.onChanged = onChanged;
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            setValueFromMouseX(mouseX);
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            setValueFromMouseX(mouseX);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            int valuePixels = (int) (value * this.width);

            fill(poseStack, x - 1, y - 1, x + width + 1, y + height + 1, 0xFF000000);
            ExtraGuiUtils.fillHorizontalGradient(
                    poseStack,
                    x, y, x + width, y + height,
                    startColor, endColor,
                    this.getBlitOffset()
            );
            fill(
                    poseStack,
                    this.x + valuePixels - 1, this.y, this.x + valuePixels + 1, this.y + this.height,
                    0x7F000000
            );
        }

        protected void setValueFromMouseX(double mouseX) {
            this.setValue(Mth.clamp((mouseX - (double) this.x) / (double) this.width, 0.0D, 1.0D));
        }

        public void setValue(double value) {
            if (this.value != value) {
                this.value = value;
                this.onChanged.accept(value, this);
            }
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {

        }
    }

    private static class HueSlider extends AbstractWidget {
        private final ColorPickerScreen parent;

        public double value;

        private static final ColorSequence sequence = ColorSequence
                .builder(0xFFFF0000)
                .add(0xFFFFFF00, 1/6.0D)
                .add(0xFF00FF00, 2/6.0D)
                .add(0xFF00FFFF, 3/6.0D)
                .add(0xFF0000FF, 4/6.0D)
                .add(0xFFFF00FF, 5/6.0D)
                .end(0xFFFF0000);

        public HueSlider(
                int pX, int pY, int pWidth, int pHeight, Component pMessage, double value, ColorPickerScreen parent
        ) {
            super(pX, pY, pWidth, pHeight, pMessage);
            this.value = value;
            this.parent = parent;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            setValueFromMouseX(mouseX);
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            setValueFromMouseX(mouseX);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            int valuePixels = (int) (value * this.width);

            fill(poseStack, x - 1, y - 1, x + width + 1, y + height + 1, 0xFF000000);
            ExtraGuiUtils.sequenceHorizontalGradient(poseStack, x, y, x + width, y + height, sequence, getBlitOffset());
            fill(
                    poseStack,
                    this.x + valuePixels - 1, this.y, this.x + valuePixels + 1, this.y + this.height,
                    0x7F000000
            );
        }

        protected void setValueFromMouseX(double mouseX) {
            this.setValue(Mth.clamp((mouseX - (double) this.x) / (double) this.width, 0.0D, 1.0D));
        }

        protected void setValue(double value) {
            if (this.value != value) {
                this.value = value;
                this.parent.setHue((float) this.value);
            }
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {

        }
    }

    private static class SaturationValueBox extends AbstractWidget {
        private final ColorPickerScreen parent;
        private float hue;
        private float sat;
        private float val;

        public SaturationValueBox(
                ColorPickerScreen parent,
                int x, int y, int width, int height,
                Component message
        ) {
            super(x, y, width, height, message);
            this.parent = parent;
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
            // TODO
        }

        protected void HSVChanged(float hue, float sat, float val) {
            this.hue = hue;
            this.sat = sat;
            this.val = val;
        };

        private void onInput(double mouseX, double mouseY) {
            this.sat = (float) Mth.clamp((mouseX - x) / (float)this.width, 0.0F, 1.0F);
            this.val = (float) Mth.clamp(1 - ((mouseY - y) / (float)this.height), 0.0F, 1.0F);
            this.parent.setSaturation(this.sat);
            this.parent.setValue(this.val);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            int col = Mth.hsvToRgb(this.hue % 1, 1, 1);

            fill(poseStack, x - 1, y - 1, width + x + 1, height + y + 1, 0xFF000000);
            ExtraGuiUtils.fillHorizontalGradient(
                    poseStack, x, y, width + x, height + y,
                    0xFFFFFFFF, col | 0xFF000000,
                    this.getBlitOffset()
            );
            this.fillGradient(poseStack, x, y, width + x, height + y, 0, 0xFF000000);
            fill(poseStack, x, (int) (y + (1 - val)*height - 1), x + width, (int) (y + (1 - val)*height + 1), 0x7F000000);
            fill(poseStack, (int) (x + sat*width - 1), y, (int) (x + sat*width + 1), y + height, 0x7F000000);
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            onInput(mouseX, mouseY);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            onInput(mouseX, mouseY);
        }

        public void setHue(float hue) {
            this.hue = hue;
        }
    }
}
