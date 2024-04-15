package mod.tjt01.lapislib.client.config.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

public class BooleanConfigEntry extends AbstractForgeConfigEntry<Boolean> {
    protected final Checkbox checkbox;

    public BooleanConfigEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Boolean> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label, tracker, configValue, valueSpec);

        this.checkbox = new BooleanConfigCheckbox(
                0, 0, 20, 20,
                label, tracker.getValue(path, configValue), this
        );
        this.widgets.add(checkbox);
    }

    protected void set(boolean value) {
        tracker.setValue(path, configValue, value);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        checkbox.x = left + width - 60;
        checkbox.y = top;
        checkbox.render(poseStack, mouseX, mouseY, pPartialTick);
    }

    @Override
    public void onResetOrUndo() {
        this.checkbox.selected = tracker.getValue(this.path, this.configValue);
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
