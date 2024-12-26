package mod.tjt01.lapislib.client.config.screen;

import com.google.common.collect.ImmutableList;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.ConfigComponents;
import mod.tjt01.lapislib.client.config.RemoteConfigChangeTracker;
import mod.tjt01.lapislib.client.config.factory.ColorConfigFactory;
import mod.tjt01.lapislib.client.config.factory.ConfigEntryFactory;
import mod.tjt01.lapislib.util.ConfigUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RootConfigScreen extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final Component NO_LEVEL
            = Component.translatable("lapislib.common.config.no_level");
    private static final Component NO_PERMISSION
            = Component.translatable("lapislib.common.config.no_permission");

    private final Screen parent;
    private final String modId;

    @Nullable
    private final List<ModConfig> configs;
    @Nullable
    private final Map<ModConfig, ConfigChangeTracker> trackerMap = new HashMap<>();

    private final Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryFactoryMap;

    public static ConfigScreenBuilder builder(String modId) {
        return new ConfigScreenBuilder(modId);
    }

    private RootConfigScreen(
            String modId, Component pTitle, Screen parent,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryFactoryMap, List<ModConfig> modConfigs
    ) {
        super(pTitle);
        if (modConfigs.isEmpty()) throw new IllegalArgumentException("modConfigs must not be empty");
        this.parent = parent;
        this.entryFactoryMap = entryFactoryMap;
        this.configs = modConfigs.stream()
                .sorted(Comparator.comparingInt(o -> o.getType().ordinal()))
                .collect(Collectors.toList());
        this.modId = modId;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void init() {
        int center = this.width/2;
        addRenderableWidget(
                Button.builder(CommonComponents.GUI_BACK, button -> minecraft.setScreen(parent))
                        .pos(center - 64, this.height - 32)
                        .size(128, 20)
                        .build()
        );

        NonNullList<Button> buttons = NonNullList.create();

        for (ModConfig modConfig: configs) {
            Button button;
            if (modConfig.getType() == ModConfig.Type.SERVER) {
                Tooltip tooltip = null;
                if (minecraft.level == null) {
                    tooltip = Tooltip.create(NO_LEVEL);
                } else if (minecraft.player != null && !minecraft.player.hasPermissions(2)) {
                    tooltip = Tooltip.create(NO_PERMISSION);
                }
                button = Button.builder(
                        ConfigComponents.serverTitle,
                        btn -> {
                            ConfigChangeTracker tracker = trackerMap.computeIfAbsent(modConfig, RemoteConfigChangeTracker::new);
                            minecraft.setScreen(
                                    new ConfigScreen(
                                            modConfig, ConfigComponents.serverTitle, this,
                                            ConfigUtil.toForgeConfigSpec(modConfig.getSpec()),
                                            tracker, entryFactoryMap, true
                                    )
                            );
                        })
                        .pos(center - BUTTON_WIDTH/2, 0)
                        .size(BUTTON_WIDTH, 20)
                        .tooltip(tooltip)
                        .build();
                button.active = minecraft.level != null
                        && minecraft.player != null
                        && minecraft.player.hasPermissions(2);
            } else {
                Component title = modConfig.getType() == ModConfig.Type.CLIENT
                        ? ConfigComponents.clientTitle
                        : ConfigComponents.commonTitle;
                button = Button.builder(
                        title,
                        btn -> {
                            ConfigChangeTracker tracker = trackerMap.computeIfAbsent(modConfig, ConfigChangeTracker::new);
                            minecraft.setScreen(
                                    new ConfigScreen(
                                            modConfig, title, this,
                                            ConfigUtil.toForgeConfigSpec(modConfig.getSpec()),
                                            tracker, entryFactoryMap, true
                                    )
                            );
                        })
                        .pos(center - BUTTON_WIDTH/2, 0)
                        .size(BUTTON_WIDTH, 20)
                        .build();
            }

            buttons.add(button);
            addRenderableWidget(button);
        }

        int yOffset = (this.height/2) - (buttons.size()*24 - 4)/2;
        int btY = 0;

        for (Button button: buttons) {
            button.setY(yOffset + btY);
            btY += 24;
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(font, this.title, this.width/2, 15, 0xFFFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.setScreen(parent);
    }

    public static class ConfigScreenBuilder {
        private final Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryMap = new HashMap<>();

        private final String modId;

        protected ConfigScreenBuilder(String modId) {
            this.modId = modId;
        }

        public ConfigScreenBuilder setEntryType(ForgeConfigSpec.ConfigValue<?> configValue, ConfigEntryFactory e) {
            this.entryMap.put(configValue, e);
            return this;
        }

        public ConfigScreenBuilder defineColor(ForgeConfigSpec.ConfigValue<?> configValue, boolean hasAlpha) {
            return setEntryType(configValue, new ColorConfigFactory(hasAlpha));
        }


        @SuppressWarnings("unchecked")
        public ConfigScreenHandler.ConfigScreenFactory getFactory() {
            return new ConfigScreenHandler.ConfigScreenFactory((minecraft1, screen) -> {
                Optional<? extends ModContainer> container = ModList.get().getModContainerById(modId);
                if (container.isEmpty())
                    throw new IllegalStateException("Mod container for " + modId + " does not exist");
                String displayName = container.get().getModInfo().getDisplayName();

                List<ModConfig> list;
                try {
                    Map<ModConfig.Type, ModConfig> configs = (
                            (ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>>)
                                    FieldUtils.readField(ConfigTracker.INSTANCE, "configsByMod", true)
                    ).get(modId);

                    if (configs == null) {
                        throw new IllegalStateException("No configs for mod " + modId);
                    }

                    list = ImmutableList.copyOf(configs.values());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                return new RootConfigScreen(
                        modId, Component.translatable("lapislib.common.config.root_title", displayName),
                        screen, entryMap, list);
            });
        }
    }
}
