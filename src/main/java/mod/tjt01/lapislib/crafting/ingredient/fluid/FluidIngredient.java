package mod.tjt01.lapislib.crafting.ingredient.fluid;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface FluidIngredient extends Predicate<FluidStack> {
    ImmutableSet<Class<? extends FluidIngredient>> SIMPLE_JSON = ImmutableSet.of(
            FluidStackIngredient.class, FluidTagIngredient.class
    );

    static boolean hasSimpleJson(FluidIngredient ingredient) {
        return SIMPLE_JSON.contains(ingredient.getClass());
    }

    @SuppressWarnings("unchecked")
    static <T extends FluidIngredient> JsonObject toJson(T ingredient) {
        JsonObject json = new JsonObject();
        Serializer<T> serializer = (Serializer<T>) ingredient.getSerializer();

        if (!hasSimpleJson(ingredient)) {
            String id = FluidIngredients.REGISTRY.get()
                    .getResourceKey(serializer)
                    .orElseThrow()
                    .location()
                    .toString();
            json.addProperty("type", id);
        }

        serializer.toJson(ingredient, json);
        return json;
    }

    static FluidIngredient fromJson(JsonObject json) {
        String type = GsonHelper.getAsString(json, "type", null);

        if (type == null) {
            boolean isFluid = json.has("fluid");
            boolean isTag = json.has("tag");
            if (isFluid && isTag) {
                throw new JsonSyntaxException("Built-in fluid ingredients must define either 'fluid' or 'tag', not both");
            } else if (isFluid) {
                return FluidIngredients.FLUID_STACK.get().fromJson(json);
            } else if (isTag) {
                return FluidIngredients.TAG.get().fromJson(json);
            } else {
                throw new JsonSyntaxException("Fluid ingredient missing one of 'type', 'fluid', or 'tag'");
            }
        }

        Serializer<?> serializer = FluidIngredients.REGISTRY.get().getValue(new ResourceLocation(type));

        if (serializer == null) {
            throw new JsonSyntaxException("Unknown fluid ingredient type '" + type + "'");
        }

        return serializer.fromJson(json);
    }

    @SuppressWarnings("unchecked")
    static <T extends FluidIngredient> void toNetwork(T ingredient, FriendlyByteBuf buffer) {
        //TODO buffer#writeId?
        Serializer<T> serializer = (Serializer<T>) ingredient.getSerializer();

        buffer.writeResourceLocation(
                Objects.requireNonNull(FluidIngredients.REGISTRY.get().getKey(ingredient.getSerializer()))
        );

        serializer.toNetwork(ingredient, buffer);
    }

    static FluidIngredient fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        Serializer<?> serializer = Objects.requireNonNull(FluidIngredients.REGISTRY.get().getValue(id));
        return serializer.fromNetwork(buffer);
    }

    int getAmountToDrain(FluidStack stack);

    List<FluidStack> getFluids();

    default boolean isEmpty() {
        return getFluids().isEmpty();
    }

    Serializer<? extends FluidIngredient> getSerializer();

    interface Serializer<T extends FluidIngredient> {
        void toJson(T ingredient, JsonObject json);

        T fromJson(JsonObject json);

        void toNetwork(T ingredient, FriendlyByteBuf buf);

        T fromNetwork(FriendlyByteBuf buf);
    }
}
