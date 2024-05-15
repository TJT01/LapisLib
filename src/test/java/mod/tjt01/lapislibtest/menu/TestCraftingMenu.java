package mod.tjt01.lapislibtest.menu;

import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeTypes;
import mod.tjt01.lapislibtest.data.recipe.TestRecipe;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public class TestCraftingMenu extends AbstractContainerMenu {
    private static final int RESULT_SLOT_IDX = 2;
    private static final int INVENTORY_START = 3;
    private static final int HOTBAR_START = INVENTORY_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final ContainerLevelAccess levelAccess;
    private final Player player;
    private final SimpleContainer craftingContainer = new SimpleContainer(2);
    private final ResultContainer resultContainer = new ResultContainer();

    public TestCraftingMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public TestCraftingMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess containerLevelAccess) {
        super(LapisLibTestMenus.CRAFTING_MENU.get(), pContainerId);

        craftingContainer.addListener(this::slotsChanged);

        levelAccess = containerLevelAccess;
        this.player = pPlayerInventory.player;

        this.addSlot(new Slot(craftingContainer, 0, 53, 18));
        this.addSlot(new Slot(craftingContainer, 1, 53, 54));

        this.addSlot(new TestResultSlot(player, resultContainer, craftingContainer, 0, 111, 35));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                int i = y*9 + x + 9;
                this.addSlot(new Slot(pPlayerInventory, i, x*18 + 8, y*18 + 84));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(pPlayerInventory, i, 8 + i*18, 142));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, craftingContainer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack inSlot = slot.getItem();
            stack = inSlot.copy();
            if (pIndex == RESULT_SLOT_IDX) { //from Result Slot
                levelAccess.execute((level, pos) -> {
                    inSlot.getItem().onCraftedBy(inSlot, level, pPlayer);
                });
                if (!this.moveItemStackTo(inSlot, INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(inSlot, stack);
            } else if (pIndex >= INVENTORY_START && pIndex < HOTBAR_END) { // from Inventory
                if (!this.moveItemStackTo(inSlot, 0, 2, false)) {
                    if (pIndex < HOTBAR_START) {
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

            slot.onTake(pPlayer, inSlot);
            if (pIndex == 2){
                pPlayer.drop(inSlot, false);
            }
        }

        return stack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        return pSlot.container != this.resultContainer && super.canTakeItemForPickAll(pStack, pSlot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, LapisLibTestBlocks.CRAFTING_BLOCK.get());//TODO
    }

    @Override
    public void slotsChanged(Container pContainer) {
        TestCraftingMenu.this.levelAccess.execute((level, pos) -> {
            if (!level.isClientSide()) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                RecipeManager manager = level.getRecipeManager();
                ItemStack stack = ItemStack.EMPTY;
                Optional<TestRecipe> recipeOptional = manager.getRecipeFor(LapisLibTestRecipeTypes.TEST.get(), craftingContainer, level);
                if (recipeOptional.isPresent()) {
                    TestRecipe recipe = recipeOptional.get();
                    if (resultContainer.setRecipeUsed(level, serverPlayer, recipe)) {
                        stack = recipe.assemble(craftingContainer);
                    }
                }
                resultContainer.setItem(0, stack);
                TestCraftingMenu.this.setRemoteSlot(RESULT_SLOT_IDX, stack);

                serverPlayer.connection.send(
                        new ClientboundContainerSetSlotPacket(
                                TestCraftingMenu.this.containerId,
                                TestCraftingMenu.this.incrementStateId(),
                                RESULT_SLOT_IDX,
                                stack
                        )
                );
            }
        });
        super.slotsChanged(pContainer);
    }

    public static class TestResultSlot extends Slot {
        private final Player player;
        private int removeCount = 0;
        private final SimpleContainer grid;

        public TestResultSlot(Player player, Container pContainer, SimpleContainer grid, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
            this.player = player;
            this.grid = grid;
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            checkTakeAchievements(pStack);
            Optional<TestRecipe> testRecipe = pPlayer.level.getRecipeManager().getRecipeFor(
                    LapisLibTestRecipeTypes.TEST.get(),
                    grid,
                    pPlayer.level
            );
            if (testRecipe.isPresent()) {
                testRecipe.get().removeItems(pPlayer, grid);
            } else {
                LapisLib.LOGGER.warn("Cannot find a recipe!");
            }
            grid.setChanged();
            super.onTake(pPlayer, pStack);
        }

        @Override
        protected void onQuickCraft(ItemStack pStack, int pAmount) {
            removeCount += pAmount;
            super.onQuickCraft(pStack, pAmount);
        }

        @Override
        public ItemStack remove(int pAmount) {
            if (this.hasItem()) {
                this.removeCount += Math.min(pAmount, this.getItem().getCount());
            }

            return super.remove(pAmount);
        }

        @Override
        protected void checkTakeAchievements(ItemStack pStack) {
            if (this.removeCount > 0) {
                pStack.onCraftedBy(player.level, player, removeCount);
            }

            if (container instanceof ResultContainer resultContainer) {
                resultContainer.awardUsedRecipes(player);
            }
            removeCount = 0;
            super.checkTakeAchievements(pStack);
        }
    }
}
