package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CategoryEntry extends ConfigEntry {
    private final Button button;
    private final ImmutableList<Button> buttons;

    public CategoryEntry(Screen parent, Component text, Function<Screen, Screen> screenFunction) {
//        this.button = new Button(0, 0, 260, 20, text, pButton ->
//                Minecraft.getInstance().setScreen(screenFunction.apply(parent))
//        );
        this.button = Button.builder(text, b -> {
            Minecraft.getInstance().setScreen(screenFunction.apply(parent));
        }).build();
        buttons = ImmutableList.of(button);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return buttons;
    }

    @Override
    public void render(
            @Nonnull GuiGraphics guiGraphics, int index,
            int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isMouseOver,
            float partialTick
    ) {
        this.button.setPosition(left, top);

        this.button.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return buttons;
    }

    @Override
    public List<FormattedCharSequence> getTooltip(int top, int left, int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
