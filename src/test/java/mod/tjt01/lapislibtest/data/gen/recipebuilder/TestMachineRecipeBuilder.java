package mod.tjt01.lapislibtest.data.gen.recipebuilder;

import com.google.gson.JsonObject;
import mod.tjt01.lapislib.crafting.ingredient.IngredientStack;
import mod.tjt01.lapislib.crafting.ingredient.fluid.FluidIngredient;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeSerializers;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TestMachineRecipeBuilder implements RecipeBuilder {
    @Nullable
    private String group;
    private final IngredientStack itemIngredient;
    private final FluidIngredient fluidIngredient;
    private final int time;
    private final ItemStack result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public static TestMachineRecipeBuilder machine(IngredientStack itemIngredient, FluidIngredient fluidIngredient, int time, ItemStack result) {
        return new TestMachineRecipeBuilder(itemIngredient, fluidIngredient, time, result);
    }

    public static TestMachineRecipeBuilder machine(Ingredient ingredient, FluidIngredient fluid, int time, ItemStack result) {
        return new TestMachineRecipeBuilder(new IngredientStack(ingredient, 1), fluid, time, result);
    }

    private TestMachineRecipeBuilder(IngredientStack itemIngredient, FluidIngredient fluidIngredient, int time, ItemStack result) {
        this.itemIngredient = itemIngredient;
        this.fluidIngredient = fluidIngredient;
        this.time = time;
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
        this.advancement
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);

        finishedRecipeConsumer.accept(new Result(
                recipeId,
                result,
                this.itemIngredient, this.fluidIngredient, this.time,
                this.group == null ? "" : this.group,
                advancement,
                new ResourceLocation(
                        recipeId.getNamespace(),
                        "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/"
                        + recipeId.getPath()
                )
        ));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final IngredientStack itemIngredient;
        private final FluidIngredient fluidIngredient;
        private final int time;
        private final String group;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ItemStack result, IngredientStack itemIngredient, FluidIngredient fluidIngredient, int time, String group, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.itemIngredient = itemIngredient;
            this.fluidIngredient = fluidIngredient;
            this.time = time;
            this.group = group;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);

            json.add("item", this.itemIngredient.toJson());
            json.add("fluid", FluidIngredient.toJson(fluidIngredient));
            //TODO make this a utility method?
            JsonObject resultJson = new JsonObject();

            CompoundTag nbt = result.serializeNBT();

            resultJson.addProperty("item", nbt.getString("id"));
            if (this.result.getCount() > 1) resultJson.addProperty("count", nbt.getByte("Count"));
            if (nbt.contains("tag") || nbt.contains("ForgeCaps")) {
                CompoundTag tag = nbt.getCompound("tag").copy();
                CompoundTag caps = nbt.getCompound("ForgeCaps");

                if (!caps.isEmpty()) tag.put("ForgeCaps", caps.copy());
                if (!tag.isEmpty()) json.addProperty("nbt", tag.getAsString());
            }

            json.add("result", resultJson);

            json.addProperty("time", this.time);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return LapisLibTestRecipeSerializers.MACHINE.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
