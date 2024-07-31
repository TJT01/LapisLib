package mod.tjt01.lapislib.data.loot.modifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.util.MoreCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class RemoveItemModifier extends LootModifier {
    public static final Codec<RemoveItemModifier> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance).and(
                    MoreCodecs.INGREDIENT_CODEC.fieldOf("remove").forGetter(RemoveItemModifier::getIngredient)
            ).apply(instance, RemoveItemModifier::new)
    );

    public final Ingredient remove;

    public RemoveItemModifier(LootItemCondition[] conditionsIn, Ingredient remove) {
        super(conditionsIn);
        this.remove = remove;
    }

    protected Ingredient getIngredient() {
        return this.remove;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot.stream()
                .filter(stack -> !remove.test(stack))
                .collect(ObjectArrayList.toList());
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
