package mod.tjt01.lapislibtest;

import mod.tjt01.lapislib.client.config.component.LabeledConfigEntry;
import mod.tjt01.lapislib.client.config.screen.RootConfigScreen;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import mod.tjt01.lapislibtest.config.CommonConfig;
import mod.tjt01.lapislibtest.config.LapisLibTestConfig;
import mod.tjt01.lapislibtest.item.LapisLibTestItems;
import net.minecraftforge.client.ConfigGuiHandler;
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
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, LapisLibTestConfig.COMMON_SPEC);

        modLoadingContext.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
                RootConfigScreen.builder(MODID)
                        .common(LapisLibTestConfig.COMMON_SPEC)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorInt, false)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorAlphaInt, true)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorString, false)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorAlphaString, true)
                        .getFactory());
    }
}
