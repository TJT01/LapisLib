package mod.tjt01.lapislib.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SingleItemHandler implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    public ItemStack stack;

    public SingleItemHandler() {
        this(ItemStack.EMPTY);
    }

    public SingleItemHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.stack = stack;
        this.onContentsChanged();
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        int limit = getStackLimit(slot, stack);

        if (!this.stack.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(this.stack, stack))
                return stack;

            limit -= this.stack.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (this.stack.isEmpty())
                this.stack = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
            else
                this.stack.grow(reachedLimit ? limit : stack.getCount());
            onContentsChanged();
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        if (this.stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack existing = this.stack;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (this.stack.getCount() <= toExtract) {
            if (!simulate) {
                this.stack = ItemStack.EMPTY;
                onContentsChanged();
                return existing;
            } else
                return existing.copy();
        } else {
            if (!simulate) {
                this.stack = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
                onContentsChanged();
            }
            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.stack.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.stack = ItemStack.of(nbt);
        this.onLoad();
    }

    public void onContentsChanged() {

    }

    public void onLoad() {

    }

}
