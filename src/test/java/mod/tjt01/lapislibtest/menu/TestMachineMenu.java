package mod.tjt01.lapislibtest.menu;

import mod.tjt01.lapislibtest.block.entity.LapisLibTestBlockEntityTypes;
import mod.tjt01.lapislibtest.block.entity.TestMachineBlockEntity;
import mod.tjt01.lapislibtest.menu.slot.TestMachineResultSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

public class TestMachineMenu extends AbstractContainerMenu {
    private static final int INVENTORY_START = 2;
    private static final int HOTBAR_START = INVENTORY_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final ContainerLevelAccess access;
    public final TestMachineBlockEntity blockEntity;

    public FluidStack fluid;

    public static TestMachineMenu create(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        FluidStack fluid = data.readFluidStack();
        TestMachineBlockEntity blockEntity = playerInventory.player.level
                .getBlockEntity(pos, LapisLibTestBlockEntityTypes.MACHINE.get()).orElseThrow();
        return new TestMachineMenu(containerId, playerInventory, blockEntity, fluid);
    }

    public TestMachineMenu(int containerId, Inventory playerInventory, TestMachineBlockEntity blockEntity, FluidStack fluidStack) {
        super(LapisLibTestMenus.MACHINE_MENU.get(), containerId);
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.blockEntity = blockEntity;
        this.fluid = fluidStack;

        this.addDataSlots(blockEntity.data);
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 0, 56, 35));
        this.addSlot(new TestMachineResultSlot(playerInventory.player, blockEntity, blockEntity.inventory, 1, 116, 35));

        //inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                int i = y*9 + x + 9;
                this.addSlot(new Slot(playerInventory, i, x*18 + 8, y*18 + 84));
            }
        }

        //hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i*18, 142));
        }

    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack inSlot = slot.getItem();
            stack = inSlot.copy();
            if (index >= INVENTORY_START && index < HOTBAR_END) {
                if (!this.moveItemStackTo(inSlot, 0, 1, false)) {
                    if (index < HOTBAR_START) {
                        if (!this.moveItemStackTo(inSlot, HOTBAR_START, HOTBAR_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(inSlot, INVENTORY_START, HOTBAR_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(inSlot, INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }

            if (inSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (inSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, inSlot);
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, blockEntity.getBlockState().getBlock());
    }
}
