package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class CategoryEntry extends ConfigEntry {
    private final Button button;
    private final ImmutableList<Button> buttons;

    public CategoryEntry(Screen parent, Component text, Function<Screen, Screen> screenFunction) {
        this.button = new Button(0, 0, 220, 20, text, pButton ->
                Minecraft.getInstance().setScreen(screenFunction.apply(parent))
        );
        buttons = ImmutableList.of(button);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return buttons;
    }

    @Override
    public void render(
            @Nonnull PoseStack poseStack, int index,
            int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isMouseOver,
            float partialTick
    ) {
        this.button.x = left;
        this.button.y = top;

        this.button.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return buttons;
    }
}
