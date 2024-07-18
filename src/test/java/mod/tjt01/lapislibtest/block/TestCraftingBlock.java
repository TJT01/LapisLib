package mod.tjt01.lapislibtest.block;

import mod.tjt01.lapislibtest.menu.TestCraftingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class TestCraftingBlock extends Block{
    private static final Component TITLE = Component.translatable("container.lapislib_test.test_crafting");

    protected TestCraftingBlock(Properties pProperties) {
        super(pProperties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else if (pPlayer instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Nonnull
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return new TestCraftingMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pLevel, pPos));
                }

                @Nonnull
                @Override
                public Component getDisplayName() {
                    return TITLE;
                }
            }, pPos);
            return InteractionResult.CONSUME;
        } else {
            throw new IllegalStateException();
        }
    }
}
