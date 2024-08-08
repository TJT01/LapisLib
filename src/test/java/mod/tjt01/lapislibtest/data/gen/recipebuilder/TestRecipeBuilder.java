package mod.tjt01.lapislibtest.data.gen.recipebuilder;

import com.google.gson.JsonObject;
import mod.tjt01.lapislib.crafting.ingredient.IngredientStack;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TestRecipeBuilder implements RecipeBuilder {
    @Nullable
    private String group;
    @Nullable
    private IngredientStack first;
    @Nullable
    private IngredientStack last;
    private final ItemStack result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public TestRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    public static TestRecipeBuilder testRecipe(ItemStack result) {
        return new TestRecipeBuilder(result.copy());
    }

    public static TestRecipeBuilder testRecipe(ItemLike result) {
        return new TestRecipeBuilder(new ItemStack(result));
    }

    public static TestRecipeBuilder testRecipe(ItemLike result, int count) {
        return new TestRecipeBuilder(new ItemStack(result, count));
    }

    public static TestRecipeBuilder testRecipe(ItemLike result, int count, @Nullable CompoundTag data) {
        return new TestRecipeBuilder(new ItemStack(result, count, data));
    }

    public TestRecipeBuilder first(IngredientStack ingredientStack) {
        this.first = ingredientStack;
        return this;
    }

    public TestRecipeBuilder first(Ingredient ingredient) {
        return this.first(new IngredientStack(ingredient, 1));
    }

    public TestRecipeBuilder first(Ingredient ingredient, int count) {
        return this.first(new IngredientStack(ingredient, count));
    }

    public TestRecipeBuilder first(ItemLike item) {
        return this.first(new IngredientStack(Ingredient.of(item), 1));
    }

    public TestRecipeBuilder first(ItemLike item, int count) {
        return this.first(new IngredientStack(Ingredient.of(item), count));
    }

    public TestRecipeBuilder first(TagKey<Item> item) {
        return this.first(new IngredientStack(Ingredient.of(item), 1));
    }

    public TestRecipeBuilder first(TagKey<Item> item, int count) {
        return this.first(new IngredientStack(Ingredient.of(item), count));
    }

    public TestRecipeBuilder last(IngredientStack ingredientStack) {
        this.last = ingredientStack;
        return this;
    }

    public TestRecipeBuilder last(Ingredient ingredient) {
        return this.last(new IngredientStack(ingredient, 1));
    }

    public TestRecipeBuilder last(Ingredient ingredient, int count) {
        return this.last(new IngredientStack(ingredient, count));
    }

    public TestRecipeBuilder last(ItemLike item) {
        return this.last(new IngredientStack(Ingredient.of(item), 1));
    }

    public TestRecipeBuilder last(ItemLike item, int count) {
        return this.last(new IngredientStack(Ingredient.of(item), count));
    }

    public TestRecipeBuilder last(TagKey<Item> item) {
        return this.last(new IngredientStack(Ingredient.of(item), 1));
    }

    public TestRecipeBuilder last(TagKey<Item> item, int count) {
        return this.last(new IngredientStack(Ingredient.of(item), count));
    }

    @Override
    public TestRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        advancement.addCriterion(criterionName, criterionTrigger);
        return this;
    }

    @Override
    public TestRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
        if (this.first == null) {
            throw new IllegalStateException("Missing first ingredient for " + recipeId);
        }
        if (this.last == null) {
            throw new IllegalStateException("Missing last ingredient for " + recipeId);
        }
        this.advancement
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);

        finishedRecipeConsumer.accept(
                new Result(
                        recipeId,
                        this.result, this.first, this.last,
                        this.group == null ? "" : group,
                        this.advancement,
                        new ResourceLocation(
                                recipeId.getNamespace(),
                                "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/"
                                        + recipeId.getPath()
                        )
                )
        );
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final IngredientStack first;
        private final IngredientStack last;
        private final String group;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancement_id;

        public Result(ResourceLocation id, ItemStack result, IngredientStack first, IngredientStack last, String group, Advancement.Builder advancement, ResourceLocation advancement_id) {
            this.id = id;
            this.result = result;
            this.first = first;
            this.last = last;
            this.group = group;
            this.advancement = advancement;
            this.advancement_id = advancement_id;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);

            json.add("left", this.first.toJson());
            json.add("right", this.last.toJson());

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
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return LapisLibTestRecipeSerializers.TEST.get();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancement_id;
        }
    }
}
