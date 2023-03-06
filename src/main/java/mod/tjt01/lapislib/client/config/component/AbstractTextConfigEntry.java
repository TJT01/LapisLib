package mod.tjt01.lapislib.client.config.component;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractTextConfigEntry<T> extends LabeledConfigEntry {
    protected final EditBox textBox;
    protected final ConfigChangeTracker tracker;
    protected final ImmutableList<EditBox> textBoxes;
    protected final String path;
    protected final ForgeConfigSpec.ConfigValue<T> configValue;
    protected final ForgeConfigSpec.ValueSpec valueSpec;
    protected boolean isStringValid = true;

    public AbstractTextConfigEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<T> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label);
        this.tracker = tracker;
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.valueSpec = valueSpec;

        this.textBox = new EditBox(
                Minecraft.getInstance().font,
                0, 0, 110, 20,
                CommonComponents.GUI_YES
        );
        textBox.setValue(getString(tracker.getValue(path, configValue)));
        this.textBox.setResponder(this::onChanged);
        textBoxes = ImmutableList.of(textBox);
    }

    protected String getString(T value) {
        return value.toString();
    }

    protected void set(T value) {
        tracker.setValue(path, configValue, value);
    }

    @Nullable
    protected abstract T fromString(String text);

    protected void checkValid() {
        boolean valid = this.isStringValid && isValid();
        this.textBox.setTextColor(valid ? 0xFFFFFFFF : 0xFFFF0000);
    }

    protected boolean isValid() {
        return true;
    }

    protected void onChanged(String text) {
        T value = fromString(text);
        if (value == null) {
            this.isStringValid = false;
        } else {
            this.isStringValid = true;
            set(value);
        }
        checkValid();
    }

    @Override
    public boolean changeFocus(boolean pFocus) {
        LapisLib.LOGGER.debug("changeFocus {}", pFocus);
        return super.changeFocus(pFocus);
    }

    @Override
    public void tick() {
        textBox.tick();
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        textBox.x = left + 220 - 110;
        textBox.y = top;
        textBox.render(poseStack, mouseX, mouseY, pPartialTick);
    }

    @Nonnull
    @Override
    public List<? extends NarratableEntry> narratables() {
        return textBoxes;
    }

    @Nonnull
    @Override
    public List<? extends GuiEventListener> children() {
        return textBoxes;
    }
}
