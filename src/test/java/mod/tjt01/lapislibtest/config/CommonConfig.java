package mod.tjt01.lapislibtest.config;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

    public final ForgeConfigSpec.ConfigValue<Integer> colorInt;
    public final ForgeConfigSpec.ConfigValue<Integer> colorAlphaInt;
    public final ForgeConfigSpec.ConfigValue<String> colorString;
    public final ForgeConfigSpec.ConfigValue<String> colorAlphaString;

    private static String getTranslation(String path) {
        return "config." + LapisLibTest.MODID + "." + path;
    }

    @SuppressWarnings("unused")
    public enum ExampleEnum {
        VANILLA,
        CHOCOLATE,
        STRAWBERRY
    }

    public CommonConfig(final ForgeConfigSpec.Builder builder) {
        builder.comment("Test string value.")
                .translation(getTranslation("testString"))
                .define("testString", "Sample Text");
        builder.comment("Test boolean value.")
                .translation(getTranslation("testBoolean"))
                .define("testBoolean", true);

        builder.push("category");
        builder.comment("Another test boolean value.")
                .translation(getTranslation("testBoolean2"))
                .define("testBoolean2", false);

        builder.push("subcategory");
        builder.comment("Third test boolean value.")
                .translation(getTranslation("testBoolean3"))
                .define("testBoolean3", true);
        builder.pop(2);

        builder.comment("Test float value")
                .translation(getTranslation("testFloat"))
                .define("testNumber", 7.0F);
        builder.comment("Test double value")
                .translation(getTranslation("testDouble"))
                .define("testDouble", 200.0D);
        builder.comment("Test integer value")
                .translation(getTranslation("testInt"))
                .define("testInt", 1024);
        builder.comment("Test long value")
                .translation(getTranslation("testLong"))
                .define("testLong", 99999999L);

        builder.comment("Test ranged float value")
                .translation(getTranslation("testFloatRanged"))
                .defineInRange("testNumberRanged", 7.0F, 1.0F, 10.0F);
        builder.comment("Test ranged double value")
                .translation(getTranslation("testDoubleRanged"))
                .defineInRange("testDoubleRanged", 200.0D, -500.0D, 500.0D);
        builder.comment("Test ranged integer value")
                .translation(getTranslation("testIntRanged"))
                .defineInRange("testIntRanged", 1024, 0, 2048);
        builder.comment("Test ranged long value")
                .translation(getTranslation("testLongRanged"))
                .defineInRange("testLongRanged", 9999L, -100000, 100000);
        builder.comment("Test enum value")
                .translation(getTranslation("testEnum"))
                .defineEnum("testEnum", ExampleEnum.VANILLA);

        this.colorInt = builder.comment("Test color value (int, no alpha)")
                .translation(getTranslation("testColorIntNoAlpha"))
                .define("testColor", 0xABCDEF);
        this.colorAlphaInt = builder.comment("Test color value (int, with alpha)")
                .translation(getTranslation("testColorIntAlpha"))
                .define("testColorAlpha", 0x87654321);

        this.colorString = builder.comment("Test color value (string, no alpha)")
                .translation(getTranslation("testColorStringNoAlpha"))
                .define("testColorString", "C0FFEE");
        this.colorAlphaString = builder.comment("Test color value (string, with alpha)")
                .translation(getTranslation("testColorStringAlpha"))
                .define("testColorAlphaString", "BAADF00D");
    }
}
