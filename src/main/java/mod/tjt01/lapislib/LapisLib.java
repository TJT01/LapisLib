package mod.tjt01.lapislib;

import mod.tjt01.lapislib.core.config.LapisLibConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("lapislib")
public class LapisLib {
    public static final String MODID = "lapislib";

    public LapisLib() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, LapisLibConfig.CLIENT_SPEC);
    }
}
