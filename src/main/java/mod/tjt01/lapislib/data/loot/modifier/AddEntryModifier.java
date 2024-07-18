package mod.tjt01.lapislib.data.loot.modifier;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.tjt01.lapislib.LapisLib;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class AddEntryModifier extends LootModifier {
    private static final Gson GSON = Deserializers.createFunctionSerializer().create();

    public static final Codec<LootPoolEntryContainer> ENTRY_CODEC = Codec.PASSTHROUGH.flatXmap(
            dynamic -> {
                try {
                    LootPoolEntryContainer entry = GSON.fromJson(
                            IGlobalLootModifier.getJson(dynamic), LootPoolEntryContainer.class);
                    return DataResult.success(entry);
                } catch (JsonSyntaxException e) {
                    LapisLib.LOGGER.warn("Unable to decode loot entry", e);
                    return DataResult.error(e.getMessage());
                }
            },
            entry -> {
                try {
                    JsonElement json = GSON.toJsonTree(entry);
                    return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, json));
                } catch (JsonSyntaxException e) {
                    LapisLib.LOGGER.warn("Unable to encode loot entry", e);
                    return DataResult.error(e.getMessage());
                }
            }
    );

    public static final Codec<LootItemFunction[]> LOOT_FUNCTION_CODEC = Codec.PASSTHROUGH.flatXmap(
            dynamic -> {
                try {
                    LootItemFunction[] itemFunctions = GSON.fromJson(
                            IGlobalLootModifier.getJson(dynamic), LootItemFunction[].class
                    );
                    return DataResult.success(itemFunctions);
                } catch (JsonSyntaxException e) {
                    LapisLib.LOGGER.warn("Unable to decode loot functions", e);
                    return DataResult.error(e.getMessage());
                }
            },
            itemFunctions -> {
                try {
                    JsonElement json = GSON.toJsonTree(itemFunctions);
                    return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, json));
                } catch (JsonSyntaxException e) {
                    LapisLib.LOGGER.warn("Unable to encode loot functions", e);
                    return DataResult.error(e.getMessage());
                }
            }
    );

    public static final Codec<AddEntryModifier> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance).and(
                    instance.group(
                            ENTRY_CODEC.fieldOf("entry").forGetter(AddEntryModifier::getEntry),
                            LOOT_FUNCTION_CODEC.optionalFieldOf("functions", new LootItemFunction[0]).forGetter(AddEntryModifier::getFunctions)
                    )
            ).apply(instance, AddEntryModifier::new)
    );

    protected final LootPoolEntryContainer entry;
    protected final LootItemFunction[] functions;
    protected final BiFunction<ItemStack, LootContext, ItemStack> combinedFuncs;

    public AddEntryModifier(LootItemCondition[] conditionsIn, LootPoolEntryContainer entry, LootItemFunction[] functions) {
        super(conditionsIn);
        this.entry = entry;
        this.functions = functions;
        this.combinedFuncs = LootItemFunctions.compose(functions);
    }

    protected LootPoolEntryContainer getEntry() {
        return entry;
    }

    protected LootItemFunction[] getFunctions() {
        return functions;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> loot = new ObjectArrayList<>(generatedLoot);
        entry.expand(context, lootPoolEntry -> lootPoolEntry.createItemStack(LootItemFunction.decorate(combinedFuncs, loot::add, context), context));
        return loot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
