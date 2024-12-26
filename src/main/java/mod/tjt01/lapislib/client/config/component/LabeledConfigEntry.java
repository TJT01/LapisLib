package mod.tjt01.lapislib.client.config.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public abstract class LabeledConfigEntry extends ConfigEntry{
    protected final Component label;

    public LabeledConfigEntry(Component label) {
        this.label = label;
    }

    public int getColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public void render(
            @Nonnull GuiGraphics guiGraphics, int index,
            int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isMouseOver,
            float pPartialTick
    ) {
        guiGraphics.drawString(Minecraft.getInstance().font, label, left, top + 6, getColor());
    }
}
