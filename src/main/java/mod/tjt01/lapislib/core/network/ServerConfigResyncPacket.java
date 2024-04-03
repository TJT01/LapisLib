package mod.tjt01.lapislib.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerConfigResyncPacket {
    private final String fileName;
    private final byte[] data;

    public ServerConfigResyncPacket(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(fileName).writeByteArray(data);
    }

    public static ServerConfigResyncPacket decode(FriendlyByteBuf buffer) {
        return new ServerConfigResyncPacket(buffer.readUtf(), buffer.readByteArray());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (!Minecraft.getInstance().isLocalServer()) {
                ModConfig config = ConfigTracker.INSTANCE.fileMap().get(fileName);

                if (config != null) {
                    config.acceptSyncedConfig(data);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
