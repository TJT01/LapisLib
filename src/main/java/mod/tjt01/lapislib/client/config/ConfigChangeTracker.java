package mod.tjt01.lapislib.client.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import mod.tjt01.lapislib.core.network.LapisLibPacketHandler;
import mod.tjt01.lapislib.core.network.SubmitServerConfigPacket;
import mod.tjt01.lapislib.util.ConfigUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConfigChangeTracker {
    protected final ModConfig config;
    public final Map<String, Object> changes = new HashMap<>();

    public ConfigChangeTracker(ModConfig config) {
        this.config = config;
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

    public CommentedConfig getCommentedConfig() {
        CommentedConfig modified = CommentedConfig.copy(config.getConfigData());
        for (Map.Entry<String, Object> entry: changes.entrySet()) {
            modified.set(entry.getKey(), entry.getValue());
        }
        return modified;
    }

    public void save() {
        if (this.changes.isEmpty()) return;
        ForgeConfigSpec spec = ConfigUtil.toForgeConfigSpec(config.getSpec());
        if (spec == null) {
            throw new IllegalStateException("Cannot retrieve config spec for " + config.getFileName());
        }
        CommentedConfig modified = CommentedConfig.copy(config.getConfigData());
        for (Map.Entry<String, Object> entry: changes.entrySet()) {
            modified.set(entry.getKey(), entry.getValue());
        }
        this.config.getConfigData().putAll(modified);

        spec.afterReload();
        try {
            Method m = ModConfig.class.getDeclaredMethod("fireEvent", IConfigEvent.class);
            m.setAccessible(true);
            m.invoke(config, new ModConfigEvent.Reloading(config));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.clearChanges();
    }
}
