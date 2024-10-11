package mod.tjt01.lapislibtest.network;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TestNetwork {
    private static final String PROTOCOL = "1";
    private static int id = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LapisLibTest.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                id++,
                ContainerFluidSyncPacket.class,
                ContainerFluidSyncPacket::encode,
                ContainerFluidSyncPacket::decode,
                ContainerFluidSyncPacket::handle
        );
    }
}
