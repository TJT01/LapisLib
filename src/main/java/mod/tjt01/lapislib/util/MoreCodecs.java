package mod.tjt01.lapislib.util;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import mod.tjt01.lapislib.LapisLib;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public class MoreCodecs {
    public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.flatXmap(
            dynamic -> {
                try {
                    return DataResult.success(Ingredient.fromJson(IGlobalLootModifier.getJson(dynamic)));
                } catch (JsonParseException e) {
                    LapisLib.LOGGER.warn("Failed to decode ingredient", e);
                    return DataResult.error(e.getMessage());
                }
            },
            ingredient -> {
                try {
                    return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));
                } catch (JsonParseException e) {
                    LapisLib.LOGGER.warn("Failed to encode ingredient", e);
                    return DataResult.error(e.getMessage());
                }
            }
    );

}
