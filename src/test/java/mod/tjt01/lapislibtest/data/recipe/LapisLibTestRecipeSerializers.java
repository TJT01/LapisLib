package mod.tjt01.lapislibtest.data.recipe;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LapisLibTestRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LapisLibTest.MODID);

    public static final RegistryObject<RecipeSerializer<?>> TEST = RECIPE_SERIALIZERS.register("test", TestRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}
