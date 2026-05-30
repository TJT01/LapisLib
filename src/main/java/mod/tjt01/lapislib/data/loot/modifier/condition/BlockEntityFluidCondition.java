package mod.tjt01.lapislib.data.loot.modifier.condition;

import mod.tjt01.lapislib.registry.loot.LapisLibLootConditions;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BlockEntityFluidCondition extends BlockEntityCapabilityCondition<IFluidHandler> {
    public BlockEntityFluidCondition(int min, int max) {
        super(ForgeCapabilities.FLUID_HANDLER, min, max);
    }

    public static BlockEntityCapabilityCondition.Builder fluid() {
        return new BlockEntityCapabilityCondition.Builder(BlockEntityFluidCondition::new);
    }

    @Override
    public boolean testCapability(IFluidHandler fluid) {
        int fluidAmount = 0;
        int tanks = fluid.getTanks();

        for (int i = 0; i < tanks; i++) {
            fluidAmount += fluid.getFluidInTank(i).getAmount();
            if (fluidAmount > max) return false;
        }

        return fluidAmount >= min;
    }

    @Override
    public LootItemConditionType getType() {
        return LapisLibLootConditions.BLOCK_ENTITY_FLUID.get();
    }
}
