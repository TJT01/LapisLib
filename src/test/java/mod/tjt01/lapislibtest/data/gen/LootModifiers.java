package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislib.data.loot.modifier.ReplaceItemModifier;
import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class LootModifiers extends GlobalLootModifierProvider {
    public LootModifiers(DataGenerator gen) {
        super(gen, LapisLibTest.MODID);
    }

    @Override
    protected void start() {
        add("test_replace_item", ReplaceItemModifier.Serializer.INSTANCE, new ReplaceItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/zombified_piglin"))
                                .build()
                },
                Ingredient.of(Items.ROTTEN_FLESH), Items.PORKCHOP
        ));
    }
}
