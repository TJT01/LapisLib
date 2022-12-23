package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislib.data.OptionalRecipeBuilder;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.data.gen.condition.TestCondition;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class TestRecipes extends RecipeProvider {
    public TestRecipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> finishedRecipeConsumer) {
        OptionalRecipeBuilder.optional(recipeConsumer -> {
            ShapedRecipeBuilder.shaped(Items.DIAMOND)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', Tags.Items.STORAGE_BLOCKS_COAL)
                    .unlockedBy("has_coal_block", has(Tags.Items.STORAGE_BLOCKS_COAL))
                    .save(recipeConsumer, new ResourceLocation(LapisLibTest.MODID, "coal_to_diamond"));
        })
                .addCondition(new TestCondition())
                .save(finishedRecipeConsumer);
    }
}
