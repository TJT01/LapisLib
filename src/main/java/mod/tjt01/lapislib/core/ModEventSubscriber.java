package mod.tjt01.lapislib.core;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.core.config.LapisLibConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LapisLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == LapisLibConfig.CLIENT_SPEC)
                LapisLibConfig.bakeClient(config);
    }
}
