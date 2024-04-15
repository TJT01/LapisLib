package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public abstract class AbstractNumberConfigEntry<T extends Number> extends AbstractTextConfigEntry<T> {
    protected T min;
    protected T max;

    @SuppressWarnings("unchecked")
    public AbstractNumberConfigEntry(Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<T> configValue, ForgeConfigSpec.ValueSpec valueSpec) {
        super(label, tracker, configValue, valueSpec);
        Object range = valueSpec.getRange();
        min = getTypeMin();
        max = getTypeMax();
        try {
            Field minField = range.getClass().getDeclaredField("min");
            Field maxField = range.getClass().getDeclaredField("max");
            minField.setAccessible(true);
            maxField.setAccessible(true);
            min = (T) minField.get(range);
            max = (T) maxField.get(range);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException | NullPointerException ignored) {
            // NOOP
        }
    }

    protected abstract T getTypeMin();
    protected abstract T getTypeMax();
    protected abstract T parse(String text) throws NumberFormatException;

    @Nullable
    @Override
    protected T fromString(String text) {
        try {
            return parse(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean isValid() {
        final T num = this.tracker.getValue(this.path, this.configValue);

        return valueSpec.test(num);
    }
}
