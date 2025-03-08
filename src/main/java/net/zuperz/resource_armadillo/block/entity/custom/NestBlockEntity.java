package net.zuperz.resource_armadillo.block.entity.custom;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
import net.zuperz.resource_armadillo.block.custom.NestBlock;
import net.zuperz.resource_armadillo.block.entity.ItemHandler.CustomItemHandler;
import net.zuperz.resource_armadillo.block.entity.ModBlockEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.recipes.NestRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.RoostRecipe;
import net.zuperz.resource_armadillo.screen.NestMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static net.minecraft.core.BlockPos.getX;
import static net.zuperz.resource_armadillo.block.custom.NestBlock.ARMADILLO_DATA;
import static oshi.util.ParseUtil.parseIntOrDefault;

public class NestBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {

    private final ItemStackHandler inputItems = createItemHandler(5);
    private final ItemStackHandler outputItems = createItemHandler(1);

    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> new CombinedInvWrapper(inputItems, outputItems));
    private final Lazy<IItemHandler> inputItemHandler = Lazy.of(() -> new CustomItemHandler(inputItems));
    private final Lazy<IItemHandler> outputItemHandler = Lazy.of(() -> new CustomItemHandler(outputItems));


    private static final int[] SLOTS_FOR_UP = new int[]{0, 1};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1};

    private int progress = 0;
    private int maxProgress = 600;

    private long lastArmadilloExitTime = 0;
    private static final long ARMADILLO_COOLDOWN_TIME = 5000;

    private String storedArmadilloData = "";
    private final Random random = new Random();

    public final ContainerData data;

    public NestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEST_BLOCK_ENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> NestBlockEntity.this.progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> NestBlockEntity.this.progress = pValue;
                    case 1 -> maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void playSound(SoundEvent p_19938_, float p_19939_, float p_19940_) {
        this.getLevel().playSound(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), p_19938_, SoundSource.NEUTRAL, p_19939_, p_19940_);
    }

    /* Block Entity */

    public void tick(Level level, BlockPos pos, BlockState state, NestBlockEntity blockEntity) {
        boolean dirty = true;
        detectAndStoreArmadillo(level, pos, blockEntity, state);
        {level.setBlockAndUpdate(pos, state.setValue(ARMADILLO_DATA, false));}

        String scuteCountStr = getStoredArmadilloDataValue("scute_count");
        String scuteTimeStr = getStoredArmadilloDataValue("scute_time");

        int scuteCount = parseIntOrDefault(scuteCountStr, 0);
        int scuteTime = parseIntOrDefault(scuteTimeStr, 0);

        updateArmadilloScuteData();

        if (blockEntity.hasRecipe()) {
            blockEntity.progress++;
            if (blockEntity.progress >= maxProgress) {
                if (blockEntity.hasRecipe()) {
                    blockEntity.craftItem(blockEntity);
                }
                blockEntity.progress = 0;
                dirty = true;
            }
            dirty = true;
        } else {
            if (blockEntity.progress != 0) {
                blockEntity.progress = Math.max(blockEntity.progress - 2, 0);
                dirty = true;
            }
        }

        if (!isArmadilloBaby() && scuteTime <= 1) {
            if (scuteCount > 1) {
                playSound(SoundEvents.ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                dropScute(blockEntity);
            }
        }

        if (dirty) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }

    private boolean hasRecipe() {
        if (!isArmadilloDataEmpty()) {
            Level level = this.level;
            if (level == null) return false;

            String armadillo1Item = getStoredArmadilloDataValue("resource_quality");
            ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));

            int scuteCount = Integer.parseInt(getStoredArmadilloDataValue("scute_count"));

            Optional<RecipeHolder<NestRecipe>> nestRecipe = level.getRecipeManager()
                    .getAllRecipesFor(ModRecipes.NEST_RECIPE_TYPE.get()).stream()
                    .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo1Stack))
                    .findFirst();


            if (nestRecipe.isPresent() && inputItems.getStackInSlot(0).canPerformAction(net.neoforged.neoforge.common.ItemAbilities.BRUSH_BRUSH)) {
                NestRecipe recipe = nestRecipe.get().value();

                if (scuteCount > 0) {
                    return recipe.armadillo_ingredient_1.test(armadillo1Stack) && canInsertAmountIntoOutputSlot() && canInsertItemsIntoOutputSlots(recipe.output.copy());
                }
            }
        }
        return false;
    }

    private record getRecipeInput(ItemStack input) implements RecipeInput {
        @Override
        public ItemStack getItem(int pIndex) {
            return input;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    private boolean canInsertAmountIntoOutputSlot() {
        ItemStack outputStack1 = outputItems.getStackInSlot(0);

        boolean canInsertIntoFirstSlot = outputStack1.isEmpty() || (outputStack1.getCount() < outputStack1.getMaxStackSize());

        return canInsertIntoFirstSlot;
    }

    private boolean canInsertItemsIntoOutputSlots(ItemStack stack1) {
        ItemStack outputStack1 = outputItems.getStackInSlot(0);

        return (outputStack1.isEmpty() || (outputStack1.getItem() == stack1.getItem() && outputStack1.getCount() < stack1.getMaxStackSize()));
    }

    private void craftItem(NestBlockEntity blockEntity) {
        Level level = this.level;
        if (level == null) return;

        String armadillo1Item = getStoredArmadilloDataValue("resource_quality").replaceAll("^\\d+\\s+", "");
        ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));

        Optional<RecipeHolder<NestRecipe>> NestRecipe = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.NEST_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo1Stack))
                .findFirst();

        NestRecipe recipe = NestRecipe.get().value();

        if (NestRecipe.isPresent()) {
            if (canInsertItemsIntoOutputSlots(recipe.output.copy())) {
                if (recipe.armadillo_ingredient_1.test(armadillo1Stack)) {
                    ItemStack result = recipe.getResultItem(level.registryAccess());

                    ItemStack stack = inputItems.getStackInSlot(0);
                    ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);

                    Holder.Reference<Enchantment> unbreakingEnchantment = level.registryAccess()
                            .registryOrThrow(Registries.ENCHANTMENT)
                            .getHolderOrThrow(Enchantments.UNBREAKING);

                    int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(unbreakingEnchantment, stack);
                    RandomSource random = RandomSource.create();

                    boolean shouldDamage = unbreakingLevel == 0 || random.nextInt(unbreakingLevel + 1) == 0;

                    if (stack.isDamageableItem() && shouldDamage) {
                        stack.setDamageValue(stack.getDamageValue() + 1);
                    }


                    if (enchantments != null && !enchantments.isEmpty()) {
                        stack.set(DataComponents.ENCHANTMENTS, enchantments);
                    }
                    inputItems.setStackInSlot(0, stack);

                    outputItems.insertItem(0, result, false);
                    setScuteCount("-1");

                    if (stack.isDamageableItem()) {
                        if (stack.getDamageValue() == stack.getMaxDamage()) {
                            inputItems.setStackInSlot(0, ItemStack.EMPTY);
                        }
                    }

                    blockEntity.setChanged();
                    level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private void dropScute(NestBlockEntity blockEntity) {
        Level level = this.level;
        if (level == null) return;

        String armadillo1Item = getStoredArmadilloDataValue("resource_quality").replaceAll("^\\d+\\s+", "");
        ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));

        setScuteCount("-1");
        outputItems.insertItem(0, armadillo1Stack, false);
        blockEntity.setChanged();
        level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
    }

    public void updateArmadilloScuteData() {
        String scuteCountStr = getStoredArmadilloDataValue("scute_count");
        String scuteCountTimeStr = getStoredArmadilloDataValue("scute_count_time");
        String scuteTimeStr = getStoredArmadilloDataValue("scute_time");

        int scuteCount = parseIntOrDefault(scuteCountStr, 0);
        int scuteCountTime = parseIntOrDefault(scuteCountTimeStr, 0);
        int scuteTime = parseIntOrDefault(scuteTimeStr, 0);

        if (scuteTime > 0) {
            --scuteTime;
            setStoredIntArmadilloDataValue("scute_time", scuteTime);
        }

        if (scuteCountTime <= 1 && scuteCount <= 1) {
            pickNextScuteCount();
            pickNextScuteCountTime();
        } else if (scuteCountTime > 1) {
            scuteCountTime--;
            setStoredIntArmadilloDataValue("scute_count_time", scuteCountTime);
        }
    }

    private void pickNextScuteCount() {
        int newScuteCount = random.nextInt(1, 8);
        setStoredIntArmadilloDataValue("scute_count", newScuteCount);
    }

    private void pickNextScuteCountTime() {
        int newScuteCountTime = random.nextInt(3400, 10000);
        setStoredIntArmadilloDataValue("scute_count_time", newScuteCountTime);
    }

    public boolean isCrafting() {
        return progress > 0;
    }

    private void spawnResourceArmadillo(NestBlockEntity blockEntity) {
        if (!blockEntity.level.isClientSide()) {
            ResourceArmadilloEntity resourceArmadillo = new ResourceArmadilloEntity(ModEntities.RESOURCE_ARMADILLO.get(), blockEntity.level);

            String storedData = blockEntity.getStoredArmadilloData();
            if (!storedData.isEmpty()) {
                try {
                    CompoundTag armadilloData = TagParser.parseTag(storedData);
                    resourceArmadillo.load(armadilloData);
                } catch (Exception e) {
                    System.err.println("Failed to parse or load armadillo data: " + e.getMessage());
                }
            }

            float rot = blockEntity.getBlockState().getValue(NestBlock.FACING).toYRot();
            Vec3 pos = new Vec3(0.0d, 0.38d, 0.0535d)
                    .yRot(-Mth.DEG_TO_RAD * rot)
                    .add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);
            resourceArmadillo.setPos(pos);
            resourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
            resourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);
            resourceArmadillo.setOnGround(true);

            resourceArmadillo.setDeltaMovement(0, 0, 0);

            ItemStack slotStack = blockEntity.getOutputItemInSlot(0);

            if (!slotStack.isEmpty()) {
                resourceArmadillo.setResource(new ItemStack(slotStack.getItem(), 1));
            }

            resourceArmadillo.setBabyAge(-2000);

            resourceArmadillo.setUUID(UUID.randomUUID());

            blockEntity.level.addFreshEntity(resourceArmadillo);
        }
    }

    public void spawnResourceArmadilloFromData(NestBlockEntity blockEntity, String Ypos, int slot) {
        if (blockEntity.level.isClientSide()) {
            System.out.println("Attempted to spawn armadillo on client side.");
            return;
        }

        if (slot == 1 || slot == 3) {
            if (!blockEntity.level.isClientSide() && !blockEntity.getStoredArmadilloData().isEmpty()) {
                try {
                    CompoundTag armadilloData = TagParser.parseTag(blockEntity.getStoredArmadilloData());

                    LivingEntity resourceArmadillo;

                    if (isArmadilloDataAResourceArmadillo()) {
                        resourceArmadillo = new ResourceArmadilloEntity(ModEntities.RESOURCE_ARMADILLO.get(), blockEntity.level);
                    } else {
                        resourceArmadillo = new Armadillo(EntityType.ARMADILLO, blockEntity.level);
                    }

                    resourceArmadillo.load(armadilloData);

                    if (Ypos == "0") {
                        float rot = blockEntity.getBlockState().getValue(NestBlock.FACING).toYRot();
                        Vec3 pos = new Vec3(0.0d, 0, 0.1875d)
                                .yRot(-Mth.DEG_TO_RAD * rot)
                                .add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);

                        resourceArmadillo.setPos(pos);
                        resourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
                        resourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);
                    } else if (Ypos == "0.5") {
                        float rot = blockEntity.getBlockState().getValue(NestBlock.FACING).toYRot();
                        Vec3 pos = new Vec3(0.0d, 0.5d, 0.1875d)
                                .yRot(-Mth.DEG_TO_RAD * rot)
                                .add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);

                        resourceArmadillo.setPos(pos);
                        resourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
                        resourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);
                    }

                    resourceArmadillo.setDeltaMovement(0, 0, 0);

                    blockEntity.level.addFreshEntity(resourceArmadillo);
                    storedArmadilloData = "";

                    lastArmadilloExitTime = System.currentTimeMillis();

                    setChanged();
                } catch (Exception e) {
                    System.err.println("Failed to spawn ResourceArmadillo from stored data: " + e.getMessage());
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putString("storedArmadilloData", this.storedArmadilloData);
        tag.put("inputItems", inputItems.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedArmadilloData = tag.getString("storedArmadilloData");
        inputItems.deserializeNBT(registries, tag.getCompound("inputItems"));
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

    public ItemStack getSlotInputItems(int slot) {
        return inputItems.getStackInSlot(slot);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new NestMenu(containerId, player, this.getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_armadillo.nest");
    }

    public void dropItems() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.get().getSlots() - 1);

        int targetSlot = 0;
        for (int i = 0; i < itemHandler.get().getSlots(); i++) {
            if (i == 4) {
                continue;
            }

            inventory.setItem(targetSlot, itemHandler.get().getStackInSlot(i));
            targetSlot++;
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
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        if (pSlot < 4) {
            return inputItems.getStackInSlot(pSlot);
        }
        return null;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 4) {
            inputItems.setStackInSlot(slot, stack);
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
    }

    public float getScaledProgress() {
        float EntitySize = 1f;
        int progess = data.get(0);
        int maxProgress = data.get(1);

        return maxProgress != 0 && progess != 0 ? progess * EntitySize / maxProgress : 0;
    }

    public float getBabyScaledProgress() {
        float EntitySize = 0.6f;
        int progess = data.get(0);
        int maxProgress = data.get(1);

        return maxProgress != 0 && progess != 0 ? progess * EntitySize / maxProgress : 0;
    }

    public void setResourceQuality(String newItemId) {
        try {
            CompoundTag armadilloNBT = TagParser.parseTag(storedArmadilloData);

            CompoundTag resourceQuality;
            if (armadilloNBT.contains("resource_quality", 10)) {
                resourceQuality = armadilloNBT.getCompound("resource_quality");
            } else {
                resourceQuality = new CompoundTag();
                armadilloNBT.put("resource_quality", resourceQuality);
            }

            if (!newItemId.equals("same")) {
                resourceQuality.putString("id", newItemId);
            }

            resourceQuality.putInt("count", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScuteCount(String countModifier) {
        try {
            CompoundTag armadilloNBT = TagParser.parseTag(storedArmadilloData);

            int currentCount = armadilloNBT.getInt("scute_count");

            if (countModifier.startsWith("+")) {
                int toAdd = Integer.parseInt(countModifier.substring(1));
                currentCount += toAdd;
            } else if (countModifier.startsWith("-")) {
                int toSubtract = Integer.parseInt(countModifier.substring(1));
                currentCount -= toSubtract;
            } else {
                currentCount = Integer.parseInt(countModifier);
            }

            armadilloNBT.putInt("scute_count", currentCount);
            storedArmadilloData = armadilloNBT.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStoredIntArmadilloDataValue(String key, int newModifier) {
        try {
            if (storedArmadilloData == null || storedArmadilloData.isEmpty()) {
                storedArmadilloData = "";
            }

            CompoundTag armadilloNBT = TagParser.parseTag(storedArmadilloData);
            armadilloNBT.putInt(key, newModifier);

            storedArmadilloData = armadilloNBT.getAsString();

        } catch (CommandSyntaxException ignored) {
        }
    }

    public void setStoredStringArmadilloDataValue(String key, String newModifier) {
        try {
            CompoundTag armadilloNBT = TagParser.parseTag(storedArmadilloData);

            CompoundTag keyTag;
            if (armadilloNBT.contains(key, 10)) {
                keyTag = armadilloNBT.getCompound(key);
            } else {
                keyTag = new CompoundTag();
                armadilloNBT.put(key, keyTag);
            }
            keyTag.putString(key, newModifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStoredArmadilloData(String data) {
        this.storedArmadilloData = data;
        setChanged();
    }

    public String getStoredArmadilloData() {
        return this.storedArmadilloData;
    }

    public boolean isArmadilloDataEmpty() {
        return getStoredArmadilloData().isEmpty();
    }

    public boolean isArmadilloDataAResourceArmadillo() {
        String armadilloDataString = getStoredArmadilloData();

        if (armadilloDataString == null || armadilloDataString.isEmpty()) {
            return false;
        }

        try {
            CompoundTag armadilloData = TagParser.parseTag(armadilloDataString);

            if (armadilloData.contains("resource_quality")) {
                return true;
            }

        } catch (CommandSyntaxException e) {
            System.err.println("isArmadilloDataAResourceArmadillo Failed: " + e.getMessage());
        }

        return false;
    }

    public String getStoredArmadilloDataValue(String key) {
        String armadilloDataString = getStoredArmadilloData();
        if (armadilloDataString == null || armadilloDataString.isEmpty()) {
            return "";
        }

        try {
            CompoundTag armadilloData = TagParser.parseTag(armadilloDataString);

            if (armadilloData.contains(key)) {
                Tag tag = armadilloData.get(key);

                if (tag.getId() == Tag.TAG_INT) {
                    return String.valueOf(armadilloData.getInt(key));
                } else if (tag.getId() == Tag.TAG_FLOAT) {
                    return String.valueOf(armadilloData.getFloat(key));
                } else if (tag.getId() == Tag.TAG_STRING) {
                    return armadilloData.getString(key);
                }
            }
        } catch (CommandSyntaxException e) {
            System.err.println("getStoredArmadilloDataValue 1: " + e.getMessage());
        }

        try {
            CompoundTag armadilloData = TagParser.parseTag(armadilloDataString);

            if (armadilloData.contains(key, Tag.TAG_COMPOUND)) {
                CompoundTag resourceTag = armadilloData.getCompound(key);
                if (resourceTag.contains("id", Tag.TAG_STRING)) {
                    return resourceTag.getString("id");
                }
            }
        } catch (CommandSyntaxException e) {
            System.err.println("getStoredArmadilloDataValue 2: " + e.getMessage());
        }

        return "";
    }

    public boolean isArmadilloBaby() {
        String armadilloDataString = getStoredArmadilloData();

        if (armadilloDataString == null || armadilloDataString.isEmpty()) {
            return false;
        }

        try {
            CompoundTag armadilloData = TagParser.parseTag(armadilloDataString);

            if (armadilloData.contains("Age")) {
                int age = armadilloData.getInt("Age");
                return age < 0;
            }
        } catch (CommandSyntaxException e) {
            System.err.println("isArmadilloBaby: " + e.getMessage());
        }
        return false;
    }

    public void detectAndStoreArmadillo(Level level, BlockPos pos, NestBlockEntity blockEntity, BlockState state) {
        if (level == null || level.isClientSide) return;

        if (System.currentTimeMillis() - lastArmadilloExitTime < ARMADILLO_COOLDOWN_TIME) {
            return;
        }

        double radius = 2;
        AABB searchArea = new AABB(
                Vec3.atLowerCornerOf(pos.offset((int) -radius, (int) -radius, (int) -radius)),
                Vec3.atLowerCornerOf(pos.offset((int) radius, (int) radius, (int) radius))
        );

        List<Armadillo> armadillos = level.getEntitiesOfClass(Armadillo.class, searchArea);

        List<ResourceArmadilloEntity> resourceArmadillos = level.getEntitiesOfClass(ResourceArmadilloEntity.class, searchArea);

        if (isArmadilloDataEmpty()) {
            if (!armadillos.isEmpty()) {
                Armadillo armadillo = armadillos.stream().filter(a -> !a.isBaby()).findFirst().orElse(null);
                if (armadillo != null) {
                    CompoundTag armadilloData = new CompoundTag();
                    armadillo.save(armadilloData);

                    blockEntity.setStoredArmadilloData(armadilloData.toString());
                    armadillo.discard();

                    lastArmadilloExitTime = System.currentTimeMillis();
                    blockEntity.setChanged();
                }
            }
            if (!resourceArmadillos.isEmpty()) {
                ResourceArmadilloEntity resourceArmadillo = resourceArmadillos.stream().filter(a -> !a.isBaby()).findFirst().orElse(null);
                if (resourceArmadillo != null) {
                    CompoundTag armadilloData = new CompoundTag();
                    resourceArmadillo.save(armadilloData);

                    blockEntity.setStoredArmadilloData(armadilloData.toString());
                    resourceArmadillo.discard();

                    lastArmadilloExitTime = System.currentTimeMillis();
                    blockEntity.setChanged();
                }
            }
        }
    }
}