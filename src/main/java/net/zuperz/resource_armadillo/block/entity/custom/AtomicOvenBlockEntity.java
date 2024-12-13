package net.zuperz.resource_armadillo.block.entity.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.zuperz.resource_armadillo.block.custom.AtomicOvenBlock;
import net.zuperz.resource_armadillo.block.entity.ItemHandler.CustomItemHandler;
import net.zuperz.resource_armadillo.block.entity.ModBlockEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.recipes.AtomicOvenRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.screen.AtomicOvenMenu;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class AtomicOvenBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private static boolean armadilloData;

    private final ItemStackHandler inputItems = createItemHandler(3);
    private final ItemStackHandler outputItems = createItemHandler(1);

    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> new CombinedInvWrapper(inputItems, outputItems));
    private final Lazy<IItemHandler> inputItemHandler = Lazy.of(() -> new CustomItemHandler(inputItems));
    private final Lazy<IItemHandler> outputItemHandler = Lazy.of(() -> new CustomItemHandler(outputItems));

    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{4};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 3};

    private int progress = 0;
    private int maxProgress = 300;

    private int fuelBurnTime = 0;
    private int maxFuelBurnTime = 1000;

    public final ContainerData data;

    public AtomicOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATOMIC_OVEN_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> AtomicOvenBlockEntity.this.progress;
                    case 1 -> AtomicOvenBlockEntity.this.maxProgress;
                    case 2 -> AtomicOvenBlockEntity.this.fuelBurnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> AtomicOvenBlockEntity.this.progress = pValue;
                    case 1 -> AtomicOvenBlockEntity.this.maxProgress = pValue;
                    case 2 -> AtomicOvenBlockEntity.this.fuelBurnTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    /* Armadillo */



    /* Block Entity */

    public static void tick(Level level, BlockPos pos, BlockState state, AtomicOvenBlockEntity blockEntity) {
        boolean dirty = false;
        detectAndStoreArmadillo(level, pos, blockEntity);

        if (blockEntity.fuelBurnTime > 0) {
            blockEntity.fuelBurnTime--;
        }

        if (blockEntity.fuelBurnTime == 0 && blockEntity.hasRecipe() && blockEntity.canConsumeFuel()) {
            blockEntity.fuelBurnTime = blockEntity.getFuelBurnTime(blockEntity.inputItems.getStackInSlot(2));
            if (blockEntity.fuelBurnTime > 0) {
                dirty = true;
                ItemStack fuelStack = blockEntity.inputItems.extractItem(2, 1, false);
            }
        }

        if (blockEntity.fuelBurnTime > 0 && blockEntity.hasRecipe()) {
            blockEntity.progress++;
            level.setBlockAndUpdate(pos, state.setValue(AtomicOvenBlock.LIT, true));
            if (blockEntity.progress >= blockEntity.maxProgress) {
                blockEntity.craftItem(blockEntity);
                blockEntity.progress = 0;
                dirty = true;
            }
            dirty = true;
        } else {
            if (blockEntity.progress != 0) {
                blockEntity.progress = Math.max(blockEntity.progress - 2, 0);
                dirty = true;
            }

            level.setBlockAndUpdate(pos, state.setValue(AtomicOvenBlock.LIT, false));
        }

        if (dirty) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }
    private boolean canConsumeFuel() {
        return isFuel(this.inputItems.getStackInSlot(2));
    }

    private int getFuelBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            return stack.getBurnTime(null);
        }
    }

    private boolean hasRecipe() {
        Level level = this.level;
        if (level == null) return false;

        SimpleContainer inventory = new SimpleContainer(inputItems.getSlots());
        for (int i = 0; i < inputItems.getSlots(); i++) {
            inventory.setItem(i, inputItems.getStackInSlot(i));
        }

        Optional<RecipeHolder<AtomicOvenRecipe>> alcheRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.ATOMIC_OVEN_RECIPE_TYPE.get(), getRecipeInput(inventory), level);


        return (alcheRecipe.isPresent() && canInsertAmountIntoOutputSlot(inventory)) ;
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
        ItemStack outputStack1 = outputItems.getStackInSlot(0);

        boolean canInsertIntoFirstSlot = outputStack1.isEmpty() || (outputStack1.getCount() < outputStack1.getMaxStackSize());

        return canInsertIntoFirstSlot;
    }

    private void craftItem(AtomicOvenBlockEntity serverLevel) {
        Level level = this.level;
        if (level == null) return;

        SimpleContainer inventory = new SimpleContainer(inputItems.getSlots());
        for (int i = 0; i < inputItems.getSlots(); i++) {
            inventory.setItem(i, inputItems.getStackInSlot(i));
        }

        Optional<RecipeHolder<AtomicOvenRecipe>> alcheRecipeOptional = level.getRecipeManager()
                .getRecipeFor(ModRecipes.ATOMIC_OVEN_RECIPE_TYPE.get(), getRecipeInput(inventory), level);

        if (alcheRecipeOptional.isPresent()) {
            AtomicOvenRecipe recipe = alcheRecipeOptional.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());

            ItemStack outputStack = outputItems.getStackInSlot(0);
            if (outputStack.isEmpty()) {
                outputItems.setStackInSlot(0, result.copy());
            }
            spawnResourceArmadillo(serverLevel);
            outputItems.extractItem(0, 1, false);

            inputItems.extractItem(0, 1, false);
            inputItems.extractItem(1, 1, false);
        }
    }

    private void spawnResourceArmadillo(AtomicOvenBlockEntity blockEntity) {
        if(!blockEntity.level.isClientSide()) {
            ResourceArmadilloEntity ResourceArmadillo = new ResourceArmadilloEntity(ModEntities.RESOURCE_ARMADILLO.get(), level);
            float rot = blockEntity.getBlockState().getValue(AtomicOvenBlock.FACING).toYRot();
            Vec3 pos = new Vec3(0.0d, 0.75d, 0.1875d).yRot(-Mth.DEG_TO_RAD * rot).add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);
            ResourceArmadillo.setPos(pos);
            ResourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
            ResourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);

            ItemStack slotStack = blockEntity.getOutputItemInSlot(0);
            ResourceArmadillo.setResource(new ItemStack(slotStack.getItem(), 1));


            blockEntity.level.addFreshEntity(ResourceArmadillo);
        }
    }

    private boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(null) > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putInt("fuelBurnTime", this.fuelBurnTime);
        tag.put("inputItems", inputItems.serializeNBT(registries));
        tag.put("outputItems", outputItems.serializeNBT(registries));
        if (!armadilloData) {
            tag.putBoolean("armadilloData", armadilloData);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.fuelBurnTime = tag.getInt("fuelBurnTime");
        inputItems.deserializeNBT(registries, tag.getCompound("inputItems"));
        outputItems.deserializeNBT(registries, tag.getCompound("outputItems"));
        if (tag.contains("armadilloData")) {
            armadilloData = tag.getBoolean("armadilloData");
        }
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

    public ItemStack getOutputItemInSlot(int slot) {
        return outputItems.getStackInSlot(slot);
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

    public int getMaxFuelBurnTime() {
        return maxFuelBurnTime;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AtomicOvenMenu(containerId, player, this.getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_armadillo.atomic_oven");
    }

    public void dropItems() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.get().getSlots());
        for (int i = 0; i < itemHandler.get().getSlots(); i++) {
            inventory.setItem(i, itemHandler.get().getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
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
    public ItemStack getItem(int pSlot) {
        if (pSlot < 4) {
            return inputItems.getStackInSlot(pSlot);
        } else {
            return outputItems.getStackInSlot(pSlot - 4);
        }
    }

    @Override
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

    public static void detectAndStoreArmadillo(Level level, BlockPos pos, AtomicOvenBlockEntity blockEntity) {
        if (level == null || level.isClientSide) return;

        double radius = 2.0;
        AABB searchArea = new AABB(Vec3.atLowerCornerOf(pos.offset((int) -radius, (int) -radius, (int) -radius)), Vec3.atLowerCornerOf(pos.offset((int) radius, (int) radius, (int) radius)));

        List<Armadillo> armadillos = level.getEntitiesOfClass(Armadillo.class, searchArea);

        if (!hasArmadillo()) {
            if (!armadillos.isEmpty()) {
                Armadillo armadillo = armadillos.get(0);
                armadilloData = true;
                armadillo.discard();

                blockEntity.setChanged();
            }
        }
    }

    public static boolean hasArmadillo() {
        return armadilloData;
    }
}