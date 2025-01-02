package mod.tjt01.lapislibtest.block;

import mod.tjt01.lapislib.block.WaterloggableBlock;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.item.LapisLibTestItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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

    public static RegistryObject<Block> makeBlock(String name, Supplier<Block> supplier){
        RegistryObject<Block> blockRegistry = makeBlockWithoutItem(name, supplier);
        LapisLibTestItems.ITEMS.register(name, () -> new BlockItem(blockRegistry.get(), new Item.Properties()));
        return blockRegistry;
    }

    public static final RegistryObject<Block> CRAFTING_BLOCK = makeBlock(
            "test_crafting_block",
            () -> new TestCraftingBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM))
    );

    public static final RegistryObject<Block> MACHINE_BLOCK = makeBlock(
            "test_machine",
            () -> new TestMachineBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM))
    );

    public static final RegistryObject<Block> WATERLOGGABLE_BLOCK = makeBlock(
            "waterloggable_block",
            () -> new WaterloggableBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM))
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
