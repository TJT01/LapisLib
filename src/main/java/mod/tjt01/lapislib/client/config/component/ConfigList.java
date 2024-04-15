package mod.tjt01.lapislib.client.config.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

import javax.annotation.Nonnull;

public class ConfigList extends ContainerObjectSelectionList<ConfigEntry> {
    public ConfigList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
    }

    @Override
    public int getRowWidth() {
        return 260;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width/2 + (260/2 + 14);
    }

    public void tick() {
        for (ConfigEntry entry: this.children()) {
            entry.tick();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ConfigEntry mouseOver = this.getEntryAtPosition(mouseX, mouseY);
        for (ConfigEntry entry: this.children()) {
            if (entry != mouseOver) entry.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public int addEntry(@Nonnull ConfigEntry pEntry) {
        return super.addEntry(pEntry);
    }
}
