package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class Lang extends LanguageProvider {
    public Lang(DataGenerator gen) {
        super(gen, LapisLibTest.MODID, "en_us");
    }

    private void addConfigKey(String name, String translation) {
        add("config.lapislib_test." + name, translation);
    }

    @Override
    protected void addTranslations() {
        addConfigKey("category.category", "Test Category");

        addConfigKey("testString", "Test String");
        addConfigKey("testBoolean", "Test Boolean");
        addConfigKey("testBoolean2", "Test Boolean 2");
        addConfigKey("testBoolean3", "Test Boolean 3");
        addConfigKey("testFloat", "Test Float");
        addConfigKey("testDouble", "Test Double");
        addConfigKey("testInt", "Test Integer");
        addConfigKey("testLong", "Test Long Integer");
        addConfigKey("testFloatRanged", "Test Ranged Float");
        addConfigKey("testDoubleRanged", "Test Ranged Double");
        addConfigKey("testIntRanged", "Test Ranged Integer");
        addConfigKey("testLongRanged", "Test Ranged Long Integer");
        addConfigKey("testEnum", "Test Enum");
    }
}
