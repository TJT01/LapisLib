package mod.tjt01.lapislib.data.loot.modifier;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class AddEntryModifier extends LootModifier {
    protected final LootPoolEntryContainer entry;
    protected final LootItemFunction[] functions;
    protected final BiFunction<ItemStack, LootContext, ItemStack> combinedFuncs;

    public AddEntryModifier(LootItemCondition[] conditionsIn, LootPoolEntryContainer entry, LootItemFunction[] functions) {
        super(conditionsIn);
        this.entry = entry;
        this.functions = functions;
        this.combinedFuncs = LootItemFunctions.compose(functions);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ArrayList<ItemStack> loot = new ArrayList<>(generatedLoot);
        entry.expand(context, lootPoolEntry -> lootPoolEntry.createItemStack(LootItemFunction.decorate(combinedFuncs, loot::add, context), context));
        return loot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddEntryModifier> {
        private static final Gson GSON = Deserializers.createFunctionSerializer().create();
        public static final AddEntryModifier.Serializer INSTANCE = new AddEntryModifier.Serializer();

        @Override
        public AddEntryModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            LootPoolEntryContainer entry = GSON.fromJson(GsonHelper.getAsJsonObject(object, "entry"), LootPoolEntryContainer.class);
            LootItemFunction[] functions = object.has("functions")
                    ? GSON.fromJson(GsonHelper.getAsJsonArray(object, "functions"), LootItemFunction[].class)
                    : new LootItemFunction[0];
            return new AddEntryModifier(ailootcondition, entry, functions);
        }

        @Override
        public JsonObject write(AddEntryModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.add("entry", GSON.toJsonTree(instance.entry, LootPoolEntryContainer.class));
            if (instance.functions.length > 0)
                json.add("functions", GSON.toJsonTree(instance.functions, LootItemFunction[].class));
            return json;
        }
    }
}
