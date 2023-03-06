package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.List;

public class BooleanConfigEntry extends LabeledConfigEntry{
    protected final Checkbox checkbox;
    protected final ConfigChangeTracker tracker;
    protected final ImmutableList<Checkbox> checkboxes;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<Boolean> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;

    public BooleanConfigEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Boolean> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;

        this.checkbox = new BooleanConfigCheckbox(
                0, 0, 20, 20,
                label, tracker.getValue(path, configValue), this
        );
        checkboxes = ImmutableList.of(checkbox);
    }

    protected void set(boolean value) {
        tracker.setValue(path, configValue, value);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        checkbox.x = left + 220 - 20;
        checkbox.y = top;
        checkbox.render(poseStack, mouseX, mouseY, pPartialTick);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return checkboxes;
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return checkboxes;
    }

    protected static class BooleanConfigCheckbox extends Checkbox {
        protected final BooleanConfigEntry parent;

        public BooleanConfigCheckbox(
                int x, int y, int width, int height,
                Component message, boolean selected, BooleanConfigEntry parent
        ) {
            super(x, y, width, height, message, selected, false);
            this.parent = parent;
        }

        @Override
        public void onPress() {
            super.onPress();
            this.parent.set(this.selected());
        }
    }
}
