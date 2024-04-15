package mod.tjt01.lapislib.client.config.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractTextConfigEntry<T> extends AbstractForgeConfigEntry<T> {
    protected final EditBox textBox;
    protected boolean isStringValid = true;

    public AbstractTextConfigEntry(
            Component label, ConfigChangeTracker tracker, ForgeConfigSpec.ConfigValue<T> configValue,
            ForgeConfigSpec.ValueSpec valueSpec
    ) {
        super(label, tracker, configValue, valueSpec);

        this.textBox = new EditBox(
                Minecraft.getInstance().font,
                0, 0, 108, 18,
                CommonComponents.GUI_YES
        );
        textBox.setValue(getString(tracker.getValue(path, configValue)));
        this.textBox.setResponder(this::onChanged);

        this.widgets.add(textBox);
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
        this.textBox.setTextColor(isValid() ? 0xFFFFFFFF : 0xFFFF0000);
    }

    @Override
    public void onResetOrUndo() {
        onChanged(getString(tracker.getValue(this.path, this.configValue)), false);
    }

    @Override
    public boolean isValid() {
        return this.isStringValid;
    }

    protected void onChanged(String text) {
        onChanged(text, true);
    }

    protected void onChanged(String text, boolean setValue) {
        T value = fromString(text);
        if (value == null) {
            this.isStringValid = false;
        } else {
            this.isStringValid = true;
            if (setValue) set(value);
        }
        checkValid();
    }

    @Override
    public void tick() {
        textBox.tick();
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float pPartialTick) {
        super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, pPartialTick);
        textBox.x = left + width - 149;
        textBox.y = top + 1;
        textBox.render(poseStack, mouseX, mouseY, pPartialTick);
    }
}
