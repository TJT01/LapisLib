package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislib.crafting.ingredient.IngredientStack;
import mod.tjt01.lapislib.crafting.ingredient.fluid.FluidIngredient;
import mod.tjt01.lapislib.crafting.ingredient.fluid.FluidStackIngredient;
import mod.tjt01.lapislib.crafting.ingredient.fluid.FluidTagIngredient;
import mod.tjt01.lapislib.data.OptionalRecipeBuilder;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.data.gen.condition.TestCondition;
import mod.tjt01.lapislibtest.data.gen.recipebuilder.TestMachineRecipeBuilder;
import mod.tjt01.lapislibtest.data.gen.recipebuilder.TestRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

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

        TestRecipeBuilder.testRecipe(Items.BONE)
                .first(Items.STICK, 6)
                .last(Tags.Items.STONE, 3)
                .unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
                .unlockedBy("has_stone", has(Tags.Items.STONE))
                .save(
                        finishedRecipeConsumer,
                        new ResourceLocation(LapisLibTest.MODID, "bone_from_sticks_and_stone")
                );

        TestMachineRecipeBuilder.machine(
                new IngredientStack(Items.COBBLED_DEEPSLATE, 4),
                new FluidTagIngredient(FluidTags.LAVA, 1000),
                20*30,
                new ItemStack(Items.MAGMA_BLOCK)
        )
                .unlockedBy("has_cobbled_deepslate", has(Items.COBBLED_DEEPSLATE))
                .save(
                        finishedRecipeConsumer,
                        new ResourceLocation(LapisLibTest.MODID, "magma_block")
                );

        TestMachineRecipeBuilder.machine(
                Ingredient.of(ItemTags.WOOL),
                new FluidStackIngredient(new FluidStack(Fluids.WATER, 250)),
                10,
                new ItemStack(Items.WHITE_WOOL)
        )
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(
                        finishedRecipeConsumer,
                        new ResourceLocation(LapisLibTest.MODID, "wool_washing")
                );
    }
}
