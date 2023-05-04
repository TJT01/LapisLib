package mod.tjt01.lapislib.client.config.factory;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.component.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@FunctionalInterface
public interface ConfigEntryFactory {
    @Nonnull
    ConfigEntry make(
            ForgeConfigSpec.ConfigValue<?> configValue, ForgeConfigSpec.ValueSpec valueSpec,
            Screen parent, ConfigChangeTracker tracker
    );

    class DefaultConfigEntryFactory implements ConfigEntryFactory {
        public static final DefaultConfigEntryFactory INSTANCE = new DefaultConfigEntryFactory();

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public ConfigEntry make(
                ForgeConfigSpec.ConfigValue<?> configValue, ForgeConfigSpec.ValueSpec valueSpec,
                Screen parent, ConfigChangeTracker tracker
        ) {
            TranslatableComponent label = new TranslatableComponent(valueSpec.getTranslationKey());
            Object value = configValue.get();
            if (value instanceof Boolean) {
               return new BooleanConfigEntry(
                        label, tracker, (ForgeConfigSpec.ConfigValue<Boolean>) configValue, valueSpec
                );
            } else if (value instanceof Integer) {
                return new IntConfigEntry(
                        label, tracker, (ForgeConfigSpec.ConfigValue<Integer>) configValue, valueSpec
                );
            } else if (value instanceof Long) {
                return new LongConfigEntry(
                        label, tracker, (ForgeConfigSpec.ConfigValue<Long>) configValue, valueSpec
                );
            } else if (value instanceof Float) {
                return new FloatConfigEntry(
                        label, tracker, (ForgeConfigSpec.ConfigValue<Float>) configValue, valueSpec
                );
            } else if (value instanceof Double) {
                return new DoubleConfigEntry(
                        label, tracker, (ForgeConfigSpec.ConfigValue<Double>) configValue, valueSpec
                );
            } else if (value instanceof Enum<?>) {
                return new EnumEntry(label, tracker, (ForgeConfigSpec.ConfigValue<Enum<?>>)configValue, valueSpec);
            } else if (value instanceof String) {
                return new StringConfigEntry(
                        label, tracker, (ForgeConfigSpec.ConfigValue<String>) configValue, valueSpec
                );
            } else if (value instanceof List<?> list) {
                return ListConfigEntry.create(
                        label, tracker, (ForgeConfigSpec.ConfigValue<List<?>>) configValue, valueSpec, parent
                );
            } else {
                LapisLib.LOGGER.warn("No config entry for type {}", value.getClass());
                return new InvalidConfigEntry(value.getClass().toString(), String.join(".", configValue.getPath()));
            }
        }
    }
}
