package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class DoubleConfigEntry extends AbstractNumberConfigEntry<Double> {
    public DoubleConfigEntry(Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Double> configValue, ForgeConfigSpec.ValueSpec valueSpec) {
        super(label, tracker, configValue, valueSpec);
    }

    @Override
    protected Double getTypeMin() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    protected Double getTypeMax() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    protected Double parse(String text) throws NumberFormatException {
        return Double.parseDouble(text);
    }
}
