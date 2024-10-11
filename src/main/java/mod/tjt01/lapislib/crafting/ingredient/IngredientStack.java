package mod.tjt01.lapislib.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class IngredientStack implements Predicate<ItemStack> {
    public final Ingredient ingredient;
    public final int count;

    public IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public IngredientStack(ItemLike item, int count) {
        this(Ingredient.of(item), count);
    }

    public IngredientStack(int count, ItemLike... items) {
        this(Ingredient.of(items), count);
    }

    public IngredientStack(TagKey<Item> tag, int count) {
        this(Ingredient.of(tag), count);
    }

    public static IngredientStack fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonArray()) {
                return new IngredientStack(Ingredient.fromJson(json), 1);
            } else if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(
                        object.has("ingredients") && object.get("ingredients").isJsonArray()
                                ? object.get("ingredients")
                                : object
                );
                int count = GsonHelper.getAsInt(object, "count", 1);
                if (count < 1 || count > 64) {
                    throw new JsonSyntaxException("Count must be between 1 and 64");
                }
                return new IngredientStack(ingredient, count);
            }
        }
        throw new JsonSyntaxException("Item cannot be null");
    }

    public static IngredientStack fromNetwork(FriendlyByteBuf buffer) {
        int count = buffer.readByte();
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        return new IngredientStack(ingredient, count);
    }

    public boolean isEmpty() {
        return ForgeHooks.hasNoElements(this.ingredient);
    }

    public JsonElement toJson() {
        JsonElement ingredientJson = ingredient.toJson();
        if (count == 1) {
            return ingredientJson;
        }

        JsonObject object;
        if (ingredientJson.isJsonObject()) {
            object = ingredientJson.getAsJsonObject();
            if (object.has("count")) throw new JsonSyntaxException("Ingredient already defines \"count\" member");
        } else if (ingredientJson.isJsonArray()) {
            object = new JsonObject();
            object.add("ingredients", ingredientJson.getAsJsonArray());
        } else {
            throw new IllegalStateException();
        }

        object.addProperty("count", this.count);
        return object;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeByte(count);
        CraftingHelper.write(buffer, ingredient);
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getCount() >= count && ingredient.test(stack);
    }
}
