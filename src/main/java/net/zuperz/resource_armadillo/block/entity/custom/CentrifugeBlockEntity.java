package net.zuperz.resource_armadillo.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.block.entity.ItemHandler.CustomItemHandler;
import net.zuperz.resource_armadillo.block.entity.ModBlockEntities;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.zuperz.resource_armadillo.block.custom.CentrifugeBlock.LIT;

public class CentrifugeBlockEntity extends BlockEntity implements WorldlyContainer {

    private final ItemStackHandler inputItems = createItemHandler(4);
    private final ItemStackHandler outputItems = createItemHandler(1);

    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> new CombinedInvWrapper(inputItems, outputItems));
    private final Lazy<IItemHandler> inputItemHandler = Lazy.of(() -> new CustomItemHandler(inputItems));
    private final Lazy<IItemHandler> outputItemHandler = Lazy.of(() -> new CustomItemHandler(outputItems));

    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{4};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 3};

    private float rotation;
    private int progress = 0;
    private int maxProgress = 500;

    public final ContainerData data;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CENTRIFUGE_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CentrifugeBlockEntity.this.progress;
                    case 1 -> CentrifugeBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CentrifugeBlockEntity.this.progress = pValue;
                    case 1 -> CentrifugeBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void tick(BlockPos pos, BlockState state, CentrifugeBlockEntity blockEntity) {
        boolean isLit = state.getValue(LIT);
        boolean crafting = false;

        for (int slotIndex = 0; slotIndex < inputItems.getSlots(); slotIndex++) {
            if (hasRecipe(slotIndex)) {
                crafting = true;
                increaseCraftingProcess();

                if (!isLit) {
                    level.setBlockAndUpdate(pos, state.setValue(LIT, true));
                }

                if (hasProgressFinished()) {
                    craftItem(slotIndex);
                    resetProgress();
                }
            }
        }

        if (!crafting) {
            blockEntity.progress = Math.max(blockEntity.progress - 2, 0);
            if (isLit) {
                level.setBlockAndUpdate(pos, state.setValue(LIT, false));
            }
        }
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    private boolean hasProgressFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProcess() {
        this.progress++;
    }

    public void dropItems() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.get().getSlots());
        for (int i = 0; i < itemHandler.get().getSlots(); i++) {
            inventory.setItem(i, itemHandler.get().getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean hasRecipe(int slotIndex) {
        Level level = this.level;
        if (level == null) return false;

        ItemStack itemInSlot = inputItems.getStackInSlot(slotIndex);
        SimpleContainer inventory = new SimpleContainer(inputItems.getSlots());
        inventory.setItem(slotIndex, itemInSlot);

        Optional<RecipeHolder<CentrifugeRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), getRecipeInput(inventory), level);

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, recipe.get().value().output.copy().getItem().getDefaultInstance());
    }

    private void craftItem(int slotIndex) {
        Level level = this.level;
        if (level == null) return;

        ItemStack itemInSlot = inputItems.getStackInSlot(slotIndex);
        SimpleContainer inventory = new SimpleContainer(inputItems.getSlots());
        inventory.setItem(slotIndex, itemInSlot);

        Optional<RecipeHolder<CentrifugeRecipe>> recipeOptional = level.getRecipeManager()
                .getRecipeFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), getRecipeInput(inventory), level);

        if (recipeOptional.isPresent()) {
            CentrifugeRecipe recipe = recipeOptional.get().value();
            ItemStack outputStack = recipe.getResultItem(level.registryAccess());
            ItemStack existingOutput = outputItems.getStackInSlot(0);

            int maxStackSize = outputStack.getMaxStackSize();
            int availableSpace = maxStackSize - existingOutput.getCount();
            int amountToAdd = Math.min(availableSpace, outputStack.getCount());

            if (existingOutput.isEmpty()) {
                outputItems.setStackInSlot(0, new ItemStack(outputStack.getItem(), amountToAdd));
            } else if (existingOutput.getItem() == outputStack.getItem()) {
                existingOutput.grow(amountToAdd);
            }

            inputItems.extractItem(slotIndex, 1, false);
        }
    }


    private RecipeInput getRecipeInput(SimpleContainer inventory) {
        return new RecipeInput() {
            @Override
            public ItemStack getItem(int index) {
                return inventory.getItem(index).copy();
            }

            @Override
            public int size() {
                return inventory.getContainerSize();
            }
        };
    }

    private boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        ItemStack outputStack = outputItems.getStackInSlot(0);
        return outputStack.getMaxStackSize() > outputStack.getCount();
    }

    private boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        ItemStack outputStack = outputItems.getStackInSlot(0);
        return outputStack.isEmpty() || (outputStack.getItem() == stack.getItem() && outputStack.getCount() < stack.getMaxStackSize());
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("my_block_entity.progress", this.progress);
        tag.putInt("my_block_entity.max_progress", this.maxProgress);
        tag.put("my_block_entity.inputs", inputItems.serializeNBT(registries));
        tag.put("my_block_entity.outputs", outputItems.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("my_block_entity.progress");
        maxProgress = tag.getInt("my_block_entity.max_progress");
        inputItems.deserializeNBT(registries, tag.getCompound("my_block_entity.inputs"));
        outputItems.deserializeNBT(registries, tag.getCompound("my_block_entity.outputs"));
    }

    private ItemStackHandler createItemHandler(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public Lazy<IItemHandler> getItemHandler() {
        return itemHandler;
    }

    public ItemStackHandler getInputItems() {
        return inputItems;
    }

    public ItemStackHandler getOutputItems() {
        return outputItems;
    }

    public Lazy<IItemHandler> getInputItemHandler() {
        return inputItemHandler;
    }

    public Lazy<IItemHandler> getOutputItemHandler() {
        return outputItemHandler;
    }


    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isInputEmpty(int slot) {
        return inputItems.getStackInSlot(slot).isEmpty();
    }

    public boolean isOutputEmpty(int slot) {
        return outputItems.getStackInSlot(slot).isEmpty();
    }

    public void clearInput(int slot) {
        inputItems.setStackInSlot(slot, ItemStack.EMPTY);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void clearOutput(int slot) {
        outputItems.setStackInSlot(slot, ItemStack.EMPTY);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public Lazy<IItemHandler> getItems() {
        return itemHandler;
    }

    public ItemStack getItem(int pSlot) {
        if (pSlot < 4) {
            return inputItems.getStackInSlot(pSlot);
        } else {
            return outputItems.getStackInSlot(pSlot - 4);
        }
    }

    public void setItem(int slot, ItemStack stack) {
        if (slot < 4) {
            inputItems.setStackInSlot(slot, stack);
        } else {
            outputItems.setStackInSlot(slot - 4, stack);
        }
        setChanged();
        if (!level.isClientSide) {
            markForUpdate();
        }
    }

    private void markForUpdate() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction p_58363_) {
        if (p_58363_ == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return p_58363_ == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_58336_, ItemStack p_58337_, @javax.annotation.Nullable Direction p_58338_) {
        return this.canPlaceItem(p_58336_, p_58337_);
    }

    @Override
    public boolean canTakeItemThroughFace(int p_58392_, ItemStack p_58393_, Direction p_58394_) {
        return p_58394_ == Direction.DOWN && p_58392_ == 1 ? p_58393_.is(Items.WATER_BUCKET) || p_58393_.is(Items.BUCKET) : true;
    }

    @Override
    public int getContainerSize() {
        return itemHandler.get().getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inputItems.getSlots(); i++) {
            if (!inputItems.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        for (int i = 0; i < outputItems.getSlots(); i++) {
            if (!outputItems.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack removeItem(int slotIndex, int count) {
        int adjustedIndex = slotIndex - inputItems.getSlots();

        if (adjustedIndex < outputItems.getSlots()) {
            ItemStack stackInSlot = outputItems.getStackInSlot(adjustedIndex);
            if (!stackInSlot.isEmpty()) {
                return outputItems.extractItem(adjustedIndex, count, false);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slotIndex) {
        if (slotIndex < inputItems.getSlots()) {
            ItemStack stackInSlot = inputItems.getStackInSlot(slotIndex);

            if (!stackInSlot.isEmpty()) {
                inputItems.setStackInSlot(slotIndex, ItemStack.EMPTY);
                return stackInSlot;
            }
        }
        int adjustedIndex = slotIndex - inputItems.getSlots();
        if (adjustedIndex < outputItems.getSlots()) {
            ItemStack stackInSlot = outputItems.getStackInSlot(adjustedIndex);

            if (!stackInSlot.isEmpty()) {
                outputItems.setStackInSlot(adjustedIndex, ItemStack.EMPTY);
                return stackInSlot;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        final double MAX_DISTANCE = 64.0;
        double distanceSquared = player.distanceToSqr(this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5);
        return distanceSquared <= MAX_DISTANCE;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inputItems.getSlots(); i++) {
            inputItems.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < outputItems.getSlots(); i++) {
            outputItems.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}