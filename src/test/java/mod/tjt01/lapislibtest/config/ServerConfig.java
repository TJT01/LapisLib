package mod.tjt01.lapislibtest.config;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    @SuppressWarnings("unused")
    public enum TestServerEnum {
        AAA, BBB, CCC
    }

    public final ForgeConfigSpec.BooleanValue enableOptionalTestRecipes;

    public ServerConfig(final ForgeConfigSpec.Builder builder) {
        enableOptionalTestRecipes = builder
                .comment("Enable some test recipes")
                .translation("config." + LapisLibTest.MODID + ".testRecipes")
                .define("testRecipes", true);

        builder
                .comment(
                        "Test server-side enum value",
                        "Has no effect in-game"
                )
                .translation("config." + LapisLibTest.MODID + ".testServerEnum")
                .defineEnum("testServerEnum", TestServerEnum.BBB);
    }
}
