package mod.tjt01.lapislibtest.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class LapisLibTestConfig {
    public static final ForgeConfigSpec SERVER_SPEC;
    static final ServerConfig SERVER_CONFIG;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPairServer = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPairServer.getLeft();
        SERVER_SPEC = specPairServer.getRight();
    }

    public static boolean enableOptionalTestRecipes = true;

    public static void bakeServer(ModConfig config) {
        enableOptionalTestRecipes = SERVER_CONFIG.enableOptionalTestRecipes.get();
    }
}
