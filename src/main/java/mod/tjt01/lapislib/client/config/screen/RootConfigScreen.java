package mod.tjt01.lapislib.client.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.ConfigComponents;
import mod.tjt01.lapislib.client.config.RemoteConfigChangeTracker;
import mod.tjt01.lapislib.client.config.factory.ColorConfigFactory;
import mod.tjt01.lapislib.client.config.factory.ConfigEntryFactory;
import mod.tjt01.lapislib.util.ConfigUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RootConfigScreen extends Screen {
    private final Screen parent;
    private final String modId;

    @Nullable
    private final ModConfig clientConfig;
    @Nullable
    private final ModConfig commonConfig;
    @Nullable
    private final ModConfig serverConfig;
    @Nullable
    private ConfigChangeTracker clientTracker;
    @Nullable
    private ConfigChangeTracker commonTracker;
    @Nullable
    private RemoteConfigChangeTracker serverTracker;

    private final Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryFactoryMap;

    public static ConfigScreenBuilder builder(String modId) {
        return new ConfigScreenBuilder(modId);
    }

    private RootConfigScreen(
            String modId, Component pTitle, Screen parent,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryFactoryMap,
            @Nullable ModConfig clientConfig, @Nullable ModConfig commonConfig, @Nullable ModConfig serverConfig
    ) {
        super(pTitle);
        this.parent = parent;
        this.entryFactoryMap = entryFactoryMap;
        this.clientConfig = clientConfig;
        this.commonConfig = commonConfig;
        this.serverConfig = serverConfig;
        this.modId = modId;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void init() {
        int center = this.width/2;
        addRenderableWidget(
                new Button(
                        center - 64, this.height - 32, 128, 20, CommonComponents.GUI_BACK,
                        button -> minecraft.setScreen(parent)
                )
        );

        NonNullList<Button> buttons = NonNullList.create();

        if (clientConfig != null) {
            clientTracker = new ConfigChangeTracker(clientConfig);
            Button button = new Button(
                    center - 50, 0, 100, 20, ConfigComponents.clientTitle,
                    btn -> minecraft.setScreen(
                            new ConfigScreen(
                                    this.modId, ConfigComponents.clientTitle, this,
                                    ConfigUtil.toForgeConfigSpec(clientConfig.getSpec()),
                                    clientTracker, entryFactoryMap, true
                            )
                    )
            );

            buttons.add(button);
            addRenderableWidget(button);
        }
        if (commonConfig != null) {
            commonTracker = new ConfigChangeTracker(commonConfig);
            Button button = new Button(
                    center - 50, 0, 100, 20, ConfigComponents.commonTitle,
                    btn -> {
                        minecraft.setScreen(
                                new ConfigScreen(
                                        this.modId, ConfigComponents.commonTitle, this,
                                        ConfigUtil.toForgeConfigSpec(commonConfig.getSpec()),
                                        commonTracker, entryFactoryMap, true
                                )
                        );
                    }
            );

            buttons.add(button);
            addRenderableWidget(button);
        }
        if (serverConfig != null) {
            serverTracker = new RemoteConfigChangeTracker(serverConfig, this.modId);
            Button button = new Button(
                    center - 50, 0, 100, 20, ConfigComponents.serverTitle,
                    btn -> {
                        minecraft.setScreen(
                                new ConfigScreen(
                                        this.modId, ConfigComponents.serverTitle, this,
                                        ConfigUtil.toForgeConfigSpec(serverConfig.getSpec()),
                                        serverTracker, entryFactoryMap, true
                                )
                        );
                    },
                    new Button.OnTooltip() {
                        private static final Component NO_LEVEL = new TranslatableComponent("lapislib.common.config.no_level");
                        private static final Component NO_PERMISSION = new TranslatableComponent("lapislib.common.config.no_permission");

                        @Override
                        public void onTooltip(@Nonnull Button button, @Nonnull PoseStack poseStack, int mouseX, int mouseY) {
                            if (minecraft.level == null) {
                                RootConfigScreen.this.renderTooltip(
                                        poseStack, minecraft.font.split(
                                                NO_LEVEL, Math.max(RootConfigScreen.this.width/2 - 43, 170)
                                        ),
                                        mouseX, mouseY
                                );
                            } else if (minecraft.player != null && !minecraft.player.hasPermissions(2)) {
                                RootConfigScreen.this.renderTooltip(
                                        poseStack, minecraft.font.split(
                                                NO_PERMISSION, Math.max(RootConfigScreen.this.width/2 - 43, 170)
                                        ),
                                        mouseX, mouseY
                                );
                            }
                        }
                    }
            );

            button.active = (minecraft.level != null && minecraft.player != null && minecraft.player.hasPermissions(2));

            buttons.add(button);
            addRenderableWidget(button);
        }

        int yOffset = (this.height/2) - (buttons.size()*24 - 4)/2;
        int btY = 0;

        for (Button button: buttons) {
            button.y = yOffset + btY;
            btY += 24;
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
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

        public ConfigGuiHandler.ConfigGuiFactory getFactory() {
            return new ConfigGuiHandler.ConfigGuiFactory((minecraft1, screen) -> {
                Optional<? extends ModContainer> container = ModList.get().getModContainerById(modId);
                if (container.isEmpty())
                    throw new IllegalStateException("Mod container for " + modId + " does not exist");
                String displayName = container.get().getModInfo().getDisplayName();

                ModConfig client = ConfigUtil.getConfig(modId, ModConfig.Type.CLIENT);
                ModConfig common = ConfigUtil.getConfig(modId, ModConfig.Type.COMMON);
                ModConfig server = ConfigUtil.getConfig(modId, ModConfig.Type.SERVER);

                return new RootConfigScreen(
                        modId, new TranslatableComponent("lapislib.common.config.root_title", displayName),
                        screen, entryMap, client, common, server);
            });
        };
    }
}
