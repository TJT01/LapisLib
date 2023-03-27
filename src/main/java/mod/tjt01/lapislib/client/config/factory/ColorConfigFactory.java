package mod.tjt01.lapislib.client.config.factory;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.component.ColorConfigEntry;
import mod.tjt01.lapislib.client.config.component.ConfigEntry;
import mod.tjt01.lapislib.client.config.component.StringConfigEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

public class ColorConfigFactory implements ConfigEntryFactory {
    private final Boolean alpha;

    public ColorConfigFactory(boolean hasAlpha) {
        this.alpha = hasAlpha;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ConfigEntry make(ForgeConfigSpec.ConfigValue<?> configValue, ForgeConfigSpec.ValueSpec valueSpec, Screen parent, ConfigChangeTracker tracker) {
        Object value = configValue.get();
        TranslatableComponent label = new TranslatableComponent(valueSpec.getTranslationKey());

        if (value instanceof String) {
            return new ColorConfigEntry.StringColorConfigEntry(
                    label, parent, tracker, (ForgeConfigSpec.ConfigValue<String>) configValue, valueSpec, alpha
            );
        } else if (value instanceof Integer) {
            return new ColorConfigEntry.IntColorConfigEntry(
                    label, parent, tracker, (ForgeConfigSpec.ConfigValue<Integer>) configValue, valueSpec, alpha
            );
        } else {
            throw new IllegalArgumentException();
        }
    }
}
