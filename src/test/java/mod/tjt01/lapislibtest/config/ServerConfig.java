package mod.tjt01.lapislibtest.config;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    final ForgeConfigSpec.BooleanValue enableOptionalTestRecipes;

    public ServerConfig(final ForgeConfigSpec.Builder builder) {
        enableOptionalTestRecipes = builder
                .comment("Enable some test recipes")
                .translation("config." + LapisLibTest.MODID + ".testRecipes")
                .define("testRecipes", true);
    }
}
