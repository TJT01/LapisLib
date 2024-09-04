package mod.tjt01.lapislib.client.config.screen;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.component.CategoryEntry;
import mod.tjt01.lapislib.client.config.component.ConfigList;
import mod.tjt01.lapislib.client.config.factory.ConfigEntryFactory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ConfigScreen extends Screen {
    protected final Screen parent;
    private final ModConfig modConfig;
    private final ForgeConfigSpec spec;
    private final UnmodifiableConfig configGroup;
    private final ConfigChangeTracker tracker;
    private final Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> valueFactoryMap;
    protected ConfigList list;
    protected final boolean isRoot;

    public ConfigScreen(
            ModConfig modConfig, Component pTitle, Screen parent, ForgeConfigSpec spec, ConfigChangeTracker tracker,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> valueFactoryMap
    ) {
        this(modConfig, pTitle, parent, spec, tracker, valueFactoryMap, false);
    }

    public ConfigScreen(
            ModConfig modConfig, Component pTitle, Screen parent, ForgeConfigSpec spec, ConfigChangeTracker tracker,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> valueFactoryMap, boolean isRoot
    ) {
        this(modConfig, pTitle, parent, spec, tracker, spec.getValues(), valueFactoryMap, isRoot);
    }

    public ConfigScreen(
            ModConfig modConfig, Component pTitle,
            Screen parent,
            ForgeConfigSpec spec, ConfigChangeTracker tracker, UnmodifiableConfig configGroup,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> valueFactoryMap
    ) {
        this(modConfig, pTitle, parent, spec, tracker, configGroup, valueFactoryMap, false);
    }

    public ConfigScreen(
            ModConfig modConfig, Component pTitle,
            Screen parent,
            ForgeConfigSpec spec, ConfigChangeTracker tracker, UnmodifiableConfig configGroup,
            Map<ForgeConfigSpec.ConfigValue<?>, ConfigEntryFactory> valueFactoryMap, boolean isRoot
    ) {
        super(pTitle);
        this.parent = parent;
        this.spec = spec;
        this.tracker = tracker;
        this.configGroup = configGroup;
        this.modConfig = modConfig;
        this.valueFactoryMap = valueFactoryMap;
        this.isRoot = isRoot;
    }

    @Override
    protected void init() {
        int center = this.width/2;

        this.list = new ConfigList(minecraft, this.width, this.height, 32, this.height - 32, 25);

        this.addWidget(list);

        configGroup.valueMap().forEach((key, obj) -> {
            if (obj instanceof AbstractConfig) {
                Component categoryTitle = Component.translatable("config." + modConfig.getModId() + ".category." + key);

                CategoryEntry categoryEntry = new CategoryEntry(
                        this, categoryTitle, screen ->
                        new ConfigScreen(
                                modConfig, categoryTitle, this, spec, tracker, (UnmodifiableConfig) obj,
                                this.valueFactoryMap
                        )
                );
                list.addEntry(categoryEntry);
            } else if (obj instanceof ForgeConfigSpec.ConfigValue<?> configValue) {
                ForgeConfigSpec.ValueSpec valueSpec = spec.getRaw(configValue.getPath());
                list.addEntry(
                        valueFactoryMap.getOrDefault(configValue, ConfigEntryFactory.DefaultConfigEntryFactory.INSTANCE)
                                .make(configValue, valueSpec, this, tracker)
                );
            }
        });

        if (isRoot) {
            this.addRenderableWidget(
                    new Button(
                            center - 132, this.height - 26, 128, 20, CommonComponents.GUI_CANCEL,
                            button -> this.getMinecraft().setScreen(parent)
                    )
            );
            this.addRenderableWidget(
                    new Button(
                            center + 8, this.height - 26, 128, 20, CommonComponents.GUI_DONE,
                            button -> {
                                this.tracker.save();
                                this.getMinecraft().setScreen(parent);
                            }
                    )
            );
        } else {
            this.addRenderableWidget(
                    new Button(
                            center - 64, this.height - 26, 128, 20, CommonComponents.GUI_DONE,
                            button -> this.getMinecraft().setScreen(parent)
                    )
            );
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, mouseX, mouseY, partialTick);
        drawCenteredString(poseStack, font, this.title, this.width/2, 13, 0xFFFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTick);
        List<FormattedCharSequence> tooltip = this.list.getTooltip(mouseX, mouseY);
        if (!tooltip.isEmpty()) {
            this.renderTooltip(poseStack, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        list.tick();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.getMinecraft().setScreen(this.parent);
    }
}
