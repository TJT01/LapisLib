package mod.tjt01.lapislib.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenModListPacket {
    @SuppressWarnings("unused")
    public OpenModListPacket(FriendlyByteBuf ignored) {
        this();
    }

    public OpenModListPacket() {}

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler::openModList);
        });
        context.get().setPacketHandled(true);
    }
}
