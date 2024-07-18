package mod.tjt01.lapislibtest.data.gen.condition;

import com.google.gson.JsonObject;
import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.config.LapisLibTestConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class TestCondition implements ICondition {
    public static ResourceLocation ID = new ResourceLocation(LapisLibTest.MODID, "test");
    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return LapisLibTestConfig.enableOptionalTestRecipes;
    }

    public static class Serializer implements IConditionSerializer<TestCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, TestCondition value) {}

        @Override
        public TestCondition read(JsonObject json) {
            return new TestCondition();
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
