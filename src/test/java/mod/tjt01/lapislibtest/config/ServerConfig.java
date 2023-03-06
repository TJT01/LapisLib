package mod.tjt01.lapislibtest.config;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public final ForgeConfigSpec.BooleanValue enableOptionalTestRecipes;

    private static String getTranslation(String path) {
        return "config." + LapisLibTest.MODID + "." + path;
    }

    public ServerConfig(final ForgeConfigSpec.Builder builder) {
        enableOptionalTestRecipes = builder
                .comment("Enable some test recipes")
                .translation("config." + LapisLibTest.MODID + ".testRecipes")
                .define("testRecipes", true);
    }
}
