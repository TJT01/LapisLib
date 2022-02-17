package mod.tjt01.lapislibtest.block;

import mod.tjt01.lapislib.block.OptionalBlock;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.item.LapisLibTestItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class LapisLibTestBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LapisLibTest.MODID);

    public static RegistryObject<Block> makeBlockWithoutItem(String name, Supplier<Block> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static RegistryObject<Block> makeBlock(String name, Supplier<Block> supplier, CreativeModeTab tab){
        RegistryObject<Block> blockRegistry = makeBlockWithoutItem(name, supplier);
        LapisLibTestItems.ITEMS.register(name, () -> new BlockItem(blockRegistry.get(), new Item.Properties().tab(tab)));
        return blockRegistry;
    }

    public static final RegistryObject<Block> OPTIONAL_BLOCK = makeBlock("optional_block", () -> new OptionalBlock(BlockBehaviour.Properties.of(Material.STONE), () -> false), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
