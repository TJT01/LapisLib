package mod.tjt01.lapislib.client.config.screen;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.component.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

public class ConfigScreen extends Screen {
    protected final Screen parent;
    private final String modId;
    private final ForgeConfigSpec spec;
    private final UnmodifiableConfig configGroup;
    private final ConfigChangeTracker tracker;
    protected ConfigList list;

    public ConfigScreen(String modId, Component pTitle, Screen parent, ForgeConfigSpec spec, ConfigChangeTracker tracker) {
        this(modId, pTitle, parent, spec, tracker, spec.getValues());
    }

    public ConfigScreen(
            String modId, Component pTitle,
            Screen parent,
            ForgeConfigSpec spec, ConfigChangeTracker tracker, UnmodifiableConfig configGroup
    ) {
        super(pTitle);
        this.parent = parent;
        this.spec = spec;
        this.tracker = tracker;
        this.configGroup = configGroup;
        this.modId = modId;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {
        int center = this.width/2;

        this.list = new ConfigList(minecraft, this.width, this.height, 32, this.height - 32, 25);

        this.addRenderableWidget(list);

        configGroup.valueMap().forEach((key, obj) -> {
            if (obj instanceof AbstractConfig) {
                Component categoryTitle = new TranslatableComponent("config." + modId + ".category." + key);

                CategoryEntry categoryEntry = new CategoryEntry(
                        this, categoryTitle, screen ->
                        new ConfigScreen(modId, categoryTitle, this, spec, tracker, (UnmodifiableConfig) obj)
                );
                list.addEntry(categoryEntry);
            } else if (obj instanceof ForgeConfigSpec.ConfigValue<?> configValue) {
                ForgeConfigSpec.ValueSpec valueSpec = spec.getRaw(configValue.getPath());
                Object value = configValue.get();
                TranslatableComponent label = new TranslatableComponent(valueSpec.getTranslationKey());

                if (value instanceof Boolean) {
                    list.addEntry(new BooleanConfigEntry(
                            label, tracker, (ForgeConfigSpec.ConfigValue<Boolean>) configValue, valueSpec
                    ));
                } else if (value instanceof Integer) {
                    list.addEntry(new IntConfigEntry(
                            label, tracker, (ForgeConfigSpec.ConfigValue<Integer>) configValue, valueSpec
                    ));
                } else if (value instanceof Long) {
                    list.addEntry(new LongConfigEntry(
                            label, tracker, (ForgeConfigSpec.ConfigValue<Long>) configValue, valueSpec
                    ));
                } else if (value instanceof Float) {
                    list.addEntry(new FloatConfigEntry(
                            label, tracker, (ForgeConfigSpec.ConfigValue<Float>) configValue, valueSpec
                    ));
                } else if (value instanceof Double) {
                    list.addEntry(new DoubleConfigEntry(
                            label, tracker, (ForgeConfigSpec.ConfigValue<Double>) configValue, valueSpec
                    ));
                } else if (value instanceof Enum<?>) {
                    list.addEntry(
                            new EnumEntry(label, tracker, (ForgeConfigSpec.ConfigValue<Enum<?>>)configValue, valueSpec)
                    );
                } else if (value instanceof String) {
                    list.addEntry(new StringConfigEntry(
                            label, tracker, (ForgeConfigSpec.ConfigValue<String>) configValue, valueSpec
                    ));
                } else {
                    LapisLib.LOGGER.warn("No config entry for type {}", value.getClass());
                }
            }
        });

        this.addRenderableWidget(
                new Button(
                        center - 64, this.height - 32, 128, 20, CommonComponents.GUI_DONE,
                        button -> this.getMinecraft().setScreen(parent)
                )
        );
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
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
