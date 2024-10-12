package mod.tjt01.lapislib.crafting.ingredient.fluid;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;

public class FluidStackIngredient implements FluidIngredient {
    protected final FluidStack stack;

    public FluidStackIngredient(FluidStack stack) {
        if (stack.isEmpty()) throw new IllegalArgumentException("Stack cannot be empty");
        this.stack = stack;
    }

    @Override
    public int getAmountToDrain(FluidStack stack) {
        return stack.getAmount();
    }

    @Override
    public List<FluidStack> getFluids() {
        return Collections.singletonList(stack);
    }

    @Override
    public boolean test(FluidStack other) {
        return stack.getFluid().equals(other.getFluid()) && other.getAmount() >= stack.getAmount();
    }

    @Override
    public FluidIngredient.Serializer<? extends FluidIngredient> getSerializer() {
        return FluidIngredients.FLUID_STACK.get();
    }

    public static class Serializer implements FluidIngredient.Serializer<FluidStackIngredient> {
        @Override
        public void toJson(FluidStackIngredient ingredient, JsonObject json) {
            json.addProperty(
                    "fluid",
                    ForgeRegistries.FLUIDS.getResourceKey(ingredient.stack.getFluid())
                            .map(ResourceKey::location)
                            .orElseThrow()
                            .toString()
            );
            json.addProperty("amount", ingredient.stack.getAmount());
        }

        @Override
        public FluidStackIngredient fromJson(JsonObject json) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            Fluid fluid = ForgeRegistries.FLUIDS.getHolder(id).orElseThrow(
                    () -> new JsonSyntaxException("Unknown fluid '" + id + "'")
            ).get();
            if (fluid == Fluids.EMPTY) throw new JsonSyntaxException("Invalid fluid '" + id + "'");

            int amount = GsonHelper.getAsInt(json, "amount");
            if (amount <= 0) throw new JsonSyntaxException("Count must be greater than 0");

            return new FluidStackIngredient(new FluidStack(fluid, amount));
        }

        @Override
        public void toNetwork(FluidStackIngredient ingredient, FriendlyByteBuf buf) {
            buf.writeFluidStack(ingredient.stack);
        }

        @Override
        public FluidStackIngredient fromNetwork(FriendlyByteBuf buf) {
            return new FluidStackIngredient(buf.readFluidStack());
        }
    }
}
