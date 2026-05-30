package mod.tjt01.lapislib.data.loot.modifier.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.BiFunction;

public abstract class BlockEntityCapabilityCondition<T> implements LootItemCondition {
    protected final Capability<T> capability;
    protected final int min;
    protected final int max;

    protected BlockEntityCapabilityCondition(Capability<T> capability, int min, int max) {
        this.capability = capability;
        this.min = min;
        this.max = max;
    }

    public abstract boolean testCapability(T t);

    @Override
    public boolean test(LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParam(LootContextParams.BLOCK_ENTITY);

        LazyOptional<T> lazy = blockEntity.getCapability(capability);

        return lazy.isPresent() && testCapability(lazy.orElseThrow(IllegalStateException::new));
    }

    public static final class Builder implements LootItemCondition.Builder {
        private final BiFunction<Integer, Integer, LootItemCondition> factory;
        private int min = 0;
        private int max = Integer.MAX_VALUE;

        public Builder(BiFunction<Integer, Integer, LootItemCondition> factory) {
            this.factory = factory;
        }

        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        @Override
        public LootItemCondition build() {
            return factory.apply(min, max);
        }
    }

    public static final class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlockEntityCapabilityCondition<?>> {
        private final BiFunction<Integer, Integer, ? extends BlockEntityCapabilityCondition<?>> factory;

        public Serializer(BiFunction<Integer, Integer, ? extends BlockEntityCapabilityCondition<?>> factory) {
            this.factory = factory;
        }

        @Override
        public void serialize(JsonObject json, BlockEntityCapabilityCondition<?> value, JsonSerializationContext serializationContext) {
            if (value.min > 0) json.addProperty("min", value.min);
            if (value.max < Integer.MAX_VALUE) json.addProperty("max", value.max);
        }

        @Override
        public BlockEntityCapabilityCondition<?> deserialize(JsonObject json, JsonDeserializationContext context) {
            return factory.apply(GsonHelper.getAsInt(json, "min", 0), GsonHelper.getAsInt(json, "max", Integer.MAX_VALUE));
        }
    }

}
