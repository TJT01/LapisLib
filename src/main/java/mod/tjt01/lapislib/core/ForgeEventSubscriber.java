package mod.tjt01.lapislib.core;

//import net.minecraftforge.event.RegisterCommandsEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.registry.entity.attribute.LapisLibAttributes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEventSubscriber {
//    @SubscribeEvent
//    public static void onRegisterCommands(RegisterCommandsEvent event) {
//        LapisLibCommands.register(event.getDispatcher());
//    }
    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(
                (float) (event.getNewSpeed() * event.getEntity().getAttributeValue(LapisLibAttributes.BLOCK_BREAK_SPEED.get()))
        );
    }
}
