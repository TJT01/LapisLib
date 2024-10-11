package mod.tjt01.lapislibtest.menu.slot;

import mod.tjt01.lapislibtest.block.entity.TestMachineBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class TestMachineResultSlot extends SlotItemHandler {
    private final Player player;
    private final TestMachineBlockEntity parent;
    private int removeCount = 0;

    public TestMachineResultSlot(Player player, TestMachineBlockEntity parent, IItemHandlerModifiable handler, int slot, int x, int y) {
        super(handler, slot, x, y);
        this.player = player;
        this.parent = parent;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level, this.player, this.removeCount);

        if (this.player instanceof ServerPlayer serverPlayer) {
            parent.awardUsedRecipes(serverPlayer);
        }

        this.removeCount = 0;
    }
}
