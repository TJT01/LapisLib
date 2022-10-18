package mod.tjt01.lapislib.core;

import mod.tjt01.lapislib.core.config.LapisLibConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class ForgeEventSubscriber {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onToolTipEvent(final ItemTooltipEvent event) {
        List<Component> tooltip = event.getToolTip();

        if (event.getFlags().isAdvanced() && LapisLibConfig.showItemTags) {
            event.getItemStack().getTags().forEach(
                    itemTagKey -> tooltip.add(
                            new TextComponent("#" + itemTagKey.location())
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY))
                    )
            );
        }
    }
}
