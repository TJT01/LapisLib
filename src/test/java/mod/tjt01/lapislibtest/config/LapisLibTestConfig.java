package mod.tjt01.lapislibtest.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class LapisLibTestConfig {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ServerConfig SERVER_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON_CONFIG;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPairServer = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPairServer.getLeft();
        SERVER_SPEC = specPairServer.getRight();

        final Pair<CommonConfig, ForgeConfigSpec> specPairCommon = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_CONFIG = specPairCommon.getLeft();
        COMMON_SPEC = specPairCommon.getRight();
    }

    public static boolean enableOptionalTestRecipes = true;

    public static void bakeServer(ModConfig config) {
        enableOptionalTestRecipes = SERVER_CONFIG.enableOptionalTestRecipes.get();

        UnmodifiableConfig values = COMMON_SPEC.getValues();
    }
}
