package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class StringConfigEntry extends AbstractTextConfigEntry<String> {

    public StringConfigEntry(
            Component label,
            ConfigChangeTracker tracker,
            ForgeConfigSpec.ConfigValue<String> configValue, ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label, tracker, configValue, valueSpec);
    }

    @Nullable
    @Override
    protected String fromString(String text) {
        return text;
    }
}
