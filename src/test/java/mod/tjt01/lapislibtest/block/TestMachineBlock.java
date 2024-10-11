package mod.tjt01.lapislibtest.block;

import mod.tjt01.lapislibtest.block.entity.LapisLibTestBlockEntityTypes;
import mod.tjt01.lapislibtest.block.entity.TestMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class TestMachineBlock extends BaseEntityBlock {
    protected TestMachineBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestMachineBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return !level.isClientSide
                ? createTickerHelper(
                        blockEntityType,
                        LapisLibTestBlockEntityTypes.MACHINE.get(),
                        TestMachineBlockEntity::tick
                ) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TestMachineBlockEntity machine) {
                machine.setCustomName(stack.getHoverName());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection()))
            return InteractionResult.sidedSuccess(level.isClientSide);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else if (player instanceof ServerPlayer serverPlayer) {
            TestMachineBlockEntity blockEntity = level.getBlockEntity(pos, LapisLibTestBlockEntityTypes.MACHINE.get()).orElseThrow();
            NetworkHooks.openScreen(
                    serverPlayer,
                    blockEntity,
                    buf -> {
                        buf.writeBlockPos(pos);
                        buf.writeFluidStack(blockEntity.fluid.getFluid());
                    }
            );
            return InteractionResult.CONSUME;
        } else {
            throw new IllegalStateException();
        }
    }
}
