package mod.tjt01.lapislibtest;

import mod.tjt01.lapislib.client.config.screen.RootConfigScreen;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import mod.tjt01.lapislibtest.block.entity.LapisLibTestBlockEntityTypes;
import mod.tjt01.lapislibtest.config.LapisLibTestConfig;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeSerializers;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeTypes;
import mod.tjt01.lapislibtest.item.LapisLibTestItems;
import mod.tjt01.lapislibtest.menu.LapisLibTestMenus;
import mod.tjt01.lapislibtest.network.TestNetwork;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lapislib_test")
public class LapisLibTest {
    public static final String MODID = "lapislib_test";
    public static final Logger LOGGER = LogManager.getLogger("LapisLibTest");

    public LapisLibTest() {
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        LapisLibTestBlocks.register(bus);
        LapisLibTestItems.register(bus);
        LapisLibTestBlockEntityTypes.register(bus);
        LapisLibTestMenus.register(bus);
        LapisLibTestRecipeTypes.register(bus);
        LapisLibTestRecipeSerializers.register(bus);

        TestNetwork.register();

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, LapisLibTestConfig.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, LapisLibTestConfig.COMMON_SPEC);
        //Note: It seems like Forge does not properly support having multiple configs of the same type
        //modLoadingContext.registerConfig(ModConfig.Type.COMMON, LapisLibTestConfig.SECONDARY_SPEC, "lapislib_test_secondary.toml");

        modLoadingContext.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                RootConfigScreen.builder(MODID)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorInt, false)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorAlphaInt, true)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorString, false)
                        .defineColor(LapisLibTestConfig.COMMON_CONFIG.colorAlphaString, true)
                        .getFactory());

    }
}
