package mod.tjt01.lapislib.client.config.component;

import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public abstract class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {
    public void tick() {}

    public abstract List<FormattedCharSequence> getTooltip(int top, int left, int mouseX, int mouseY);
}
