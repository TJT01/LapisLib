package mod.tjt01.lapislibtest;

import mod.tjt01.lapislib.core.config.LapisLibConfig;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import mod.tjt01.lapislibtest.config.LapisLibTestConfig;
import mod.tjt01.lapislibtest.item.LapisLibTestItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("lapislib_test")
public class LapisLibTest {
    public static final String MODID = "lapislib_test";

    public LapisLibTest() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        LapisLibTestBlocks.register(bus);
        LapisLibTestItems.register(bus);

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, LapisLibTestConfig.SERVER_SPEC);
    }
}
