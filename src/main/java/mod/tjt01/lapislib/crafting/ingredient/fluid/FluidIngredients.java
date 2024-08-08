package mod.tjt01.lapislib.crafting.ingredient.fluid;

import mod.tjt01.lapislib.LapisLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FluidIngredients {
    public static final ResourceLocation ID = new ResourceLocation(LapisLib.MODID, "fluid_ingredient");

    public static final DeferredRegister<FluidIngredient.Serializer<?>> FLUID_INGREDIENTS = DeferredRegister.create(
            ID, LapisLib.MODID
    );

    public static final Supplier<IForgeRegistry<FluidIngredient.Serializer<?>>> REGISTRY
            = FLUID_INGREDIENTS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<FluidIngredient.Serializer<FluidStackIngredient>> FLUID_STACK
            = FLUID_INGREDIENTS.register("fluid_stack", FluidStackIngredient.Serializer::new);

    public static final RegistryObject<FluidIngredient.Serializer<FluidTagIngredient>> TAG
            = FLUID_INGREDIENTS.register("tag", FluidTagIngredient.Serializer::new);

    public static void register(IEventBus bus) {
        FLUID_INGREDIENTS.register(bus);
    }
}
