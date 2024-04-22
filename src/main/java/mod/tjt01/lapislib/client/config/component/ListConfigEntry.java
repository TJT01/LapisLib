package mod.tjt01.lapislib.client.config.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.screen.ListEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ListConfigEntry extends AbstractForgeConfigEntry<List<?>> {
    protected static final Component BUTTON_LABEL = new TranslatableComponent("lapislib.common.edit");
    protected final ConfigChangeTracker tracker;
    protected final Button button;

    protected final ForgeConfigSpec.ConfigValue<List<?>> configValue;

    @Nonnull
    public static ConfigEntry create(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<List<?>> configValue,
            ForgeConfigSpec.ValueSpec valueSpec, Screen parent
    ) {
        List<?> aDefault = (List<?>) valueSpec.getDefault();
        ListType listType = ListType.UNKNOWN;

        if (!aDefault.isEmpty()) listType = ListType.fromObject(aDefault);

        if (listType == ListType.UNKNOWN) {
            listType = ListType.fromValidator(valueSpec);
        }

        if (listType == ListType.UNKNOWN) {
            LapisLib.LOGGER.error("Cannot infer type of list {}", String.join(".", configValue.getPath()));
            return new InvalidConfigEntry(
                    new TextComponent("Cannot infer type of list " + String.join(".", configValue.getPath()))
            );
        } else {
            return new ListConfigEntry(label, tracker, configValue, valueSpec, listType, parent);
        }
    }

    public ListConfigEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<List<?>> configValue,
            ForgeConfigSpec.ValueSpec valueSpec, ListType type, Screen parent
    ) {
        super(label, tracker, configValue, valueSpec);
        this.tracker = tracker;
        this.configValue = configValue;

        this.button = new Button(0, 0, 50, 20, BUTTON_LABEL, pButton -> Minecraft.getInstance().setScreen(new ListEditScreen(new TextComponent("ABCXYZ"), tracker, configValue, valueSpec, type, parent)));

        this.widgets.add(button);
    }

    @Override
    public void render(
            @Nonnull PoseStack poseStack, int index,
            int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isMouseOver,
            float partialTick
    ) {
        button.x = left + width - 50 - 40;
        button.y = top;
        button.render(poseStack, mouseX, mouseY, partialTick);

        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
    }

    public enum ListType {
        STRING, INTEGER, LONG, FLOAT, DOUBLE, BOOLEAN, UNKNOWN;

        protected static ListType fromObject(Object object) {
            if (object instanceof String) {
                return STRING;
            } else if (object instanceof Integer) {
                return INTEGER;
            } else if (object instanceof Long) {
                return LONG;
            } else if (object instanceof Float) {
                return FLOAT;
            } else if (object instanceof Double) {
                return DOUBLE;
            } else if (object instanceof Boolean) {
                return BOOLEAN;
            } else {
                return UNKNOWN;
            }
        }

        protected static ListType fromValidator(ForgeConfigSpec.ValueSpec spec) {
            if (spec.test(Collections.singletonList("t"))) {
                return STRING;
            } else if (spec.test(Collections.singletonList(0))) {
                return INTEGER;
            } else if (spec.test(Collections.singletonList(0L))) {
                return LONG;
            } else if (spec.test(Collections.singletonList(0.0F))) {
                return FLOAT;
            } else if (spec.test(Collections.singletonList(0.0D))) {
                return DOUBLE;
            } else if (spec.test(Collections.singletonList(true))) {
                return BOOLEAN;
            } else {
                return UNKNOWN;
            }
        }
    }
}
