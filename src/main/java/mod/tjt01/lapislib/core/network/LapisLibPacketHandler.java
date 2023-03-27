package mod.tjt01.lapislib.core.network;

import mod.tjt01.lapislib.LapisLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;

public class LapisLibPacketHandler {
    private static final String PROTOCOL_VER = "1";
    private static int id = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LapisLib.MODID, "main"),
            () -> PROTOCOL_VER,
            PROTOCOL_VER::equals,
            PROTOCOL_VER::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                id++, OpenModListPacket.class,
                (openModListPacket, friendlyByteBuf) -> {}, OpenModListPacket::new, OpenModListPacket::handle
        );
    }
}
