package mod.tjt01.lapislib.util;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigUtil {
    @SuppressWarnings("unchecked")
    @Nullable
    public static ModConfig getConfig(String modId, ModConfig.Type type) {
        try {
            return (
                    (ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>>)
                            FieldUtils.readField(ConfigTracker.INSTANCE, "configsByMod", true)
            ).getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null);
        } catch (Throwable ignored) {}
        return null;
    }

    @Nullable
    public static ForgeConfigSpec toForgeConfigSpec(UnmodifiableConfig spec) {
        if (spec instanceof ForgeConfigSpec forgeConfigSpec) return forgeConfigSpec;
        if (spec instanceof UnmodifiableConfigWrapper<?> wrapper) {
            try {
                return toForgeConfigSpec((UnmodifiableConfig) FieldUtils.readField(wrapper, "config", true));
            } catch (Throwable ignored) {}
        }
        return null;
    }
}
