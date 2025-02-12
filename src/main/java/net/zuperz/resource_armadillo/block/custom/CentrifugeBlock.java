package net.zuperz.resource_armadillo.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.zuperz.resource_armadillo.block.entity.custom.CentrifugeBlockEntity;

import javax.annotation.Nullable;

public class CentrifugeBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    static VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 9, 16),

            box(0.5, 9, 0.5, 2.5, 18, 2.5),

            box(0.5, 9, 13.5, 2.5, 18, 15.5),

            box(13.5, 9, 0.5, 15.5, 18, 2.5),

            box(13.5, 9, 13.5, 15.5, 18, 15.5)
    );

    public CentrifugeBlock(Properties properties) {
        super(Properties.of());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CentrifugeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof CentrifugeBlockEntity tile) {
                tile.tick(pos, state, tile);
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof CentrifugeBlockEntity BlockEntity) {

            ItemStack singleStack = pStack.copy();
            singleStack.setCount(1);

            if (!pStack.isEmpty()) {
                for (int i = 0; i < 4; i++) {
                    if (BlockEntity.isInputEmpty(i)) {
                        BlockEntity.setItem(i, singleStack);
                        pStack.shrink(1);
                        pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            } else {
                if (!BlockEntity.isOutputEmpty(0)) {
                    ItemStack stackOnPedestal = BlockEntity.getItem(4);
                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, stackOnPedestal);
                    BlockEntity.clearOutput(0);
                    pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                    return ItemInteractionResult.SUCCESS;
                }

                for (int i = 0; i < 4; i++) {
                    if (!BlockEntity.isInputEmpty(i)) {
                        ItemStack stackOnPedestal = BlockEntity.getItem(i);
                        pPlayer.setItemInHand(InteractionHand.MAIN_HAND, stackOnPedestal);
                        BlockEntity.clearInput(i);
                        pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof CentrifugeBlockEntity furnace) {
                furnace.dropItems();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if(level.getBlockEntity(pos) instanceof CentrifugeBlockEntity CentrifugeBlockEntity && !CentrifugeBlockEntity.getInputItems().getStackInSlot(1).isEmpty()) {
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, CentrifugeBlockEntity.getInputItems().getStackInSlot(1)),
                        0, 0.7, 0, 0.0, 0.0, 0.0);
            }
        }
    }
}