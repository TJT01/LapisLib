package mod.tjt01.lapislib;

import mod.tjt01.lapislib.client.config.screen.RootConfigScreen;
import mod.tjt01.lapislib.core.config.LapisLibConfig;
import mod.tjt01.lapislib.core.network.LapisLibPacketHandler;
import mod.tjt01.lapislib.registry.loot.GlobalLootModifiers;
import mod.tjt01.lapislib.registry.loot.LapisLibLootConditions;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("lapislib")
public class LapisLib {
    public static final String MODID = "lapislib";
    public static final Logger LOGGER = LogManager.getLogger("LapisLib");

    public LapisLib() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, LapisLibConfig.CLIENT_SPEC);

        LapisLibPacketHandler.register();

        GlobalLootModifiers.register(bus);
        LapisLibLootConditions.register(bus);

        modLoadingContext.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                RootConfigScreen.builder(MODID).getFactory()
        );
    }
}
