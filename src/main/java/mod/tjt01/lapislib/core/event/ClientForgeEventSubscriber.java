package mod.tjt01.lapislib.core.event;

import mod.tjt01.lapislib.core.config.LapisLibConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientForgeEventSubscriber {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onToolTipEvent(final ItemTooltipEvent event) {
        List<Component> tooltip = event.getToolTip();

        if (event.getFlags().isAdvanced() && LapisLibConfig.showItemTags) {
            event.getItemStack().getTags().forEach(
                    itemTagKey -> tooltip.add(
                            Component.literal("#" + itemTagKey.location())
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY))
                    )
            );
        }
    }
}
