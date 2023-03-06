package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public abstract class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {
    public void tick() {}
}
