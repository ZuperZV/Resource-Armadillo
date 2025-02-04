package net.zuperz.resource_armadillo.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.block.entity.custom.RoostBlockEntity;
import net.zuperz.resource_armadillo.screen.slot.FluidContainerSlot;
import net.zuperz.resource_armadillo.screen.slot.ModMenuTypes;

import java.util.HashMap;

public class RoostMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    public final RoostBlockEntity blockentity;
    public final static HashMap<String, Object> guistate = new HashMap<>();

    public RoostMenu(int containerId, Player player, BlockPos pos) {
        super(ModMenuTypes.ATOMIC_OVEN_MENU.get(), containerId);
        RoostBlockEntity AtomicOvenBlockEntity;
        this.pos = pos;

        if (player.level().getBlockEntity(pos) instanceof RoostBlockEntity blockentity) {
            AtomicOvenBlockEntity = blockentity;
            this.addDataSlots(blockentity.data);

            addSlot(new SlotItemHandler(blockentity.getInputItems(), 0, 26, 20));
            addSlot(new SlotItemHandler(blockentity.getInputItems(), 1, 76, 20));
            addSlot(new SlotItemHandler(blockentity.getInputItems(), 2, 51, 54));

            /*addSlot(new SlotItemHandler(blockentity.getOutputItems(), 0, 110, 37) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });*/

        } else {
            AtomicOvenBlockEntity = null;
            System.err.println("Invalid block entity at position: " + pos);
        }

        addPlayerInventory(player.getInventory());
        addPlayerHotbar(player.getInventory());

        this.blockentity = AtomicOvenBlockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockentity.getLevel(), blockentity.getBlockPos()), player, ModBlocks.ROOST.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMovedStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack rawStack = slot.getItem();
            quickMovedStack = rawStack.copy();

            if (index >= 0 && index <= 2) {
                if (!this.moveItemStackTo(rawStack, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 3 && index < 39) {
                if (!this.moveItemStackTo(rawStack, 0, 3, false)) {
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

    public ItemStack isSlotItem(int slot) {
        return blockentity.getSlotInputItems(slot);
    }

    public int getScaledProgress() {
        int progress = blockentity.data.get(0);
        int maxProgress = blockentity.data.get(1);
        int progressArrowSize = 22;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;

    }

    public int getScaledFuelBurnTime() {
        int fuelBurnTime = blockentity.data.get(2);
        int maxFuelBurnTime = blockentity.getMaxFuelBurnTime();
        int fuelBarHeight = 14;

        return maxFuelBurnTime != 0 && fuelBurnTime != 0 ? fuelBurnTime * fuelBarHeight / maxFuelBurnTime : 0;
    }

    public float getLitProgress() {
        int i = blockentity.data.get(2);
        int maxFuelBurnTime = blockentity.getMaxFuelBurnTime();
        if (i == 0) {
            i = 200;
        }

        return Mth.clamp((float)blockentity.data.get(2) / (float)i, 0.0F, 1.0F);
    }

    public int getScaledEntityProgress() {
        int progress = blockentity.data.get(0);
        return progress != 0 ? (int) (progress * 0.098) / 14 : 0;
    }

    public int getBabyScaledEntityProgress() {
        int progress = blockentity.data.get(0);
        return progress != 0 ? (progress * 196) / 1000 / 46 : 0;
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