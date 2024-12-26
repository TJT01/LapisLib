package mod.tjt01.lapislib.client.config.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;


import javax.annotation.Nonnull;
import java.util.List;

public class InvalidConfigEntry extends ConfigEntry{
    private final Component label;

    public InvalidConfigEntry(String type, String path) {
        this.label = Component.translatable("lapislib.common.config.unsupported", type, path);
    }

    public InvalidConfigEntry(Component component) {
        this.label = component;
    }

    @Override
    public void render(
            @Nonnull GuiGraphics guiGraphics, int index,
            int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isMouseOver,
            float pPartialTick
    ) {
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, label, left + width/2, top + 6, 0xFFFF7F7F);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of();
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }

    @Override
    public List<FormattedCharSequence> getTooltip(int top, int left, int mouseX, int mouseY) {
        return List.of();
    }
}
