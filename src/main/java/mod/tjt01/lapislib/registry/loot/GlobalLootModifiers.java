package mod.tjt01.lapislib.registry.loot;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.data.loot.modifier.AddEntryModifier;
import mod.tjt01.lapislib.data.loot.modifier.RemoveItemModifier;
import mod.tjt01.lapislib.data.loot.modifier.ReplaceItemModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GlobalLootModifiers {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(
            ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, LapisLib.MODID
    );

    public static final RegistryObject<GlobalLootModifierSerializer<ReplaceItemModifier>> REPLACE_ITEM = LOOT_MODIFIERS
            .register("replace_item", () -> ReplaceItemModifier.Serializer.INSTANCE);

    public static final RegistryObject<GlobalLootModifierSerializer<RemoveItemModifier>> REMOVE_ITEM = LOOT_MODIFIERS
            .register("remove_item", () -> RemoveItemModifier.Serializer.INSTANCE);

    public static final RegistryObject<GlobalLootModifierSerializer<AddEntryModifier>> ADD_ENTRY = LOOT_MODIFIERS
            .register("add_entry", () -> AddEntryModifier.Serializer.INSTANCE);

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }
}
