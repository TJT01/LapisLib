package mod.tjt01.lapislib.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * @see mod.tjt01.lapislib.item.OptionalItem
 */
public class OptionalBlock extends Block {
    public final Supplier<Boolean> condition;

    public OptionalBlock(Properties properties, Supplier<Boolean> condition) {
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks) {
        if (condition.get())
            super.fillItemCategory(group, stacks);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> components, TooltipFlag flag) {
        if (!condition.get())
            components.add(getDisabledHoverText());
        super.appendHoverText(stack, getter, components, flag);
    }
}
