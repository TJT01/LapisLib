package mod.tjt01.lapislibtest.data.gen;

import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
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
        add(LapisLibTestBlocks.CRAFTING_BLOCK.get(), "Test Crafting Block");
        add("container.lapislib_test.crafting_test", "Crafting Test");

        add(LapisLibTestBlocks.MACHINE_BLOCK.get(), "Test Machine");
        add("container.lapislib_test.machine", "Machine");

        add("lapislib_test.gui.fluid.empty", "0 / %s mb");
        add("lapislib_test.gui.fluid", "%s / %s mb %s");

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
