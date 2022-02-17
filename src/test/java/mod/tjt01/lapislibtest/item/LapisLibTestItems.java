package mod.tjt01.lapislibtest.item;

import mod.tjt01.lapislib.item.OptionalItem;
import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LapisLibTestItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LapisLibTest.MODID);

    public static final RegistryObject<OptionalItem> OPTIONAL_ITEM = ITEMS.register("optional_item", () -> new OptionalItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), () -> false));

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
