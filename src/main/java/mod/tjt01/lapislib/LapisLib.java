package mod.tjt01.lapislib;

import mod.tjt01.lapislib.core.config.LapisLibConfig;
import mod.tjt01.lapislib.registry.loot.GlobalLootModifiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("lapislib")
public class LapisLib {
    public static final String MODID = "lapislib";

    public LapisLib() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, LapisLibConfig.CLIENT_SPEC);

        GlobalLootModifiers.register(bus);
    }
}
