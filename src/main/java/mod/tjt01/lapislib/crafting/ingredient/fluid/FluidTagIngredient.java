package mod.tjt01.lapislib.crafting.ingredient.fluid;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FluidTagIngredient implements FluidIngredient{
    protected final TagKey<Fluid> tag;
    protected final int amount;

    public FluidTagIngredient(TagKey<Fluid> tag, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0");
        this.amount = amount;
        this.tag = tag;
    }

    @Override
    public List<FluidStack> getFluids() {
        return Objects.requireNonNull(ForgeRegistries.FLUIDS.tags()).getTag(tag).stream()
                .map(fluid -> new FluidStack(fluid, amount))
                .collect(Collectors.toList());
    }

    @Override
    public FluidIngredient.Serializer<? extends FluidIngredient> getSerializer() {
        return FluidIngredients.TAG.get();
    }

    @Override
    public boolean test(FluidStack stack) {
        return ForgeRegistries.FLUIDS.getHolder(stack.getFluid()).orElseThrow().is(tag);
    }

    public static class Serializer implements FluidIngredient.Serializer<FluidTagIngredient> {
        @Override
        public void toJson(FluidTagIngredient ingredient, JsonObject json) {
            json.addProperty("tag", ingredient.tag.location().toString());
            json.addProperty("amount", ingredient.amount);
        }

        @Override
        public FluidTagIngredient fromJson(JsonObject json) {
            ResourceLocation location = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            TagKey<Fluid> tag = TagKey.create(ForgeRegistries.Keys.FLUIDS, location);
            int amount = GsonHelper.getAsInt(json, "amount");
            if (amount <= 0) throw new JsonSyntaxException("Amount must be greater than 0");
            return new FluidTagIngredient(tag, amount);
        }

        @Override
        public void toNetwork(FluidTagIngredient ingredient, FriendlyByteBuf buf) {
            buf.writeResourceLocation(ingredient.tag.location());
            buf.writeInt(ingredient.amount);
        }

        @Override
        public FluidTagIngredient fromNetwork(FriendlyByteBuf buf) {
            TagKey<Fluid> tag = TagKey.create(ForgeRegistries.Keys.FLUIDS, buf.readResourceLocation());
            int amount = buf.readInt();
            return new FluidTagIngredient(tag, amount);
        }
    }
}
