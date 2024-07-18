package mod.tjt01.lapislib.client.config.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

public class EnumEntry extends AbstractForgeConfigEntry<Enum<?>>{
    protected final CycleButton<Enum<?>> cycleButton;
    protected final ConfigChangeTracker tracker;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<Enum<?>> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;

    public EnumEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Enum<?>> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label, tracker, configValue, valueSpec);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;

        this.cycleButton = new CycleButton.Builder<Enum<?>>(o -> Component.literal(o.toString()))
                .displayOnlyValue()
                .withValues(tracker.getValue(path, configValue).getDeclaringClass().getEnumConstants())
                .withInitialValue(tracker.getValue(path, configValue))
                .create(0, 0, 110, 20, this.label, (cycleButton, value) -> {
                    tracker.setValue(path, configValue, value);
                });

        this.widgets.add(cycleButton);
    }

    @Override
    public void onResetOrUndo() {
        this.cycleButton.setValue(this.tracker.getValue(this.path, this.configValue));
    }

    protected String enumToString(Enum<?> enm) {
        return enm.toString();
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        cycleButton.x = left + width - 110 - 40;
        cycleButton.y = top;
        cycleButton.render(poseStack, mouseX, mouseY, pPartialTick);
    }
}
