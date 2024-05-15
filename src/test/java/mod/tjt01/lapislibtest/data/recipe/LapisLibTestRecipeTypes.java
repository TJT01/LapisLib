package mod.tjt01.lapislibtest.data.recipe;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LapisLibTestRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, LapisLibTest.MODID);

    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
        return RECIPE_TYPES.register("test", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }

    public static final RegistryObject<RecipeType<TestRecipe>> TEST = register("test");

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
    }
}
