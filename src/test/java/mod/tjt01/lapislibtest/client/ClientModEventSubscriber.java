package mod.tjt01.lapislibtest.client;

import mod.tjt01.lapislibtest.client.screen.TestCraftingScreen;
import mod.tjt01.lapislibtest.client.screen.TestMachineScreen;
import mod.tjt01.lapislibtest.menu.LapisLibTestMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventSubscriber {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(LapisLibTestMenus.CRAFTING_MENU.get(), TestCraftingScreen::new);
        MenuScreens.register(LapisLibTestMenus.MACHINE_MENU.get(), TestMachineScreen::new);
    }
}
