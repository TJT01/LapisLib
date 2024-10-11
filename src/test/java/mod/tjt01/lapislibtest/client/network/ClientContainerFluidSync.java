package mod.tjt01.lapislibtest.client.network;

import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.menu.TestMachineMenu;
import mod.tjt01.lapislibtest.network.ContainerFluidSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ClientContainerFluidSync {
    public static void sync(ContainerFluidSyncPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        assert minecraft.player != null;
        AbstractContainerMenu containerMenu = minecraft.player.containerMenu;
        if (containerMenu instanceof TestMachineMenu testMachineMenu) {
            if (containerMenu.containerId != packet.containerId) {
                LapisLibTest.LOGGER.warn("Ignoring container fluid sync for mismatched container ids: expected {}, got {}", containerMenu.containerId, packet.containerId);
            }
            testMachineMenu.fluid = packet.fluid;
        } else {
            LapisLibTest.LOGGER.warn("Ignoring container fluid sync for invalid container {}", containerMenu.getClass().toString());
        }
    }
}
