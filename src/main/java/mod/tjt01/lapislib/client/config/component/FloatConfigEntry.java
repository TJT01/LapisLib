package mod.tjt01.lapislib.client.config.component;

import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class FloatConfigEntry extends AbstractNumberConfigEntry<Float>{
    public FloatConfigEntry(Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<Float> configValue, ForgeConfigSpec.ValueSpec valueSpec) {
        super(label, tracker, configValue, valueSpec);
    }

    @Override
    protected Float getTypeMin() {
        return Float.NEGATIVE_INFINITY;
    }

    @Override
    protected Float getTypeMax() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    protected Float parse(String text) throws NumberFormatException {
        return Float.parseFloat(text);
    }
}
