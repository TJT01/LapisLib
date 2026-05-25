package mod.tjt01.lapislib.registry.entity.attribute;

import mod.tjt01.lapislib.LapisLib;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

public class LapisLibAttributes {
    protected static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, LapisLib.MODID);

    public static final RegistryObject<Attribute> BLOCK_BREAK_SPEED = ATTRIBUTES.register(
            "player.block_break_speed", () -> new RangedAttribute(
                    "attribute.name.lapislib.player.block_break_speed", 1.0F, 0.0F, 1024.0F
            ).setSyncable(true)
    );

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}
