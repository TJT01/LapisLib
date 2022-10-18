package mod.tjt01.lapislib.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class LapisLibConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    static final ClientConfig CLIENT_CONFIG;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPairClient = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_CONFIG = specPairClient.getLeft();
        CLIENT_SPEC = specPairClient.getRight();
    }

    public static boolean showItemTags = true;

    public static void bakeClient(ModConfig config) {
        showItemTags = CLIENT_CONFIG.showItemTags.get();
    }
}
