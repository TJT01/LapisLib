package mod.tjt01.lapislib.core.datagen;

import mod.tjt01.lapislib.LapisLib;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class LapisLibLanguage extends LanguageProvider {
    public LapisLibLanguage(DataGenerator gen) {
        super(gen, LapisLib.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("lapislib.common.disabled", "Disabled");
        this.add("config.lapislib.showItemTags", "Show Item Tags");
        this.add("lapislib.common.config.root_title", "%s config");

        this.add("lapislib.common.config.client", "Client");
        this.add("lapislib.common.config.common", "Common");
        this.add("lapislib.common.config.server", "Server");
        this.add("lapislib.common.config.unsupported", "Type %s is not supported (in %s)");
        this.add("lapislib.common.edit", "Edit");
        this.add("lapislib.common.config.no_level", "To edit server configs; join a world, then use \u00A7o/lapislib showModList\u00A7r.");
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
