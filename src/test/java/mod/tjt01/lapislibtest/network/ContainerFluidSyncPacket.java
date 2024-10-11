package mod.tjt01.lapislibtest.network;

import mod.tjt01.lapislibtest.client.network.ClientContainerFluidSync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ContainerFluidSyncPacket {
    public final int containerId;
    public final FluidStack fluid;

    public ContainerFluidSyncPacket(int containerId, FluidStack fluid) {
        this.containerId = containerId;
        this.fluid = fluid;
    }

    public static ContainerFluidSyncPacket decode(FriendlyByteBuf buffer) {
        return new ContainerFluidSyncPacket(buffer.readInt(), buffer.readFluidStack());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeFluidStack(fluid);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientContainerFluidSync.sync(this);
            });
        });
        context.get().setPacketHandled(true);
    }
}
