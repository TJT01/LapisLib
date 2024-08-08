package mod.tjt01.lapislibtest.data.recipe;

import com.google.gson.JsonObject;
import mod.tjt01.lapislib.crafting.ingredient.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestRecipe implements Recipe<SimpleContainer> {
    private final IngredientStack left;
    private final IngredientStack right;
    private final ItemStack out;
    private final String group;
    public final ResourceLocation id;

    public TestRecipe(ResourceLocation id, String group, IngredientStack left, IngredientStack right, ItemStack out) {
        this.id = id;
        this.group = group;
        this.left = left;
        this.right = right;
        this.out = out;
    }

    private void removeItem(Player player, SimpleContainer container, int slot, ItemStack remainder, IngredientStack stack) {
        ItemStack current = container.getItem(slot);
        if (!current.isEmpty()) {
            container.removeItem(slot, stack.count);
            current = container.getItem(slot);
        }

        if (!remainder.isEmpty()) {
            ItemStack toAdd = remainder.copy();
            toAdd.setCount(stack.count);
            if (current.isEmpty()) {
                container.setItem(slot, toAdd);
            } else if (ItemStack.isSame(current, toAdd) && ItemStack.tagMatches(current, toAdd)) {
                toAdd.grow(current.getCount());
                container.setItem(slot, toAdd);
            } else if (!player.getInventory().add(toAdd)) {
                player.drop(toAdd, false);
            }
        }
    }

    public void removeItems(Player player, SimpleContainer container) {
        NonNullList<ItemStack> remainders = this.getRemainingItems(container);

        removeItem(player, container, 0, remainders.get(0), left);
        removeItem(player, container, 1, remainders.get(1), right);
    }

    @Nonnull
    @Override
    public String getGroup() {
        return Recipe.super.getGroup();
    }

    @Override
    public boolean matches(@Nonnull SimpleContainer container, @Nonnull Level pLevel) {
        return left.test(container.getItem(0)) && right.test(container.getItem(1));
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull SimpleContainer pContainer) {
        return out.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return out;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nonnull
    @Override
    public ItemStack getToastSymbol() {
        //TODO implement
        return Recipe.super.getToastSymbol();
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return LapisLibTestRecipeSerializers.TEST.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return LapisLibTestRecipeTypes.TEST.get();
    }

    public static class Serializer implements RecipeSerializer<TestRecipe> {
        @Override
        public TestRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            String group = GsonHelper.getAsString(serializedRecipe, "group", "");
            IngredientStack left = IngredientStack.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "left"));
            IngredientStack right = IngredientStack.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "right"));
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(serializedRecipe, "result"), true, true);
            return new TestRecipe(recipeId, group, left, right, result);
        }

        @Nullable
        @Override
        public TestRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new TestRecipe(recipeId, buffer.readUtf(), IngredientStack.fromNetwork(buffer), IngredientStack.fromNetwork(buffer), buffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TestRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.left.toNetwork(buffer);
            recipe.right.toNetwork(buffer);
            buffer.writeItem(recipe.out);
        }
    }
}
