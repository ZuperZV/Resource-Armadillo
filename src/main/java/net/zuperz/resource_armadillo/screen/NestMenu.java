package net.zuperz.resource_armadillo.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.block.entity.custom.NestBlockEntity;
import net.zuperz.resource_armadillo.block.entity.custom.NestBlockEntity;
import net.zuperz.resource_armadillo.screen.slot.ModMenuTypes;

public class NestMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    public final NestBlockEntity blockentity;
    public NestMenu(int containerId, Player player, BlockPos pos) {
        super(ModMenuTypes.NEST_MENU.get(), containerId);
        NestBlockEntity NestBlockEntity;
        this.pos = pos;

        if (player.level().getBlockEntity(pos) instanceof NestBlockEntity blockentity) {
            NestBlockEntity = blockentity;
            this.addDataSlots(blockentity.data);

            addSlot(new SlotItemHandler(blockentity.getInputItems(), 0, 45, 52));
            addSlot(new SlotItemHandler(blockentity.getOutputItems(), 0, 117, 39) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });

        } else {
            NestBlockEntity = null;
            System.err.println("Invalid block entity at position: " + pos);
        }

        addPlayerInventory(player.getInventory());
        addPlayerHotbar(player.getInventory());

        this.blockentity = NestBlockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockentity.getLevel(), blockentity.getBlockPos()), player, ModBlocks.NEST.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMovedStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack rawStack = slot.getItem();
            quickMovedStack = rawStack.copy();

            if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(rawStack, 30, 38, false) &&
                        !this.moveItemStackTo(rawStack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 3 && index < 38) {
                if (!this.moveItemStackTo(rawStack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (rawStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, rawStack);
        }

        return quickMovedStack;
    }

    public boolean isCrafting() {
        return blockentity.getProgress() > 0;
    }

    public int getScaledProgress() {
        int progress = blockentity.data.get(0);
        int maxProgress = blockentity.data.get(1);
        int progressArrowSize = 22;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}