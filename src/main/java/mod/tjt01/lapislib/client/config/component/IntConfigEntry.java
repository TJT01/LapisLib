package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class IntConfigEntry extends AbstractNumberConfigEntry<Integer> {
    public IntConfigEntry(Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Integer> configValue, ForgeConfigSpec.ValueSpec valueSpec) {
        super(label, tracker, configValue, valueSpec);
    }

    @Override
    protected Integer getTypeMin() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected Integer getTypeMax() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected Integer parse(String text) throws NumberFormatException {
        return Integer.parseInt(text);
    }
}
