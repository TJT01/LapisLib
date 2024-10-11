package mod.tjt01.lapislibtest.block.entity;

import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LapisLibTestBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LapisLibTest.MODID);

    public static final RegistryObject<BlockEntityType<TestMachineBlockEntity>> MACHINE = BLOCK_ENTITIES.register(
            "machine",
            () -> BlockEntityType.Builder.of(TestMachineBlockEntity::new, LapisLibTestBlocks.MACHINE_BLOCK.get())
                    .build(null)
    );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
