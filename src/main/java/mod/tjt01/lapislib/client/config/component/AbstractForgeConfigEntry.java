package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class AbstractForgeConfigEntry<T> extends LabeledConfigEntry {
    protected static final Component RESET_BUTTON_TEXT
            = Component.translatable("lapislib.common.config.reset.text");
    protected static final Component RESET_TOOLTIP
            = Component.translatable("lapislib.common.config.reset.tooltip");

    protected static final Component UNDO_BUTTON_TEXT
            = Component.translatable("lapislib.common.config.undo.text");
    protected static final Component UNDO_TOOLTIP
            = Component.translatable("lapislib.common.config.undo.tooltip");

    protected static final Style RANGE_STYLE = Style.EMPTY.withColor(ChatFormatting.YELLOW);

    protected final ConfigChangeTracker tracker;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<T> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;
    protected final List<AbstractWidget> widgets = new ArrayList<>();
    protected final Button resetButton;
    protected final Button undoButton;

    @SuppressWarnings("unchecked")
    public AbstractForgeConfigEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<T> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;

        resetButton = Button.builder(RESET_BUTTON_TEXT, button -> {
            this.tracker.setValue(this.path, configValue, (T) valueSpec.getDefault());
            onResetOrUndo();
        })
                .size(20, 20)
                .tooltip(Tooltip.create(RESET_BUTTON_TEXT))
                .build();

        undoButton = Button.builder(UNDO_BUTTON_TEXT, button -> {
            this.tracker.setValue(this.path, configValue, configValue.get());
            onResetOrUndo();
        })
                .size(20, 20)
                .tooltip(Tooltip.create(UNDO_BUTTON_TEXT))
                .build();
        
        widgets.add(resetButton);
        widgets.add(undoButton);
    }

    public boolean isValid() {
        return true;
    }

    public void onResetOrUndo() {

    }

    @Override
    public int getColor() {
        if (!isValid()) {
            return 0xFFFF7F7F;
        } else if (this.tracker.hasValue(this.path)) {
            return 0xFFFFFF7F;
        } else {
            return super.getColor();
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);

        resetButton.setPosition(left + width - 20, top);
        resetButton.active = !Objects.equals(this.tracker.getValue(this.path, this.configValue), this.valueSpec.getDefault());

        resetButton.render(guiGraphics, mouseX, mouseY, pPartialTick);

        undoButton.setPosition(left + width - 40, top);
        undoButton.active = this.tracker.hasValue(this.path);

        undoButton.render(guiGraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    public List<FormattedCharSequence> getTooltip(int top, int left, int mouseX, int mouseY) {
        if (resetButton.isHoveredOrFocused()) {
            return Collections.singletonList(RESET_TOOLTIP.getVisualOrderText());
        } else if (undoButton.isHoveredOrFocused()) {
            return Collections.singletonList(UNDO_TOOLTIP.getVisualOrderText());
        } else {
            ArrayList<FormattedCharSequence> charSequences = new ArrayList<>();
            charSequences.add(Component.literal(path).getVisualOrderText());
            charSequences.add(FormattedCharSequence.EMPTY);

            for (String line: StringUtils.split(this.valueSpec.getComment(), '\n')) {
                charSequences.add(FormattedCharSequence.forward(
                        line,
                        line.startsWith("Range: ") || line.startsWith("Allowed Values: ") ? RANGE_STYLE : Style.EMPTY
                ));
            }

            return charSequences;
        }
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }
}
