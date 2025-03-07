package net.zuperz.resource_armadillo.block.entity.custom;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.zuperz.resource_armadillo.block.custom.ArmadilloHiveBlock;
import net.zuperz.resource_armadillo.block.custom.ArmadilloHiveBlock;
import net.zuperz.resource_armadillo.block.entity.ItemHandler.CustomItemHandler;
import net.zuperz.resource_armadillo.block.entity.ModBlockEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.recipes.BreedingRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.BreedingRecipe;
import net.zuperz.resource_armadillo.recipes.NestRecipe;
import net.zuperz.resource_armadillo.screen.ArmadilloHiveMenu;
import net.zuperz.resource_armadillo.screen.RoostMenu;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.zuperz.resource_armadillo.block.custom.ArmadilloHiveBlock.ARMADILLO_DATA;

public class ArmadilloHiveBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {

    private final ItemStackHandler inputItems = createItemHandler(5);
    private final ItemStackHandler outputItems = createItemHandler(1);

    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> new CombinedInvWrapper(inputItems, outputItems));
    private final Lazy<IItemHandler> inputItemHandler = Lazy.of(() -> new CustomItemHandler(inputItems));
    private final Lazy<IItemHandler> outputItemHandler = Lazy.of(() -> new CustomItemHandler(outputItems));


    private static final int[] SLOTS_FOR_UP = new int[]{0, 1};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1};

    private int progress = 0;
    private int maxProgress = 3000;

    private long lastArmadilloExitTime = 0;
    private static final long ARMADILLO_COOLDOWN_TIME = 5000;

    private String storedArmadilloData = "";
    public String storedArmadilloData2 = "";

    public final ContainerData data;

    public ArmadilloHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARMADILLO_HIVE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ArmadilloHiveBlockEntity.this.progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ArmadilloHiveBlockEntity.this.progress = pValue;
                    case 1 -> maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    /* Block Entity */

    public void tick(Level level, BlockPos pos, BlockState state, ArmadilloHiveBlockEntity blockEntity) {
        boolean dirty = true;
        detectAndStoreArmadillo(level, pos, blockEntity, state);

        if (!isArmadilloDataEmpty() || !isArmadilloData2Empty()) {
            level.setBlockAndUpdate(pos, state.setValue(ARMADILLO_DATA, true));
        } else {
            level.setBlockAndUpdate(pos, state.setValue(ARMADILLO_DATA, false));
        }

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

        blockEntity.goingToBeCrafted();

        if (dirty) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }

    private boolean hasRecipe() {
        if (!isArmadilloDataEmpty() && !isArmadilloData2Empty()) {
            Level level = this.level;
            if (level == null) return false;

            String armadillo1Item = getStoredArmadilloDataValue("resource_quality");
            ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));
            String armadillo2Item = getStoredArmadilloData2Value("resource_quality");
            ItemStack armadillo2Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo2Item)));

            Optional<RecipeHolder<BreedingRecipe>> breedingRecipe = level.getRecipeManager()
                    .getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()).stream()
                    .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo1Stack))
                    .filter(recipe -> recipe.value().armadillo_ingredient_2.test(armadillo2Stack))
                    .findFirst();

            Optional<RecipeHolder<BreedingRecipe>> breeding2Recipe = level.getRecipeManager()
                    .getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()).stream()
                    .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo2Stack))
                    .filter(recipe -> recipe.value().armadillo_ingredient_2.test(armadillo1Stack))
                    .findFirst();


            if (armadillo1Item.equals(armadillo2Item)) {

                ItemStack stack1 = inputItems.getStackInSlot(0);
                ItemStack stack2 = inputItems.getStackInSlot(1);

                boolean matchesFoodIngredients = stack1.is(ItemTags.ARMADILLO_FOOD) && stack2.is(ItemTags.ARMADILLO_FOOD);

                return matchesFoodIngredients;
            }

            if (breedingRecipe.isPresent() || breeding2Recipe.isPresent()) {
                BreedingRecipe recipe = breedingRecipe.map(RecipeHolder::value).orElse(null);
                BreedingRecipe recipe2 = breeding2Recipe.map(RecipeHolder::value).orElse(null);

                if (recipe != null) {
                    boolean matchesArmadilloIngredients =
                            (recipe.armadillo_ingredient_1.test(armadillo1Stack) && recipe.armadillo_ingredient_2.test(armadillo2Stack)) ||
                                    (recipe.armadillo_ingredient_1.test(armadillo2Stack) && recipe.armadillo_ingredient_2.test(armadillo1Stack));

                    boolean matchesFoodIngredients =
                            recipe.food_1.test(inputItems.getStackInSlot(0)) &&
                                    recipe.food_2.test(inputItems.getStackInSlot(1));

                    if (matchesArmadilloIngredients && matchesFoodIngredients) {
                        return true;
                    }
                }

                if (recipe2 != null) {
                    boolean matchesArmadilloIngredients2 =
                            (recipe2.armadillo_ingredient_1.test(armadillo1Stack) && recipe2.armadillo_ingredient_2.test(armadillo2Stack)) ||
                                    (recipe2.armadillo_ingredient_1.test(armadillo2Stack) && recipe2.armadillo_ingredient_2.test(armadillo1Stack));

                    boolean matchesFoodIngredients2 =
                            recipe2.food_1.test(inputItems.getStackInSlot(0)) &&
                                    recipe2.food_2.test(inputItems.getStackInSlot(1));

                    if (matchesArmadilloIngredients2 && matchesFoodIngredients2) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    private void craftItem(ArmadilloHiveBlockEntity blockEntity) {
        Level level = this.level;
        if (level == null) return;

        String armadillo1Item = getStoredArmadilloDataValue("resource_quality").replaceAll("^\\d+\\s+", "");
        String armadillo2Item = getStoredArmadilloData2Value("resource_quality").replaceAll("^\\d+\\s+", "");
        ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));
        ItemStack armadillo2Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo2Item)));


        Optional<RecipeHolder<BreedingRecipe>> breedingRecipe = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo1Stack))
                .filter(recipe -> recipe.value().armadillo_ingredient_2.test(armadillo2Stack))
                .findFirst();

        Optional<RecipeHolder<BreedingRecipe>> breeding2Recipe = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo2Stack))
                .filter(recipe -> recipe.value().armadillo_ingredient_2.test(armadillo1Stack))
                .findFirst();
        BreedingRecipe recipe = breedingRecipe.map(RecipeHolder::value).orElse(null);
        BreedingRecipe recipe2 = breeding2Recipe.map(RecipeHolder::value).orElse(null);

        if (recipe == null && recipe2 == null) return;

        BreedingRecipe finalRecipe = (recipe != null) ? recipe : recipe2;

        if (armadillo1Item.equals(armadillo2Item)) {
            if (!(finalRecipe.armadillo_ingredient_1.test(armadillo1Stack) && finalRecipe.armadillo_ingredient_2.test(armadillo2Stack))
                    && !(finalRecipe.armadillo_ingredient_1.test(armadillo2Stack) && finalRecipe.armadillo_ingredient_2.test(armadillo1Stack))) {

                ItemStack result = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));

                outputItems.setStackInSlot(0, result.copy());

                inputItems.extractItem(0, 1, false);
                inputItems.extractItem(1, 1, false);

                spawnResourceArmadillo(blockEntity);

                blockEntity.setChanged();
                level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
                return;
            }
        }

        ItemStack result = finalRecipe.getResultItem(level.registryAccess());

        outputItems.setStackInSlot(0, result.copy());

        inputItems.extractItem(0, 1, false);
        inputItems.extractItem(1, 1, false);

        spawnResourceArmadillo(blockEntity);

        blockEntity.setChanged();
        level.sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
    }

    private void goingToBeCrafted() {
        Level level = this.level;
        if (level == null) return;

        String armadillo1Item = getStoredArmadilloDataValue("resource_quality").replaceAll("^\\d+\\s+", "");
        String armadillo2Item = getStoredArmadilloData2Value("resource_quality").replaceAll("^\\d+\\s+", "");
        ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));
        ItemStack armadillo2Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo2Item)));

        Optional<RecipeHolder<BreedingRecipe>> breedingRecipeOpt = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo1Stack))
                .filter(recipe -> recipe.value().armadillo_ingredient_2.test(armadillo2Stack))
                .findFirst();

        Optional<RecipeHolder<BreedingRecipe>> breeding2RecipeOpt = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.value().armadillo_ingredient_1.test(armadillo2Stack))
                .filter(recipe -> recipe.value().armadillo_ingredient_2.test(armadillo1Stack))
                .findFirst();

        BreedingRecipe recipe = breedingRecipeOpt.map(RecipeHolder::value).orElse(null);
        BreedingRecipe recipe2 = breeding2RecipeOpt.map(RecipeHolder::value).orElse(null);

        if (recipe == null && recipe2 == null) return;

        BreedingRecipe finalRecipe = (recipe != null) ? recipe : recipe2;

        if (armadillo1Item.equals(armadillo2Item)) {
            if (!isArmadilloDataEmpty()) {
                if (!(finalRecipe.armadillo_ingredient_1.test(armadillo1Stack) && finalRecipe.armadillo_ingredient_2.test(armadillo2Stack)) &&
                        !(finalRecipe.armadillo_ingredient_1.test(armadillo2Stack) && finalRecipe.armadillo_ingredient_2.test(armadillo1Stack))) {
                    inputItems.setStackInSlot(4, armadillo1Stack.copy());
                    return;
                }
            }
        }

        if (!isArmadilloDataEmpty()) {
            if (finalRecipe.armadillo_ingredient_1.test(armadillo1Stack) && finalRecipe.armadillo_ingredient_2.test(armadillo2Stack) ||
                    finalRecipe.armadillo_ingredient_1.test(armadillo2Stack) && finalRecipe.armadillo_ingredient_2.test(armadillo1Stack)) {

                ItemStack result = finalRecipe.getResultItem(level.registryAccess());
                inputItems.setStackInSlot(4, result.copy());
            }
        }
    }

    public boolean isCrafting() {
        return progress > 0;
    }

    private void spawnResourceArmadillo(ArmadilloHiveBlockEntity blockEntity) {
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

            float rot = blockEntity.getBlockState().getValue(ArmadilloHiveBlock.FACING).toYRot();
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

    public void spawnResourceArmadilloFromData(ArmadilloHiveBlockEntity blockEntity, String Ypos, int slot) {
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
                        float rot = blockEntity.getBlockState().getValue(ArmadilloHiveBlock.FACING).toYRot();
                        Vec3 pos = new Vec3(0.0d, 0, 0.1875d)
                                .yRot(-Mth.DEG_TO_RAD * rot)
                                .add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);

                        resourceArmadillo.setPos(pos);
                        resourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
                        resourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);
                    } else if (Ypos == "0.5") {
                        float rot = blockEntity.getBlockState().getValue(ArmadilloHiveBlock.FACING).toYRot();
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
        if (slot == 2 || slot == 3) {
            if (!blockEntity.level.isClientSide() && !blockEntity.getStoredArmadilloData2().isEmpty()) {
                try {
                    CompoundTag armadilloData = TagParser.parseTag(blockEntity.getStoredArmadilloData2());

                    LivingEntity resourceArmadillo;

                    if (isArmadilloData2AResourceArmadillo()) {
                        resourceArmadillo = new ResourceArmadilloEntity(ModEntities.RESOURCE_ARMADILLO.get(), blockEntity.level);
                    } else {
                        resourceArmadillo = new Armadillo(EntityType.ARMADILLO, blockEntity.level);
                    }

                    resourceArmadillo.load(armadilloData);

                    if (Ypos == "0") {
                        float rot = blockEntity.getBlockState().getValue(ArmadilloHiveBlock.FACING).toYRot();
                        Vec3 pos = new Vec3(0.0d, 0, 0.1875d)
                                .yRot(-Mth.DEG_TO_RAD * rot)
                                .add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);

                        resourceArmadillo.setPos(pos);
                        resourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
                        resourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);
                    } else if (Ypos == "0.5") {
                        float rot = blockEntity.getBlockState().getValue(ArmadilloHiveBlock.FACING).toYRot();
                        Vec3 pos = new Vec3(0.0d, 0.5d, 0.1875d)
                                .yRot(-Mth.DEG_TO_RAD * rot)
                                .add(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ() + 0.5);

                        resourceArmadillo.setPos(pos);
                        resourceArmadillo.yBodyRot = Mth.wrapDegrees(rot);
                        resourceArmadillo.yHeadRot = Mth.wrapDegrees(rot);
                    }

                    resourceArmadillo.setDeltaMovement(0, 0, 0);

                    blockEntity.level.addFreshEntity(resourceArmadillo);
                    storedArmadilloData2 = "";

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
        tag.putString("storedArmadilloData2", this.storedArmadilloData2);
        tag.put("inputItems", inputItems.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedArmadilloData = tag.getString("storedArmadilloData");
        this.storedArmadilloData2 = tag.getString("storedArmadilloData2");
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
        return new ArmadilloHiveMenu(containerId, player, this.getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.resource_armadillo.armadillo_hive");
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

    public void setStoredArmadilloData2(String data) {
        this.storedArmadilloData2 = data;
        setChanged();
    }

    public String getStoredArmadilloData2() {
        return this.storedArmadilloData2;
    }

    public boolean isArmadilloData2Empty() {
        return getStoredArmadilloData2().isEmpty();
    }

    public boolean isArmadilloData2AResourceArmadillo() {
        String armadilloDataString = getStoredArmadilloData2();

        if (armadilloDataString == null || armadilloDataString.isEmpty()) {
            return false;
        }

        try {
            CompoundTag armadilloData = TagParser.parseTag(armadilloDataString);

            if (armadilloData.contains("resource_quality")) {
                return true;
            }

        } catch (CommandSyntaxException e) {
            System.err.println("Failed to parse Armadillo data: " + e.getMessage());
        }

        return false;
    }

    public String getStoredArmadilloData2Value(String key) {
        String armadilloDataString = getStoredArmadilloData2();
        if (armadilloDataString == null || armadilloDataString.isEmpty()) {
            return "";
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
            e.printStackTrace();
        }

        return "";
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
            System.err.println("Failed to parse Armadillo data: " + e.getMessage());
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

            if (armadilloData.contains(key, Tag.TAG_COMPOUND)) {
                CompoundTag resourceTag = armadilloData.getCompound(key);
                if (resourceTag.contains("id", Tag.TAG_STRING)) {
                    return resourceTag.getString("id");
                }
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }

    public boolean isArmadillo2Baby() {
        String armadilloDataString = getStoredArmadilloData2();

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
            e.printStackTrace();
        }
        return false;
    }

    public void detectAndStoreArmadillo(Level level, BlockPos pos, ArmadilloHiveBlockEntity blockEntity, BlockState state) {
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
        List<Player> players = level.getEntitiesOfClass(Player.class, searchArea);

        List<ResourceArmadilloEntity> resourceArmadillos = level.getEntitiesOfClass(ResourceArmadilloEntity.class, searchArea);

        if (isArmadilloDataEmpty()) {
            if (!armadillos.isEmpty()) {
                Armadillo armadillo = armadillos.stream().filter(a -> !a.isBaby()).findFirst().orElse(null);
                if (armadillo != null) {
                    CompoundTag armadilloData = new CompoundTag();
                    armadillo.save(armadilloData);

                    blockEntity.setStoredArmadilloData(armadilloData.toString());
                    armadillo.discard();
                    level.playSound((Entity) players, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);

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
                    level.playSound((Entity) players, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);

                    lastArmadilloExitTime = System.currentTimeMillis();
                    blockEntity.setChanged();
                }
            }
        } else if (isArmadilloData2Empty()) {
            if (!armadillos.isEmpty()) {
                Armadillo armadillo = armadillos.stream().filter(a -> !a.isBaby()).findFirst().orElse(null);
                if (armadillo != null) {
                    CompoundTag armadilloData = new CompoundTag();
                    armadillo.save(armadilloData);

                    blockEntity.setStoredArmadilloData2(armadilloData.toString());
                    armadillo.discard();
                    level.playSound((Entity) players, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);

                    lastArmadilloExitTime = System.currentTimeMillis();
                    blockEntity.setChanged();
                }
            }
            if (!resourceArmadillos.isEmpty()) {
                ResourceArmadilloEntity resourceArmadillo = resourceArmadillos.stream().filter(a -> !a.isBaby()).findFirst().orElse(null);
                if (resourceArmadillo != null) {
                    CompoundTag armadilloData = new CompoundTag();
                    resourceArmadillo.save(armadilloData);

                    blockEntity.setStoredArmadilloData2(armadilloData.toString());
                    resourceArmadillo.discard();
                    level.playSound((Entity) players, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);

                    lastArmadilloExitTime = System.currentTimeMillis();
                    blockEntity.setChanged();
                }
            }
        }
    }
}