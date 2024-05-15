package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.data.gen.asset.BlockStates;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = LapisLibTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(new TestRecipes(generator));
        generator.addProvider(new LootModifiers(generator));
        generator.addProvider(new Lang(generator));
        generator.addProvider(new BlockStates(generator, helper));
    }
}