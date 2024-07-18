package mod.tjt01.lapislib.client.config.factory;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.component.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.List;

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
            Component label;
            String translationKey = valueSpec.getTranslationKey();
            if (translationKey == null) {
                label = Component.literal(configValue.getPath().get(configValue.getPath().size() - 1));
            } else {
                label = Component.translatable(valueSpec.getTranslationKey());
            }
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
