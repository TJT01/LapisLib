package mod.tjt01.lapislib.client.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import mod.tjt01.lapislib.client.config.component.ListConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ListEditScreen extends Screen {
    protected final Screen parent;
    protected final ListConfigEntry.ListType type;
    protected final ConfigChangeTracker tracker;
    protected final ForgeConfigSpec.ConfigValue<List<?>> configValue;
    protected final ForgeConfigSpec.ValueSpec spec;
    protected final String path;
    protected final boolean canBeEmpty;

    protected final LinkedList<Object> objects = new LinkedList<>();
    protected Button saveButton;

    protected EntryList entryList;

    public ListEditScreen(
            Component pTitle,
            ConfigChangeTracker tracker,
            ForgeConfigSpec.ConfigValue<List<?>> configValue, ForgeConfigSpec.ValueSpec spec,
            ListConfigEntry.ListType type,
            Screen parent
    ) {
        super(pTitle);

        if (type == ListConfigEntry.ListType.UNKNOWN) throw new IllegalArgumentException("Unknown list type");

        this.path = String.join(".", configValue.getPath());
        this.type = type;
        this.parent = parent;
        this.configValue = configValue;
        this.tracker = tracker;
        this.spec = spec;

        this.canBeEmpty = spec.test(Collections.EMPTY_LIST);

        objects.addAll(tracker.getValue(path, configValue));
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    protected void populateList() {
        LinkedList<Entry> entries = new LinkedList<>();
        List<?> list = objects;
        for (int k = 0; k < list.size(); k++){
            Object o = list.get(k);
            Entry entry;
            if (o instanceof String s) {
                entry = new StringItemEntry(s, k, this);
            } else if (o instanceof Integer i) {
                entry = new IntegerItemEntry(i, k, this);
            } else if (o instanceof Long l) {
                entry = new LongItemEntry(l, k, this);
            } else if (o instanceof Float f) {
                entry = new FloatItemEntry(f, k, this);
            } else if (o instanceof Double d) {
                entry = new DoubleItemEntry(d, k, this);
            } else if (o instanceof Boolean b) {
                entry = new BooleanEntry(b, k, this);
            } else {
                throw new IllegalStateException("Unsupported " + o.getClass().toString());
            }
            entries.add(entry);
        }
        entries.add(new AddItemEntry(this));
        entryList.replaceEntries(entries);
    }

    public boolean save() {
        List<?> list = this.tracker.getValue(this.path, this.configValue);
        if (list instanceof ArrayList<?>) {
            this.tracker.setValue(path, this.configValue, new ArrayList<>(this.objects));
            return true;
        } else if (list instanceof LinkedList<?>) {
            this.tracker.setValue(path, this.configValue, new LinkedList<>(this.objects));
            return true;
        }
        LapisLib.LOGGER.warn("Unsupported list of type {}", list.getClass());
        return false;
    }

    public Object getDefaultObject() {
        return switch (this.type) {
            case STRING -> "";
            case INTEGER -> 0;
            case LONG -> 0L;
            case FLOAT -> 0.0F;
            case DOUBLE -> 0.0D;
            case BOOLEAN -> false;
            case UNKNOWN -> throw new IllegalStateException();
        };
    }

    public void addItem() {
        objects.add(getDefaultObject());
        populateList();
    }

    public boolean moveItemForward(int index) {
        if (index < this.objects.size() - 1) {
            Object o = objects.get(index);
            objects.set(index, objects.get(index + 1));
            objects.set(index + 1, o);
            populateList();
            return true;
        }
        return false;
    }

    public boolean moveItemBack(int index) {
        if (index > 0) {
            return moveItemForward(index - 1);
        }
        return false;
    }

    public void setItem(int index, Object element) {
        objects.set(index, element);
    }

    public boolean canRemoveItems() {
        return this.canBeEmpty || objects.size() >= 2;
    }

    public void removeItem(int index) {
        if (canRemoveItems()) {
            this.objects.remove(index);
            populateList();
        }
    }

    @Override
    public void tick() {
        if (entryList != null) {
            this.entryList.tick();
        }
    }

    @Override
    protected void init() {
        this.entryList = new EntryList(this.getMinecraft(), this.width, this.height, 32, this.height - 32, 24);
        this.saveButton = new Button(this.width/2 - 64, this.height - 26, 128, 20, CommonComponents.GUI_DONE, pButton -> {
            save();
            this.getMinecraft().setScreen(this.parent);
        });

        this.addRenderableWidget(entryList);
        this.addRenderableWidget(saveButton);

        populateList();
    }

    @Override
    public void onClose() {
        super.onClose();
        save();
        this.getMinecraft().setScreen(parent);
    }

    public static class EntryList extends ContainerObjectSelectionList<Entry> {
        public EntryList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height, y0, y1, itemHeight);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ListEditScreen.Entry mouseOver = this.getEntryAtPosition(mouseX, mouseY);
            for (ListEditScreen.Entry entry: this.children()) {
                if (entry != mouseOver) entry.mouseClicked(mouseX, mouseY, button);
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected void replaceEntries(Collection<ListEditScreen.Entry> pEntries) {
            super.replaceEntries(pEntries);
        }

        public void tick() {
            for (ListEditScreen.Entry entry: this.children()) {
                entry.tick();
            }
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        public static final Style DELETE_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);

        public final LinkedList<Widget> widgets = new LinkedList<>();
        public final LinkedList<NarratableEntry> narratables = new LinkedList<>();
        public final LinkedList<GuiEventListener> guiEventListeners = new LinkedList<>();

        public void addRenderableWidget(Widget widget) {
            this.widgets.add(widget);
            if (widget instanceof NarratableEntry entry) {
                this.narratables.add(entry);
            }
            if (widget instanceof GuiEventListener listener) {
                this.guiEventListeners.add(listener);
            }
        }

        @Nonnull
        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.narratables;
        }

        @Override
        public void render(
                @Nonnull PoseStack poseStack, int index,
                int top, int left, int width, int height,
                int mouseX, int mouseY, boolean isMouseOver,
                float partialTick
        ) {
            for (Widget widget: this.widgets) {
                widget.render(poseStack, mouseX, mouseY, partialTick);
            }
        }

        public void tick() {}

        @Nonnull
        @Override
        public List<? extends GuiEventListener> children() {
            return this.guiEventListeners;
        }
    }

    public static class AddItemEntry extends Entry {
        public static final Style DELETE_STYLE = Style.EMPTY.withColor(ChatFormatting.GREEN);
        public static final TextComponent LABEL = new TextComponent("+");
        private final ListEditScreen parent;
        private final Button button;

        public AddItemEntry(ListEditScreen parent) {
            this.parent = parent;
            this.button = new Button(0, 0, 128, 20, LABEL, btn -> parent.addItem());
            this.addRenderableWidget(button);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            button.x = left + width/2 - 64;
            button.y = top;

            super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
        }
    }

    public abstract static class ListItemEntry extends Entry {
        public static final Style DELETE_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
        public final Button deleteButton;
        public final Button upButton;
        public final Button downButton;

        public final int index;

        public final Object object;

        public final ListEditScreen parent;

        public ListItemEntry(Object o, int index, ListEditScreen parent) {
            this.object = o;
            this.index = index;
            this.parent = parent;

            this.deleteButton = new Button(0, 0, 20, 20, new TextComponent("-").withStyle(DELETE_STYLE), button -> {
                this.parent.removeItem(index);
            });
            this.upButton = new Button(0, 0, 20, 20, new TextComponent("^"), button -> {
                this.parent.moveItemBack(index);
            });
            this.downButton = new Button(0, 0, 20, 20, new TextComponent("v"), button -> {
                this.parent.moveItemForward(index);
            });

            if (parent.canRemoveItems()) this.addRenderableWidget(deleteButton);
            if (index > 0) this.addRenderableWidget(upButton);
            if (index < parent.objects.size() - 1) this.addRenderableWidget(downButton);
        }

        @Nonnull
        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.narratables;
        }

        @Override
        public void render(
                @Nonnull PoseStack poseStack, int index,
                int top, int left, int width, int height,
                int mouseX, int mouseY, boolean isMouseOver,
                float partialTick
        ) {
            this.deleteButton.x = left + width - 20;
            this.deleteButton.y = top;
            this.upButton.x = left + width - 60;
            this.upButton.y = top;
            this.downButton.x = left + width - 40;
            this.downButton.y = top;

            drawString(poseStack, Minecraft.getInstance().font, "#" + index, left, top + 10, 0xFFFFFFFF);
            super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
        }

        public void tick() {}

        @Nonnull
        @Override
        public List<? extends GuiEventListener> children() {
            return this.guiEventListeners;
        }
    }

    public abstract static class TextboxItemEntry extends ListItemEntry {
        public final EditBox editBox;

        @Nullable
        protected abstract Object fromString(String s);

        public TextboxItemEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
            this.editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 98, 18, TextComponent.EMPTY);
            editBox.setValue(o.toString());
            editBox.setResponder(s -> {
                Object obj = fromString(s);
                if (obj == null) {
                    editBox.setTextColor(0xFFFF0000);
                } else {
                    editBox.setTextColor(0xFFFFFFFF);
                    this.parent.setItem(this.index, obj);
                }
            });

            this.addRenderableWidget(editBox);
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            this.editBox.x = left + width - (80 + 100);
            this.editBox.y = top + 1;
            super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
        }

        @Override
        public void tick() {
            this.editBox.tick();
        }
    }

    public static class StringItemEntry extends TextboxItemEntry {

        public StringItemEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
        }

        @Override
        protected Object fromString(String s) {
            return s;
        }
    }

    public static class IntegerItemEntry extends TextboxItemEntry {

        public IntegerItemEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
        }

        @Override
        protected Object fromString(String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    }

    public static class LongItemEntry extends TextboxItemEntry {

        public LongItemEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
        }

        @Override
        protected Object fromString(String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    }

    public static class FloatItemEntry extends TextboxItemEntry {

        public FloatItemEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
        }

        @Override
        protected Object fromString(String s) {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    }

    public static class DoubleItemEntry extends TextboxItemEntry {

        public DoubleItemEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
        }

        @Override
        protected Object fromString(String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    }

    public static class BooleanEntry extends ListItemEntry {
        public static final class CheckboxWidget extends Checkbox {
            private final ListEditScreen parent;
            private final int index;
            public CheckboxWidget(
                    ListEditScreen parent, int index, int x, int y, int width, int height, Component message, boolean selected, boolean showLabel
            ) {
                super(x, y, width, height, message, selected, showLabel);
                this.parent = parent;
                this.index = index;
            }

            @Override
            public void onPress() {
                super.onPress();
                this.parent.setItem(this.index, this.selected());
            }
        }

        protected final Checkbox checkbox;
        public BooleanEntry(Object o, int index, ListEditScreen parent) {
            super(o, index, parent);
            boolean b = false;
            if (o instanceof Boolean b2) b = b2;
            checkbox = new CheckboxWidget(parent, index, 0, 20, 20, 20, TextComponent.EMPTY, b, false);
            this.addRenderableWidget(checkbox);
        }

        @Override
        public void render(
                @Nonnull PoseStack poseStack, int index,
                int top, int left, int width, int height,
                int mouseX, int mouseY, boolean isMouseOver,
                float partialTick
        ) {
            this.checkbox.x = left + (width/2) - 10;
            this.checkbox.y = top;
            super.render(poseStack, index, top, left, width, height, mouseX, mouseY, isMouseOver, partialTick);
        }
    }
}
