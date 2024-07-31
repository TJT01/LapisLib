package mod.tjt01.lapislib.registry.loot;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.data.loot.modifier.condition.BlockTagCondition;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LapisLibLootConditions {
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, LapisLib.MODID);

    public static final RegistryObject<LootItemConditionType> BLOCK_TAG = CONDITIONS.register(
            "block_tag", () -> new LootItemConditionType(new BlockTagCondition.Serializer())
    );

    public static void register(IEventBus bus) {
        CONDITIONS.register(bus);
    }
}
