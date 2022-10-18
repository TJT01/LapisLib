package mod.tjt01.lapislib.core.config;

import mod.tjt01.lapislib.LapisLib;
import net.minecraftforge.common.ForgeConfigSpec;

final class ClientConfig {
    final ForgeConfigSpec.BooleanValue showItemTags;

    public ClientConfig(final ForgeConfigSpec.Builder builder) {
        showItemTags = builder
                .comment("If an item's tags should be shown when advanced tooltips(F3+H) are enabled.")
                .translation("config." + LapisLib.MODID + ".showItemTags")
                .define("showItemTags", true);
    }
}
