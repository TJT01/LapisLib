package mod.tjt01.lapislibtest.item;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LapisLibTestItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LapisLibTest.MODID);

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
