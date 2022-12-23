package mod.tjt01.lapislib.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class OptionalItem extends Item {
    public final Supplier<Boolean> condition;

    public OptionalItem(Properties properties, Supplier<Boolean> condition) {
        super(properties);
        this.condition = condition;
    }

    public String getDisabledTooltip() {
        return "lapislib.common.disabled";
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> stacks) {
        if (condition.get())
            super.fillItemCategory(group, stacks);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!condition.get())
            components.add(new TranslatableComponent(this.getDisabledTooltip()).withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, level, components, flag);
    }
}
