package mod.tjt01.lapislib.registry.loot;

import com.mojang.serialization.Codec;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.data.loot.modifier.AddEntryModifier;
import mod.tjt01.lapislib.data.loot.modifier.RemoveItemModifier;
import mod.tjt01.lapislib.data.loot.modifier.ReplaceItemModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, LapisLib.MODID
    );

    public static final RegistryObject<Codec<ReplaceItemModifier>> REPLACE_ITEM = LOOT_MODIFIERS
            .register("replace_item", () -> ReplaceItemModifier.CODEC);

    public static final RegistryObject<Codec<RemoveItemModifier>> REMOVE_ITEM = LOOT_MODIFIERS
            .register("remove_item", () -> RemoveItemModifier.CODEC);

    public static final RegistryObject<Codec<AddEntryModifier>> ADD_ENTRY = LOOT_MODIFIERS
            .register("add_entry", () -> AddEntryModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }
}
