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
    }
}
