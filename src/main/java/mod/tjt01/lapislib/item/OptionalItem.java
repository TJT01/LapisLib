package mod.tjt01.lapislib.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class OptionalItem extends Item {
    public final Supplier<Boolean> condition;

    public OptionalItem(Properties properties, Supplier<Boolean> condition) {
        super(properties);
        this.condition = condition;
    }

    public String getDisabledTooltip() {
        return "lapislib.common.disabled";
    }

    public Component getDisabledHoverText() {
        return Component.translatable(getDisabledTooltip()).withStyle(ChatFormatting.RED);
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> stacks) {
        if (condition.get())
            super.fillItemCategory(group, stacks);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!condition.get())
            components.add(getDisabledHoverText());
        super.appendHoverText(stack, level, components, flag);
    }
}
