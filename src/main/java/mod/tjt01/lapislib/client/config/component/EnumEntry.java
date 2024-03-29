package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.command.TextComponentHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class EnumEntry extends LabeledConfigEntry{
    protected final CycleButton<Enum<?>> cycleButton;
    protected final ConfigChangeTracker tracker;
    protected final ImmutableList<CycleButton<Enum<?>>> cycleButtons;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<Enum<?>> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;

    public EnumEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Enum<?>> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;

        this.cycleButton = new CycleButton.Builder<Enum<?>>(o -> new TextComponent(o.toString()))
                .displayOnlyValue()
                .withValues(tracker.getValue(path, configValue).getDeclaringClass().getEnumConstants())
                .withInitialValue(tracker.getValue(path, configValue))
                .create(0, 0, 110, 20, this.label, (cycleButton, value) -> {
                    tracker.setValue(path, configValue, value);
                });

        cycleButtons = ImmutableList.of(cycleButton);
    }

    protected String enumToString(Enum<?> enm) {
        return enm.toString();
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        cycleButton.x = left + width - 110;
        cycleButton.y = top;
        cycleButton.render(poseStack, mouseX, mouseY, pPartialTick);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return cycleButtons;
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return cycleButtons;
    }
}
