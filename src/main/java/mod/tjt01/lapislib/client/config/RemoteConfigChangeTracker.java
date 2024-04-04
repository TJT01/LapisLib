package mod.tjt01.lapislib.client.config;

import mod.tjt01.lapislib.core.network.LapisLibPacketHandler;
import mod.tjt01.lapislib.core.network.SubmitServerConfigPacket;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfigChangeTracker extends ConfigChangeTracker {
    public RemoteConfigChangeTracker(ModConfig config) {
        super(config);
    }

    public void save() {
        LapisLibPacketHandler.CHANNEL.sendToServer(new SubmitServerConfigPacket(this.config.getFileName(), this));
        this.clearChanges();
    }
}
