package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class LongConfigEntry extends AbstractNumberConfigEntry<Long> {
    public LongConfigEntry(Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Long> configValue, ForgeConfigSpec.ValueSpec valueSpec) {
        super(label, tracker, configValue, valueSpec);
    }

    @Override
    protected Long getTypeMin() {
        return Long.MIN_VALUE;
    }

    @Override
    protected Long getTypeMax() {
        return Long.MAX_VALUE;
    }

    @Override
    protected Long parse(String text) throws NumberFormatException {
        return Long.parseLong(text);
    }
}
