package mod.tjt01.lapislib.data.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.tjt01.lapislib.util.MoreCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class ReplaceItemModifier extends LootModifier {
    public static final Codec<ReplaceItemModifier> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance).and(
                    instance.group(
                            MoreCodecs.INGREDIENT_CODEC.fieldOf("target").forGetter(ReplaceItemModifier::getTarget),
                            ForgeRegistries.ITEMS.getCodec().fieldOf("replacement").forGetter(ReplaceItemModifier::getReplacement)
                    )
            ).apply(instance, ReplaceItemModifier::new)
    );

    public final Ingredient target;
    public final Item replacement;

    public ReplaceItemModifier(LootItemCondition[] conditionsIn, Ingredient target, Item replacement) {
        super(conditionsIn);
        this.target = target;
        this.replacement = replacement;
    }

    protected Ingredient getTarget() {
        return target;
    }

    protected Item getReplacement() {
        return replacement;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot.stream()
                .map(stack -> {
                    if(target.test(stack)) {
                        return new ItemStack(replacement, stack.getCount(), stack.getTag());
                    }
                    return stack;
                })
                .collect(ObjectArrayList.toList());
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return null;
    }
}
