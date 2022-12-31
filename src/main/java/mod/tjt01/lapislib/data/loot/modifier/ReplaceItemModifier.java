package mod.tjt01.lapislib.data.loot.modifier;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class ReplaceItemModifier extends LootModifier {
    public final Ingredient target;
    public final Item replacement;

    public ReplaceItemModifier(LootItemCondition[] conditionsIn, Ingredient target, Item replacement) {
        super(conditionsIn);
        this.target = target;
        this.replacement = replacement;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot.stream()
                .map(stack -> {
                    if(target.test(stack)) {
                        return new ItemStack(replacement, stack.getCount(), stack.getTag());
                    }
                    return stack;
                })
                .collect(Collectors.toList());
    }

    public static class Serializer extends GlobalLootModifierSerializer<ReplaceItemModifier> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ReplaceItemModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            Ingredient target = CraftingHelper.getIngredient(object.get("target"));
            Item replacement = GsonHelper.getAsItem(object, "replacement");
            return new ReplaceItemModifier(ailootcondition, target, replacement);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public JsonObject write(ReplaceItemModifier instance) {
            JsonObject json = this.makeConditions(instance.conditions);
            json.add("target", instance.target.toJson());
            json.addProperty("replacement", instance.replacement.getRegistryName().toString());
            return json;
        }
    }
}
