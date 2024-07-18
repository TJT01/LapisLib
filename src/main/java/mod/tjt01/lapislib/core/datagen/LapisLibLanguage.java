package mod.tjt01.lapislib.core.datagen;

import mod.tjt01.lapislib.LapisLib;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class LapisLibLanguage extends LanguageProvider {
    public LapisLibLanguage(DataGenerator gen) {
        super(gen, LapisLib.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("lapislib.common.disabled", "Disabled");
        this.add("config.lapislib.showItemTags", "Show Item Tags");
        this.add("lapislib.common.config.root_title", "%s Config");

        this.add("lapislib.common.config.client", "Client");
        this.add("lapislib.common.config.common", "Common");
        this.add("lapislib.common.config.server", "Server");
        this.add("lapislib.common.config.unsupported", "Type %s is not supported (in %s)");
        this.add("lapislib.common.edit", "Edit");
        this.add("lapislib.common.config.no_level", "Join a world to edit server configs.");
        this.add("lapislib.common.config.no_permission", "Only operators can modify server configs.");

        this.add("lapislib.common.config.reset.text", "R");
        this.add("lapislib.common.config.reset.tooltip", "Reset");

        this.add("lapislib.common.config.undo.text", "U");
        this.add("lapislib.common.config.undo.tooltip", "Undo");

        this.add("lapislib.common.config.red", "Red: %s");
        this.add("lapislib.common.config.green", "Green: %s");
        this.add("lapislib.common.config.blue", "Blue: %s");
        this.add("lapislib.common.config.alpha", "Alpha: %s");
    }
}
