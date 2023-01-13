package mod.tjt01.lapislib.data.loot.modifier;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
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

public class RemoveItemModifier extends LootModifier {
    public final Ingredient remove;

    public RemoveItemModifier(LootItemCondition[] conditionsIn, Ingredient remove) {
        super(conditionsIn);
        this.remove = remove;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot.stream()
                .filter(stack -> !remove.test(stack))
                .collect(Collectors.toList());
    }

    public static class Serializer extends GlobalLootModifierSerializer<RemoveItemModifier> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public RemoveItemModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            Ingredient remove = CraftingHelper.getIngredient(object.get("remove"));
            return new RemoveItemModifier(ailootcondition, remove);
        }

        @Override
        public JsonObject write(RemoveItemModifier instance) {
            JsonObject json = this.makeConditions(instance.conditions);
            json.add("remove", instance.remove.toJson());
            return json;
        }
    }
}
