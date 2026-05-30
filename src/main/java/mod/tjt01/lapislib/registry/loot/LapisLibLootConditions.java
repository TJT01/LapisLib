package mod.tjt01.lapislib.registry.loot;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.data.loot.modifier.condition.BlockEntityCapabilityCondition;
import mod.tjt01.lapislib.data.loot.modifier.condition.BlockEntityEnergyCondition;
import mod.tjt01.lapislib.data.loot.modifier.condition.BlockEntityFluidCondition;
import mod.tjt01.lapislib.data.loot.modifier.condition.BlockTagCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

public class LapisLibLootConditions {
    @ApiStatus.Internal
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, LapisLib.MODID);

    public static final RegistryObject<LootItemConditionType> BLOCK_TAG = CONDITIONS.register(
            "block_tag", () -> new LootItemConditionType(new BlockTagCondition.Serializer())
    );

    public static final RegistryObject<LootItemConditionType> BLOCK_ENTITY_ENERGY = CONDITIONS.register(
            "block_entity_energy", () -> new LootItemConditionType(new BlockEntityCapabilityCondition.Serializer(BlockEntityEnergyCondition::new))
    );

    public static final RegistryObject<LootItemConditionType> BLOCK_ENTITY_FLUID = CONDITIONS.register(
            "block_entity_fluid", () -> new LootItemConditionType(new BlockEntityCapabilityCondition.Serializer(BlockEntityFluidCondition::new))
    );

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        CONDITIONS.register(bus);
    }
}
