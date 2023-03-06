package mod.tjt01.lapislibtest;

import mod.tjt01.lapislib.client.config.screen.RootConfigScreen;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
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

//        modLoadingContext.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> {
//            return new ConfigGuiHandler.ConfigGuiFactory((minecraft, parent) -> {
//                return new RootConfigScreen(
//                        CommonComponents.GUI_YES, parent,
//                        screen -> new ConfigScreen(
//                                CommonComponents.GUI_YES, screen, (configEntryConsumer, myParent) -> {
//                                    configEntryConsumer.accept(
//                                            new CategoryEntry(
//                                                    myParent, CommonComponents.GUI_DONE,
//                                                    parent2 -> {
//                                                        return new ConfigScreen(
//                                                                CommonComponents.CONNECT_FAILED, parent2,
//                                                                (configEntryConsumer1, screen1) -> {
//
//                                                                }
//                                                        );
//                                                    }
//                                            )
//                                    );
//                                    configEntryConsumer.accept(
//                                            new CategoryEntry(
//                                                    myParent, CommonComponents.GUI_PROCEED,
//                                                    parent2 -> {
//                                                        return new ConfigScreen(
//                                                                CommonComponents.OPTION_OFF, parent2,
//                                                                (configEntryConsumer1, screen1) -> {}
//                                                        );
//                                                    }
//                                            )
//                                    );
//                                }
//                        ),
//                        null
//                );
//            });
//        });
//        new StringConfigNode(LapisLibTestConfig.SERVER_CONFIG.testStringValue);
        modLoadingContext.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
                RootConfigScreen.builder(MODID)
                .common(LapisLibTestConfig.COMMON_SPEC)
                .getFactory());
    }
}
