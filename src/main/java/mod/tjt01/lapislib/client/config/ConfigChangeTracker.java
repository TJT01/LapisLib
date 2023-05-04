package mod.tjt01.lapislib.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ConfigChangeTracker {
    private final ForgeConfigSpec spec;
    private final Map<String, Object> changes = new HashMap<>();

    public ConfigChangeTracker(ForgeConfigSpec spec) {
        this.spec = spec;
    }

    public <T> void setValue(String path, ForgeConfigSpec.ConfigValue<T> configValue, T value) {
        if (configValue.get().equals(value)) {
            changes.remove(path);
        } else {
            changes.put(path, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String path, ForgeConfigSpec.ConfigValue<T> configValue) {
        return (T) (hasValue(path) ? changes.get(path) : configValue.get());
    }

    public boolean hasValue(String path) {
        return changes.containsKey(path);
    }

    public void clearChanges() {
        this.changes.clear();
    }

    public void save() {
        for (Map.Entry<String, Object> entry: changes.entrySet()) {
            ForgeConfigSpec.ValueSpec valueSpec = spec.get(entry.getKey());
            ForgeConfigSpec.ConfigValue<Object> configValue = spec.getValues().get(entry.getKey());
            if (valueSpec.test(entry.getValue())) configValue.set(entry.getValue());
        }

        this.clearChanges();
    }
}
