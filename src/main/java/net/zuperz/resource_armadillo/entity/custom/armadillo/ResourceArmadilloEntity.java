package net.zuperz.resource_armadillo.entity.custom.armadillo;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ai.ResourceArmadilloAi;
import net.zuperz.resource_armadillo.entity.custom.armadillo.type.ResourceEntityDataSerializers;
import net.zuperz.resource_armadillo.item.ModArmadilloScutes;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;
import net.zuperz.resource_armadillo.recipes.ResourceArmadilloEntityRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.variant;

public class ResourceArmadilloEntity extends Animal {
    public static final float BABY_SCALE = 0.6F;
    public static final float MAX_HEAD_ROTATION_EXTENT = 32.5F;
    public static final int SCARE_CHECK_INTERVAL = 80;
    private static final double SCARE_DISTANCE_HORIZONTAL = 7.0;
    private static final double SCARE_DISTANCE_VERTICAL = 2.0;

    private ResourceArmadilloEntityRecipe recipe;
    Random pRandom = new Random();

    private double productionSpeed = (generateProductionSpeed(pRandom::nextDouble));
    private double productionEfficiency = (generateproductionEfficiency(pRandom::nextDouble));

    private static final EntityDataAccessor<ArmadilloState> ARMADILLO_STATE = SynchedEntityData.defineId(
            ResourceArmadilloEntity.class, ResourceEntityDataSerializers.RESOURCE_ARMADILLO_STATE);

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(
            ResourceArmadilloEntity.class, EntityDataSerializers.INT);

    public ItemStack resource = Items.ARMADILLO_SCUTE.getDefaultInstance();


    private long inStateTicks = 0L;
    public final AnimationState rollOutAnimationState = new AnimationState();
    public final AnimationState rollUpAnimationState = new AnimationState();
    public final AnimationState peekAnimationState = new AnimationState();
    private int scuteCount;
    private int scuteTime;
    private int scuteCountTime = 0;
    public ResourceLocation overlayTexture;
    BlockPos hivePos;
    private boolean armadillo_part = true;

    private boolean peekReceivedClient = false;

    public ResourceArmadilloEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.getNavigation().setCanFloat(true);
        this.scuteTime = this.pickNextScuteDropTime();
    }

    /* Resource */
    public void setResource(ItemStack resource) {
        this.resource = resource;
    }

    @Override
    public void tick() {
        super.tick();

        updateVariantFromResourceNotClient();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (this.isScared()) {
            this.clampHeadRotationToBody();
        }

        this.inStateTicks++;
        --scuteTime;

        if (scuteCountTime <= 0 && scuteCount <= 0) {
            pickNextScuteCount();
            pickNextScuteCountTime();
        } else if (scuteCountTime > 0) {
            scuteCountTime--;
        }
    }

    public void updateVariantFromResource() {
        ArmadilloScuteType newVariant = ArmadilloScuteRegistry.getInstance().getArmadilloScuteTypes().get(0);

        if (this.resource.getItem() instanceof ArmadilloScuteItem scuteItem) {
            newVariant = scuteItem.getScuteType();
        }

        if (!newVariant.equals(getVariant())) {
            setVariant(newVariant);
        }
    }


    private void updateVariantFromResourceNotClient() {
        if (this.level().isClientSide()) return;

        ArmadilloScuteType newVariant = ArmadilloScuteRegistry.getInstance().getArmadilloScuteTypes().get(0);

        if (this.resource.getItem() instanceof ArmadilloScuteItem scuteItem) {
            newVariant = scuteItem.getScuteType();
        }

        if (!newVariant.equals(getVariant())) {
            setVariant(newVariant);
        }
    }


    protected static double generateProductionSpeed(DoubleSupplier pSupplier) {
        return 1F + pSupplier.getAsDouble() * 0.2 + pSupplier.getAsDouble() * 0.2 + pSupplier.getAsDouble() * 0.2;
    }

    protected static double generateproductionEfficiency(DoubleSupplier pSupplier) {
        return 1F + pSupplier.getAsDouble() * 0.2 + pSupplier.getAsDouble() * 0.2 + pSupplier.getAsDouble() * 0.2;
    }

    public boolean hasResource() {
        return !resource.isEmpty();
    }

    /* Armadillo */

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        ResourceArmadilloEntity offspring = ModEntities.RESOURCE_ARMADILLO.get().create(level);
        if (offspring != null && otherParent instanceof ResourceArmadilloEntity) {
            ResourceArmadilloEntity parent = (ResourceArmadilloEntity) otherParent;

            //System.out.println("Breeding parents found!");
            //System.out.println("Parent 1 Production Speed: " + this.productionSpeed);
            //System.out.println("Parent 2 Production Speed: " + parent.productionSpeed);

            offspring.productionSpeed = (this.productionSpeed + parent.productionSpeed) / 2;
            offspring.productionEfficiency = (this.productionEfficiency + parent.productionEfficiency) / 2;

            ItemStack thisQuality = this.getResource();
            ItemStack otherQuality = parent.getResource();
            if (thisQuality.getItem() == otherQuality.getItem()) {
                ItemStack combinedQuality = thisQuality.copy();
                combinedQuality.setCount(Math.min(thisQuality.getCount() + otherQuality.getCount(), thisQuality.getMaxStackSize()));
                offspring.setResource(combinedQuality);
            } else {
                offspring.setResource(this.random.nextBoolean() ? thisQuality.copy() : otherQuality.copy());
            }
        } else {
            //System.out.println("Breeding failed: offspring or parent is null.");
        }
        return offspring;
    }


    @Override
    public boolean canMate(Animal mate) {
        return mate instanceof ResourceArmadilloEntity && this.isInLove() && mate.isInLove() && !this.isScared();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.ARMADILLO_FOOD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,
                12.0).add(Attributes.MOVEMENT_SPEED, 0.14);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ARMADILLO_STATE, ResourceArmadilloEntity.ArmadilloState.IDLE);
        pBuilder.define(VARIANT, 0);
    }
    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public ArmadilloScuteType getVariant() {
        List<ArmadilloScuteType> scuteTypes = ArmadilloScuteRegistry.getInstance().getArmadilloScuteTypes().stream().filter(ArmadilloScuteType::isEnabled).toList();
        int index = this.getTypeVariant();

        if (index < 0 || index >= scuteTypes.size()) {
            return scuteTypes.get(0);
        }
        return scuteTypes.get(index);
    }

    public void setVariant(ArmadilloScuteType variant) {
        List<ArmadilloScuteType> scuteTypes = ArmadilloScuteRegistry.getInstance().getArmadilloScuteTypes().stream().filter(ArmadilloScuteType::isEnabled).toList();
        int index = scuteTypes.indexOf(variant);

        if (index == -1) {
            index = 0;
        }
        this.entityData.set(VARIANT, index);
    }


    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType,
                                        @Nullable SpawnGroupData pSpawnGroupData) {
        var scuteTypes = ArmadilloScuteRegistry.getInstance().getArmadilloScuteTypes();
        ArmadilloScuteType randomScute = scuteTypes.get(this.random.nextInt(scuteTypes.size()));
        if (!scuteTypes.isEmpty()) {
            List<ArmadilloScuteType> validScuteTypes = scuteTypes.stream()
                    .filter(scute -> !scute.getName().equals("none"))
                    .toList();
            if (!validScuteTypes.isEmpty()) {
                randomScute = validScuteTypes.get(this.random.nextInt(validScuteTypes.size()));
                this.setVariant(randomScute);
            }
            String scuteName = randomScute.getName() + "_scute";
            ResourceLocation scuteLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scuteName);
            Item scuteItem = BuiltInRegistries.ITEM.get(scuteLocation);
            if (scuteItem != null) {
                setResource(new ItemStack(scuteItem));
            }
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
    }

    public boolean isScared() {
        return this.entityData.get(ARMADILLO_STATE) != ResourceArmadilloEntity.ArmadilloState.IDLE;
    }

    public boolean shouldHideInShell() {
        return this.getState().shouldHideInShell(this.inStateTicks);
    }

    public boolean shouldSwitchToScaredState() {
        return this.getState() == ResourceArmadilloEntity.ArmadilloState.ROLLING && this.inStateTicks > (long) ResourceArmadilloEntity.ArmadilloState.ROLLING.animationDuration();
    }

    public ResourceArmadilloEntity.ArmadilloState getState() {
        return this.entityData.get(ARMADILLO_STATE);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    public void switchToState(ResourceArmadilloEntity.ArmadilloState pState) {
        this.entityData.set(ARMADILLO_STATE, pState);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (ARMADILLO_STATE.equals(pKey)) {
            this.inStateTicks = 0L;
        }

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    protected Brain.Provider<ResourceArmadilloEntity> brainProvider() {
        return ResourceArmadilloAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return ResourceArmadilloAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }

    @Override
    public void customServerAiStep() {
        this.level().getProfiler().push("armadilloBrain");
        ((Brain<ResourceArmadilloEntity>)this.brain).tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("armadilloActivityUpdate");
        ResourceArmadilloAi.updateActivity(this);
        this.level().getProfiler().pop();

        if (this.isAlive() && !this.isBaby() && this.scuteTime <= 0) {
            if (this.scuteCount > 0) {
                this.playSound(SoundEvents.ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.spawnAtLocation(resource);
                this.gameEvent(GameEvent.ENTITY_PLACE);
                this.scuteCount--;
            }
            this.scuteTime = this.pickNextScuteDropTime();
        }

        super.customServerAiStep();
    }

    public boolean setBabyAge(int Age) {
        this.setAge(Age);
        return true;
    }


    private int pickNextScuteDropTime() {
        return 22 * (10 + this.random.nextInt(30, 200));
    }

    private void pickNextScuteCount() {
        this.scuteCount = this.random.nextInt(1, 8);
    }

    private void pickNextScuteCountTime() {
        this.scuteCountTime = this.random.nextInt(3400, 10000);
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.6F : 1.0F;
    }

    private void setupAnimationStates() {
        switch (this.getState()) {
            case IDLE:
                this.rollOutAnimationState.stop();
                this.rollUpAnimationState.stop();
                this.peekAnimationState.stop();
                break;
            case ROLLING:
                this.rollOutAnimationState.stop();
                this.rollUpAnimationState.startIfStopped(this.tickCount);
                this.peekAnimationState.stop();
                break;
            case SCARED:
                this.rollOutAnimationState.stop();
                this.rollUpAnimationState.stop();
                if (this.peekReceivedClient) {
                    this.peekAnimationState.stop();
                    this.peekReceivedClient = false;
                }

                if (this.inStateTicks == 0L) {
                    this.peekAnimationState.start(this.tickCount);
                    this.peekAnimationState.fastForward(ResourceArmadilloEntity.ArmadilloState.SCARED.animationDuration(), 1.0F);
                } else {
                    this.peekAnimationState.startIfStopped(this.tickCount);
                }
                break;
            case UNROLLING:
                this.rollOutAnimationState.startIfStopped(this.tickCount);
                this.rollUpAnimationState.stop();
                this.peekAnimationState.stop();
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 64 && this.level().isClientSide) {
            this.peekReceivedClient = true;
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMADILLO_PEEK, this.getSoundSource(), 1.0F, 1.0F, false);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public static boolean checkArmadilloSpawnRules(
            EntityType<ResourceArmadilloEntity> pEntityType, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
    ) {
        return pLevel.getBlockState(pPos.below()).is(BlockTags.ARMADILLO_SPAWNABLE_ON) && isBrightEnoughToSpawn(pLevel, pPos);
    }

    public boolean isScaredBy(LivingEntity pEntity) {
        if (!this.getBoundingBox().inflate(7.0, 2.0, 7.0).intersects(pEntity.getBoundingBox())) {
            return false;
        } else if (pEntity.getType().is(EntityTypeTags.UNDEAD)) {
            return true;
        } else if (this.getLastHurtByMob() == pEntity) {
            return true;
        } else if (pEntity instanceof Player player) {
            return player.isSpectator() ? false : player.isSprinting() || player.isPassenger();
        } else {
            return false;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("state", this.getState().getSerializedName());

        pCompound.putString("resource_quality", (this.resource.toString()));
        System.out.println("resource_quality test: " + (this.resource.toString()));

        pCompound.putBoolean("armadillo_part", this.armadillo_part);

        pCompound.putInt("scute_time", this.scuteTime);
        pCompound.putInt("scute_count", this.scuteCount);
        pCompound.putInt("scute_count_time", this.scuteCountTime);
        pCompound.putInt("variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.switchToState(ResourceArmadilloEntity.ArmadilloState.fromName(pCompound.getString("state")));
        if (pCompound.contains("scute_time")) {
            this.scuteTime = pCompound.getInt("scute_time");
        }
        if (pCompound.contains("resource_quality")) {
            String resourceItem = pCompound.getString("resource_quality");
            String[] parts = resourceItem.split(" ", 2);
            String cleanedResource = parts.length > 1 ? parts[1] : parts[0];
            System.out.println("resource_quality 2Text: " + cleanedResource);
            System.out.println("resource_quality 3Text: " +  BuiltInRegistries.ITEM.get(ResourceLocation.parse(cleanedResource)).getDefaultInstance());
            this.resource = BuiltInRegistries.ITEM.get(ResourceLocation.parse(cleanedResource)).getDefaultInstance();
        }

        if (pCompound.contains("armadillo_part")) {
            this.armadillo_part = pCompound.getBoolean("armadillo_part");
        }

        if (pCompound.contains("scute_count")) {
            this.scuteCount = pCompound.getInt("scute_count");
        }
        if (pCompound.contains("scute_count_time")) {
            this.scuteCountTime = pCompound.getInt("scute_count_time");
        }
        if (pCompound.contains("variant")) {
            this.entityData.set(VARIANT, pCompound.getInt("variant"));
        }
    }

    public void rollUp() {
        if (!this.isScared()) {
            this.stopInPlace();
            this.resetLove();
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.makeSound(SoundEvents.ARMADILLO_ROLL);
            this.switchToState(ResourceArmadilloEntity.ArmadilloState.ROLLING);
        }
    }

    public void rollOut() {
        if (this.isScared()) {
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.makeSound(SoundEvents.ARMADILLO_UNROLL_FINISH);
            this.switchToState(ResourceArmadilloEntity.ArmadilloState.IDLE);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isScared()) {
            pAmount = (pAmount - 1.0F) / 2.0F;
        }

        return super.hurt(pSource, pAmount);
    }


    @Override
    protected void actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);
        if (!this.isNoAi() && !this.isDeadOrDying()) {
            if (pDamageSource.getEntity() instanceof LivingEntity) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, 80L);
                if (this.canStayRolledUp()) {
                    this.rollUp();
                }
            } else if (pDamageSource.is(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) {
                this.rollOut();
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (itemstack.canPerformAction(net.neoforged.neoforge.common.ItemAbilities.BRUSH_BRUSH) && scuteCount > 0 && this.brushOffScute() ) {
            itemstack.hurtAndBreak(16, pPlayer, getSlotForHand(pHand));
            scuteCount--;
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (itemstack.is(Items.SHEARS) && this.shearOffScute()) {
            itemstack.hurtAndBreak(5, pPlayer, getSlotForHand(pHand));
        } else if (itemstack.isEmpty() && !(itemstack.canPerformAction(net.neoforged.neoforge.common.ItemAbilities.BRUSH_BRUSH) && !(itemstack.is(Items.SHEARS)))) {
            pPlayer.sendSystemMessage(Component.literal("resource is: " + getResource().getDisplayName().getString()));
            pPlayer.sendSystemMessage(Component.literal("scuteCount is: " + scuteCount));
            pPlayer.sendSystemMessage(Component.literal("scuteCountTime is: " + scuteCountTime));
            pPlayer.sendSystemMessage(Component.literal("scuteTime is: " + scuteTime));
        }
        return this.isScared() ? InteractionResult.FAIL : super.mobInteract(pPlayer, pHand);
    }

    private Boolean hasArmadilloPart() {
        if (armadillo_part) {
            return true;
        }
        else return false;
    }

    private void setArmadilloPart(boolean booleankey) {
        armadillo_part = booleankey;
    }

    @Override
    public void ageUp(int pAmount, boolean pForced) {
        if (this.isBaby() && pForced) {
            this.makeSound(SoundEvents.ARMADILLO_EAT);
        }

        super.ageUp(pAmount, pForced);
    }

    public boolean shearOffScute() {
        if (this.isBaby()) {
            return false;
        } else if (hasArmadilloPart()){
            this.spawnAtLocation(new ItemStack(ModItems.ARMADILLO_PART.get()));
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.ARMADILLO_BRUSH);
            setArmadilloPart(false);
            return true;
        }
        return false;
    }

    public boolean brushOffScute() {
        if (this.isBaby()) {
            return false;
        } else {
            this.spawnAtLocation(new ItemStack(getResource().getItem()));
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.ARMADILLO_BRUSH);
            return true;
        }
    }

    public boolean canStayRolledUp() {
        return !this.isPanicking() && !this.isInLiquid() && !this.isLeashed() && !this.isPassenger() && !this.isVehicle();
    }

    @Override
    public void setInLove(@Nullable Player pPlayer) {
        super.setInLove(pPlayer);
        this.makeSound(SoundEvents.ARMADILLO_EAT);
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.isScared();
    }

    @Override
    public SoundEvent getEatingSound(ItemStack pStack) {
        return SoundEvents.ARMADILLO_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isScared() ? null : SoundEvents.ARMADILLO_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMADILLO_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return this.isScared() ? SoundEvents.ARMADILLO_HURT_REDUCED : SoundEvents.ARMADILLO_HURT;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundEvents.ARMADILLO_STEP, 0.15F, 1.0F);
    }

    @Override
    public int getMaxHeadYRot() {
        return this.isScared() ? 0 : 32;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
                if (!ResourceArmadilloEntity.this.isScared()) {
                    super.clientTick();
                }
            }
        };
    }

    public ItemStack getResource() {
        return this.resource;
    }

    public static enum ArmadilloState implements StringRepresentable {
        IDLE("idle", false, 0, 0) {
            @Override
            public boolean shouldHideInShell(long p_326483_) {
                return false;
            }
        },
        ROLLING("rolling", true, 10, 1) {
            @Override
            public boolean shouldHideInShell(long p_326211_) {
                return p_326211_ > 5L;
            }
        },
        SCARED("scared", true, 50, 2) {
            @Override
            public boolean shouldHideInShell(long p_326129_) {
                return true;
            }
        },
        UNROLLING("unrolling", true, 30, 3) {
            @Override
            public boolean shouldHideInShell(long p_326371_) {
                return p_326371_ < 26L;
            }
        };

        private static final StringRepresentable.EnumCodec<ResourceArmadilloEntity.ArmadilloState> CODEC = StringRepresentable.fromEnum(ResourceArmadilloEntity.ArmadilloState::values);
        private static final IntFunction<ResourceArmadilloEntity.ArmadilloState> BY_ID = ByIdMap.continuous(
                ResourceArmadilloEntity.ArmadilloState::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
        public static final StreamCodec<ByteBuf, ResourceArmadilloEntity.ArmadilloState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ResourceArmadilloEntity.ArmadilloState::id);
        private final String name;
        private final boolean isThreatened;
        private final int animationDuration;
        private final int id;

        ArmadilloState(String pName, boolean pIsThreatened, int pAnimationDuration, int pId) {
            this.name = pName;
            this.isThreatened = pIsThreatened;
            this.animationDuration = pAnimationDuration;
            this.id = pId;
        }

        public static ResourceArmadilloEntity.ArmadilloState fromName(String pName) {
            return CODEC.byName(pName, IDLE);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private int id() {
            return this.id;
        }

        public abstract boolean shouldHideInShell(long pInStateTicks);

        public boolean isThreatened() {
            return this.isThreatened;
        }

        public int animationDuration() {
            return this.animationDuration;
        }
    }
}