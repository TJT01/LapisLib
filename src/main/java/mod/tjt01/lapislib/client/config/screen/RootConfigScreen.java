package mod.tjt01.lapislib.client.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.ConfigComponents;
import mod.tjt01.lapislib.client.config.factory.ColorConfigFactory;
import mod.tjt01.lapislib.client.config.factory.ConfigEntryFactory;
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
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RootConfigScreen extends Screen {
    private final Screen parent;
    private final String modId;

    @Nullable
    private final ForgeConfigSpec clientConfig;
    @Nullable
    private final ForgeConfigSpec commonConfig;
    @Nullable
    private ConfigChangeTracker clientTracker;
    @Nullable
    private ConfigChangeTracker commonTracker;

    private final Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryFactoryMap;

    public static ConfigScreenBuilder builder(String modId) {
        return new ConfigScreenBuilder(modId);
    }

    private RootConfigScreen(
            String modId, Component pTitle, Screen parent,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> entryFactoryMap,
            @Nullable ForgeConfigSpec clientConfig, @Nullable ForgeConfigSpec commonConfig
    ) {
        super(pTitle);
        this.parent = parent;
        this.entryFactoryMap = entryFactoryMap;
        this.clientConfig = clientConfig;
        this.commonConfig = commonConfig;
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
                                    this.modId, ConfigComponents.clientTitle, this, clientConfig,
                                    clientTracker, entryFactoryMap
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
                        LapisLib.LOGGER.debug(minecraft.level);
                        minecraft.setScreen(
                                new ConfigScreen(
                                        this.modId, ConfigComponents.commonTitle, this, commonConfig,
                                        commonTracker, entryFactoryMap
                                )
                        );
                    }
            );

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
        @Nullable
        private ForgeConfigSpec clientSpec;
        @Nullable
        private ForgeConfigSpec commonSpec;
        @Nullable
        private ForgeConfigSpec serverSpec;

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

        public ConfigScreenBuilder client(ForgeConfigSpec spec) {
            if (clientSpec != null) {
                throw new IllegalStateException("Client config spec was already defined");
            }
            clientSpec = spec;
            return this;
        }

        public ConfigScreenBuilder common(ForgeConfigSpec spec) {
            if (commonSpec != null) {
                throw new IllegalStateException("Common config spec was already defined");
            }
            commonSpec = spec;
            return this;
        }

        public ConfigScreenBuilder server(ForgeConfigSpec spec) {
//            if (serverSpec != null) {
//                throw new IllegalStateException("Server config spec was already defined");
//            }
//            serverSpec = spec;
//            return this;
            //TODO implement
            throw new NotImplementedException("Server config gui not yet implemented");
        }

        public ConfigGuiHandler.ConfigGuiFactory getFactory() {
            return new ConfigGuiHandler.ConfigGuiFactory((minecraft1, screen) -> {
                Optional<? extends ModContainer> container = ModList.get().getModContainerById(modId);
                if (container.isEmpty())
                    throw new IllegalStateException("Mod container for " + modId + " does not exist");
                String displayName = container.get().getModInfo().getDisplayName();

                if (clientSpec == null && commonSpec == null)
                    throw new IllegalStateException("No config specs have been defined");

                return new RootConfigScreen(
                        modId, new TranslatableComponent("lapislib.common.config.root_title", displayName),
                        screen, entryMap, clientSpec, commonSpec
                );
            });
        };
    }
}
