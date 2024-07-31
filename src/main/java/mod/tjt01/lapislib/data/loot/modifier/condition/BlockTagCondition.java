package mod.tjt01.lapislib.data.loot.modifier.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import mod.tjt01.lapislib.registry.loot.LapisLibLootConditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class BlockTagCondition implements LootItemCondition {
    public final TagKey<Block> blockTag;

    BlockTagCondition(TagKey<Block> blockTag) {
        this.blockTag = blockTag;
    }

    public static Builder hasTag(TagKey<Block> blockTag) {
        return new Builder(blockTag);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockState state = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        return state != null && state.is(blockTag);
    }

    @Override
    public LootItemConditionType getType() {
        return LapisLibLootConditions.BLOCK_TAG.get();
    }

    public static final class Builder implements LootItemCondition.Builder {
        private final TagKey<Block> blockTag;

        public Builder(TagKey<Block> blockTag) {
            this.blockTag = blockTag;
        }

        @Override
        public LootItemCondition build() {
            return new BlockTagCondition(blockTag);
        }
    }

    public static final class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlockTagCondition> {

        @Override
        public void serialize(JsonObject json, BlockTagCondition condition, JsonSerializationContext context) {
            json.addProperty("tag", condition.blockTag.location().toString());
        }

        @Override
        public BlockTagCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation tag = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            return new BlockTagCondition(TagKey.create(Registry.BLOCK_REGISTRY, tag));
        }
    }
}
