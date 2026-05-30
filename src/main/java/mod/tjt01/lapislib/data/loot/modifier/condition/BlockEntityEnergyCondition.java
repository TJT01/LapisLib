package mod.tjt01.lapislib.data.loot.modifier.condition;

import com.google.common.collect.ImmutableSet;
import mod.tjt01.lapislib.registry.loot.LapisLibLootConditions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Set;

public class BlockEntityEnergyCondition extends BlockEntityCapabilityCondition<IEnergyStorage> {
    public BlockEntityEnergyCondition(int min, int max) {
        super(ForgeCapabilities.ENERGY, min, max);
    }

    public static BlockEntityCapabilityCondition.Builder energy() {
        return new BlockEntityCapabilityCondition.Builder(BlockEntityEnergyCondition::new);
    }

    @Override
    public LootItemConditionType getType() {
        return LapisLibLootConditions.BLOCK_ENTITY_ENERGY.get();
    }

    @Override
    public boolean testCapability(IEnergyStorage energy) {
        int stored = energy.getEnergyStored();
        return stored >= min && stored <= max;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
    }
}
