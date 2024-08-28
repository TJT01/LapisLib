package mod.tjt01.lapislib.capability;

import mod.tjt01.lapislib.LapisLib;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * A basic {@link net.minecraftforge.items.IItemHandler} implementation
 * <p>
 * Unlike {@link net.minecraftforge.items.ItemStackHandler}, this does not support changing size
 * after creation, and does not store it in NBT.
 */
public class SimpleItemStackHandler implements IItemHandlerModifiable, INBTSerializable<ListTag> {
    protected final NonNullList<ItemStack> stacks;

    public SimpleItemStackHandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public void onLoad() {}
    public void onContentsChanged() {}

    protected void checkSlotInBounds(int idx) {
        if (idx < 0 || idx >= this.stacks.size()) throw new IndexOutOfBoundsException(idx);
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public ListTag serializeNBT() {
        ListTag list = new ListTag();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack item = stacks.get(i);
            if (!item.isEmpty()) {
                CompoundTag tag = item.serializeNBT();
                tag.putInt("Slot", i);
                list.add(tag);
            }
        }

        return list;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            CompoundTag tag = nbt.getCompound(i);
            if (tag.contains("Slot")) {
                this.stacks.set(tag.getInt("Slot"), ItemStack.of(tag));
            } else {
                LapisLib.LOGGER.warn("Skipping invalid slot entry: {}", tag.toString());
            }
        }

        this.onLoad();
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        checkSlotInBounds(slot);
        this.stacks.set(slot, stack);
        this.onContentsChanged();
    }

    @Override
    public int getSlots() {
        return this.stacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!isItemValid(slot, stack)) return stack;

        checkSlotInBounds(slot);

        ItemStack existing = stacks.get(slot);
        int limit = this.getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(existing, stack)) return stack;
            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
        }

        return reachedLimit
                ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit)
                : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;

        checkSlotInBounds(slot);

        ItemStack existing = stacks.get(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int extract = Math.min(amount, existing.getMaxStackSize());

        if (extract >= existing.getCount()) {
            if (simulate) {
                return existing.copy();
            } else {
                stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged();
                return existing;
            }
        } else {
            ItemStack ret = ItemHandlerHelper.copyStackWithSize(existing, extract);

            if (!simulate) {
                existing.shrink(extract);
                onContentsChanged();
            }

            return ret;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
