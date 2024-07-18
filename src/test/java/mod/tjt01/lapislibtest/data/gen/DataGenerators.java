package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.data.gen.asset.BlockStates;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = LapisLibTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new TestRecipes(generator));
        generator.addProvider(event.includeServer(), new LootModifiers(generator));
        generator.addProvider(event.includeClient(), new Lang(generator));
        generator.addProvider(event.includeClient(), new BlockStates(generator, helper));
    }
}