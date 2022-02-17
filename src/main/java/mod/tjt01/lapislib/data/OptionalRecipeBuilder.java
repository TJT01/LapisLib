package mod.tjt01.lapislib.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OptionalRecipeBuilder {
    private final List<ICondition> conditions = new ArrayList<>();
    private FinishedRecipe recipe;

    private void setRecipe(FinishedRecipe recipe) {
        this.recipe = recipe;
    }

    public static OptionalRecipeBuilder optional(Consumer<Consumer<FinishedRecipe>> consumer) {
        OptionalRecipeBuilder recipeBuilder = new OptionalRecipeBuilder();
        consumer.accept(recipeBuilder::setRecipe);
        return recipeBuilder;
    }

    public OptionalRecipeBuilder addCondition(ICondition condition){
        this.conditions.add(condition);
        return this;
    }

    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        if (conditions.isEmpty())
            throw new IllegalStateException("No conditions were defined");
        finishedRecipeConsumer.accept(new Result(recipe, conditions));
    }


    private static class Result implements FinishedRecipe {
        private final FinishedRecipe recipe;
        private final List<ICondition> conditions;

        Result(FinishedRecipe finishedRecipe, List<ICondition> conditions) {
            this.recipe = finishedRecipe;
            this.conditions = conditions;
        }

        @Override
        @NotNull
        public JsonObject serializeRecipe() {
            JsonObject parentJson = recipe.serializeRecipe();
            this.serializeRecipeData(parentJson);
            return parentJson;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            JsonArray conditionJsonArray = new JsonArray();
            for (ICondition condition : conditions)
                conditionJsonArray.add(CraftingHelper.serialize(condition));
            json.add("conditions", conditionJsonArray);
        }

        @Override
        @NotNull
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        @NotNull
        public RecipeSerializer<?> getType() {
            return recipe.getType();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            ConditionalAdvancement.Builder adv = ConditionalAdvancement.builder()
            .addAdvancement(recipe);
            for (ICondition condition : conditions)
                adv.addCondition(condition);
            return adv.write();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return recipe.getAdvancementId();
        }
    }
}
