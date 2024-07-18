package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislib.data.loot.modifier.AddEntryModifier;
import mod.tjt01.lapislib.data.loot.modifier.RemoveItemModifier;
import mod.tjt01.lapislib.data.loot.modifier.ReplaceItemModifier;
import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class LootModifiers extends GlobalLootModifierProvider {
    public LootModifiers(DataGenerator gen) {
        super(gen, LapisLibTest.MODID);
    }

    @Override
    protected void start() {
        add("test_replace_item", new ReplaceItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/zombified_piglin")).build()
                },
                Ingredient.of(Items.ROTTEN_FLESH), Items.PORKCHOP
        ));

        add("test_add_loot", new AddEntryModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/silverfish")).build(),
                },
                LootItem.lootTableItem(Items.IRON_NUGGET).build(),
                new LootItemFunction[] {
                        SetItemCountFunction.setCount(
                                UniformGenerator.between(0.0F, 2.0F)
                        ).build(),
                        LootingEnchantFunction.lootingMultiplier(
                                UniformGenerator.between(0.0F, 1.0F)
                        ).build()
                }
        ));

        add("test_remove_item", new RemoveItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/iron_golem")).build()
                },
                Ingredient.of(Tags.Items.INGOTS_IRON)
        ));
    }
}
