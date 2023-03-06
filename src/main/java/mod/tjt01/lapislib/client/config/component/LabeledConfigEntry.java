package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class LabeledConfigEntry extends ConfigEntry{
    protected final Component label;

    public LabeledConfigEntry(Component label) {
        this.label = label;
    }

    @Override
    public void render(
            @Nonnull PoseStack poseStack, int index,
            int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isMouseOver,
            float pPartialTick
    ) {
        Screen.drawString(poseStack, Minecraft.getInstance().font, label, left, top + 6, 0xFFFFFFFF);
    }
}
