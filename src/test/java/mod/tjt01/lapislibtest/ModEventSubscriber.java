package mod.tjt01.lapislibtest;

import mod.tjt01.lapislibtest.config.LapisLibTestConfig;
import mod.tjt01.lapislibtest.data.gen.condition.TestCondition;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LapisLibTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CraftingHelper.register(TestCondition.Serializer.INSTANCE);
    }

    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent event) {
        ModConfig modConfig = event.getConfig();
        if (modConfig.getSpec() == LapisLibTestConfig.SERVER_SPEC)
            LapisLibTestConfig.bakeServer(modConfig);
    }
}
