package mod.tjt01.lapislib.client.config.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractForgeConfigEntry<T> extends LabeledConfigEntry {
    protected static final TranslatableComponent RESET_BUTTON_TEXT
            = new TranslatableComponent("lapislib.common.config.reset.text");
    protected static final TranslatableComponent RESET_TOOLTIP
            = new TranslatableComponent("lapislib.common.config.reset.tooltip");

    protected static final TranslatableComponent UNDO_BUTTON_TEXT
            = new TranslatableComponent("lapislib.common.config.undo.text");
    protected static final TranslatableComponent UNDO_TOOLTIP
            = new TranslatableComponent("lapislib.common.config.undo.tooltip");

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

        resetButton = new Button(
                0, 0, 20, 20,
                RESET_BUTTON_TEXT, button -> {
                    this.tracker.setValue(this.path, configValue, (T) valueSpec.getDefault());
                    onResetOrUndo();
                }
        );

        undoButton = new Button(
                0, 0, 20, 20,
                UNDO_BUTTON_TEXT, button -> {
                    this.tracker.setValue(this.path, configValue, configValue.get());
                    onResetOrUndo();
                }
        );
        
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
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);

        resetButton.x = left + width - 20;
        resetButton.y = top;
        resetButton.active = !Objects.equals(this.tracker.getValue(this.path, this.configValue), this.valueSpec.getDefault());

        resetButton.render(poseStack, mouseX, mouseY, pPartialTick);

        undoButton.x = left + width - 40;
        undoButton.y = top;
        undoButton.active = this.tracker.hasValue(this.path);

        undoButton.render(poseStack, mouseX, mouseY, pPartialTick);
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
