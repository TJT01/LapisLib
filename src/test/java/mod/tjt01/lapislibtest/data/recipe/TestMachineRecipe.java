package mod.tjt01.lapislibtest.data.recipe;

import com.google.gson.JsonObject;
import mod.tjt01.lapislib.crafting.ingredient.IngredientStack;
import mod.tjt01.lapislib.crafting.ingredient.fluid.FluidIngredient;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import mod.tjt01.lapislibtest.block.entity.TestMachineBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

public class TestMachineRecipe implements Recipe<TestMachineBlockEntity.TestMachineRecipeWrapper> {
    public final ResourceLocation id;
    public final IngredientStack ingredient;
    public final FluidIngredient fluidIngredient;
    public final ItemStack output;
    public final int craftTime;
    public final String group;

    public TestMachineRecipe(
            ResourceLocation id,
            IngredientStack ingredient, FluidIngredient fluidIngredient,
            ItemStack output, int craftTime,
            String group
    ) {
        this.id = id;
        this.ingredient = ingredient;
        this.fluidIngredient = fluidIngredient;
        this.output = output;
        this.craftTime = craftTime;
        this.group = group;
    }

    public int getCraftTime() {
        return this.craftTime;
    }

    @Override
    public boolean matches(TestMachineBlockEntity.TestMachineRecipeWrapper container, Level level) {
        return fluidIngredient.test(container.getFluid()) && ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(TestMachineBlockEntity.TestMachineRecipeWrapper pContainer) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(TestMachineBlockEntity.TestMachineRecipeWrapper pContainer) {
        return Recipe.super.getRemainingItems(pContainer);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(ingredient.ingredient);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(LapisLibTestBlocks.MACHINE_BLOCK.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LapisLibTestRecipeSerializers.MACHINE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return LapisLibTestRecipeTypes.MACHINE.get();
    }

    @Override
    public boolean isIncomplete() {
        return fluidIngredient.isEmpty() && this.ingredient.isEmpty();
    }

    public static class Serializer implements RecipeSerializer<TestMachineRecipe> {
        @Override
        public TestMachineRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            IngredientStack ingredientStack = IngredientStack.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "item"));
            FluidIngredient fluidIngredient = FluidIngredient.fromJson(GsonHelper.getAsJsonObject(serializedRecipe, "fluid"));
            ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(serializedRecipe, "result"), true, true);
            int craftTime = GsonHelper.getAsInt(serializedRecipe, "time", 200);
            String group = GsonHelper.getAsString(serializedRecipe, "group", "");
            return new TestMachineRecipe(recipeId, ingredientStack, fluidIngredient, output, craftTime, group);
        }

        @Override
        @Nullable
        public TestMachineRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            IngredientStack ingredientStack = IngredientStack.fromNetwork(buffer);
            FluidIngredient fluidIngredient = FluidIngredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            int craftTime = buffer.readInt();
            String group = buffer.readUtf();
            return new TestMachineRecipe(recipeId, ingredientStack, fluidIngredient, output, craftTime, group);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TestMachineRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            FluidIngredient.toNetwork(recipe.fluidIngredient, buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.craftTime);
            buffer.writeUtf(recipe.group);
        }
    }
}
