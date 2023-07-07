package mod.tjt01.lapislib.client.config;

import mod.tjt01.lapislib.core.network.LapisLibPacketHandler;
import mod.tjt01.lapislib.core.network.SubmitServerConfigPacket;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfigChangeTracker extends ConfigChangeTracker {
    private final String modId;

    public RemoteConfigChangeTracker(ModConfig config, String modId) {
        super(config);
        this.modId = modId;
    }

    public void save() {
        LapisLibPacketHandler.CHANNEL.sendToServer(new SubmitServerConfigPacket(modId, this));
        this.clearChanges();
    }
}
