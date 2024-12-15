package net.zuperz.resource_armadillo.entity.custom.armadillo.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.RandomLookAround;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.entity.custom.armadillo.type.ResourceSensorTypes;

public class ResourceArmadilloAi {
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0F;
    private static final double DEFAULT_CLOSE_ENOUGH_DIST = 2.0;
    private static final double BABY_CLOSE_ENOUGH_DIST = 1.0;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);


    private static final ImmutableList<SensorType<? extends Sensor<? super ResourceArmadilloEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY,
            (SensorType<? extends Sensor<? super ResourceArmadilloEntity>>) ResourceSensorTypes.ARMADILLO_TEMPTATIONS.get(),
            SensorType.NEAREST_ADULT,
            (SensorType<? extends Sensor<? super ResourceArmadilloEntity>>) ResourceSensorTypes.ARMADILLO_SCARE_DETECTED.get()
    );

    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.IS_PANICKING,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.GAZE_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.DANGER_DETECTED_RECENTLY
    );

    //* Custom *//

    public static void moveToBlock(ResourceArmadilloEntity armadillo, BlockPos targetPos) {
        armadillo.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, 1.0F, 0));
    }

    public static BlockPos findAtomicOvenPosition(ServerLevel level) {
        for (int x = -25; x <= 25; x++) {
            for (int z = -25; z <= 25; z++) {
                BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
                if (level.getBlockState(pos).getBlock() == ModBlocks.ROOST.get()) {
                    return pos;
                }
            }
        }
        return null;
    }

    //* Armadillo *//

    private static final OneShot<ResourceArmadilloEntity> ARMADILLO_ROLLING_OUT = BehaviorBuilder.create(
            p_316587_ -> p_316587_.group(p_316587_.absent(MemoryModuleType.DANGER_DETECTED_RECENTLY))
                    .apply(p_316587_, p_316348_ -> (p_319679_, p_319680_, p_319681_) -> {
                        if (p_319680_.isScared()) {
                            p_319680_.rollOut();
                            return true;
                        } else {
                            return false;
                        }
                    })
    );

    public static Brain.Provider<ResourceArmadilloEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    public static Brain<?> makeBrain(Brain<ResourceArmadilloEntity> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initScaredActivity(pBrain);
        pBrain.setCoreActivities(Set.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
        return pBrain;
    }

    private static void initCoreActivity(Brain<ResourceArmadilloEntity> pBrain) {
        pBrain.addActivity(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new Swim(0.8F),
                        new net.zuperz.resource_armadillo.entity.custom.armadillo.ai.ResourceArmadilloAi.ArmadilloPanic(2.0F),
                        new LookAtTargetSink(45, 90),
                        new MoveToTargetSink() {
                            @Override
                            protected boolean checkExtraStartConditions(ServerLevel p_316506_, Mob p_316710_) {
                                if (p_316710_ instanceof ResourceArmadilloEntity armadillo && armadillo.isScared()) {
                                    return false;
                                }

                                return super.checkExtraStartConditions(p_316506_, p_316710_);
                            }
                        },
                        new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                        new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS),
                        ARMADILLO_ROLLING_OUT
                )
        );
    }

    private static void initIdleActivity(Brain<ResourceArmadilloEntity> pBrain) {
        pBrain.addActivity(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                        Pair.of(1, new AnimalMakeLove(ModEntities.RESOURCE_ARMADILLO.get(), 1.1F, 1)),
                        Pair.of(
                                2,
                                new RunOne<>(
                                        ImmutableList.of(
                                                Pair.of(new FollowTemptation(p_316818_ -> 1.25F, p_319682_ -> p_319682_.isBaby() ? 1.0 : 2.0), 1),
                                                Pair.of(BabyFollowAdult.create(ADULT_FOLLOW_RANGE, 1.25F), 1)
                                        )
                                )
                        ),
                        Pair.of(3, new Behavior<ResourceArmadilloEntity>(ImmutableMap.of(), 1) {
                            @Override
                            protected void tick(ServerLevel pLevel, ResourceArmadilloEntity pOwner, long pGameTime) {
                                BlockPos targetPos = findAtomicOvenPosition(pLevel);

                                if (targetPos != null) {
                                    moveToBlock(pOwner, targetPos);
                                }
                            }

                            @Override
                            protected boolean canStillUse(ServerLevel pLevel, ResourceArmadilloEntity pEntity, long pGameTime) {
                                return true;
                            }
                        }),
                        Pair.of(5, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
                        Pair.of(
                                6,
                                new RunOne<>(
                                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                                        ImmutableList.of(
                                                Pair.of(RandomStroll.stroll(1.0F), 1), Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1), Pair.of(new DoNothing(30, 60), 1)
                                        )
                                )
                        )
                )
        );
    }

    private static void moveToHive(ResourceArmadilloEntity armadillo, BlockPos hivePos) {
        armadillo.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(hivePos, 1.0F, 0));
    }


    private static void initScaredActivity(Brain<ResourceArmadilloEntity> pBrain) {
        pBrain.addActivityWithConditions(
                Activity.PANIC,
                ImmutableList.of(Pair.of(0, new net.zuperz.resource_armadillo.entity.custom.armadillo.ai.ResourceArmadilloAi.ArmadilloBallUp())),
                Set.of(
                        Pair.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.VALUE_PRESENT),
                        Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT)
                )
        );
    }

    public static void updateActivity(ResourceArmadilloEntity pArmadillo) {
        pArmadillo.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.PANIC, Activity.IDLE));
    }

    public static Predicate<ItemStack> getTemptations() {
        return p_335265_ -> p_335265_.is(ItemTags.ARMADILLO_FOOD);
    }

    public static class ArmadilloBallUp extends Behavior<ResourceArmadilloEntity> {
        static final int BALL_UP_STAY_IN_STATE = 5 * TimeUtil.SECONDS_PER_MINUTE * 20;
        static final int TICKS_DELAY_TO_DETERMINE_IF_DANGER_IS_STILL_AROUND = 5;
        static final int DANGER_DETECTED_RECENTLY_DANGER_THRESHOLD = 75;
        int nextPeekTimer = 0;
        boolean dangerWasAround;

        public ArmadilloBallUp() {
            super(Map.of(), BALL_UP_STAY_IN_STATE);
        }

        protected void tick(ServerLevel pLevel, ResourceArmadilloEntity pOwner, long pGameTime) {
            super.tick(pLevel, pOwner, pGameTime);
            if (this.nextPeekTimer > 0) {
                this.nextPeekTimer--;
            }

            if (pOwner.shouldSwitchToScaredState()) {
                pOwner.switchToState(ResourceArmadilloEntity.ArmadilloState.SCARED);
                if (pOwner.onGround()) {
                    pOwner.playSound(SoundEvents.ARMADILLO_LAND);
                }
            } else {
                ResourceArmadilloEntity.ArmadilloState armadillo$armadillostate = pOwner.getState();
                long i = pOwner.getBrain().getTimeUntilExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY);
                boolean flag = i > 75L;
                if (flag != this.dangerWasAround) {
                    this.nextPeekTimer = this.pickNextPeekTimer(pOwner);
                }

                this.dangerWasAround = flag;
                if (armadillo$armadillostate == ResourceArmadilloEntity.ArmadilloState.SCARED) {
                    if (this.nextPeekTimer == 0 && pOwner.onGround() && flag) {
                        pLevel.broadcastEntityEvent(pOwner, (byte)64);
                        this.nextPeekTimer = this.pickNextPeekTimer(pOwner);
                    }

                    if (i < (long) ResourceArmadilloEntity.ArmadilloState.UNROLLING.animationDuration()) {
                        pOwner.playSound(SoundEvents.ARMADILLO_UNROLL_START);
                        pOwner.switchToState(ResourceArmadilloEntity.ArmadilloState.UNROLLING);
                    }
                } else if (armadillo$armadillostate == ResourceArmadilloEntity.ArmadilloState.UNROLLING && i > (long) ResourceArmadilloEntity.ArmadilloState.UNROLLING.animationDuration()) {
                    pOwner.switchToState(ResourceArmadilloEntity.ArmadilloState.SCARED);
                }
            }
        }

        private int pickNextPeekTimer(ResourceArmadilloEntity pArmadillo) {
            return ResourceArmadilloEntity.ArmadilloState.SCARED.animationDuration() + pArmadillo.getRandom().nextIntBetweenInclusive(100, 400);
        }

        protected boolean checkExtraStartConditions(ServerLevel pLevel, ResourceArmadilloEntity pOwner) {
            return pOwner.onGround();
        }

        protected boolean canStillUse(ServerLevel pLevel, ResourceArmadilloEntity pEntity, long pGameTime) {
            return pEntity.getState().isThreatened();
        }

        protected void start(ServerLevel pLevel, ResourceArmadilloEntity pEntity, long pGameTime) {
            pEntity.rollUp();
        }

        protected void stop(ServerLevel pLevel, ResourceArmadilloEntity pEntity, long pGameTime) {
            if (!pEntity.canStayRolledUp()) {
                pEntity.rollOut();
            }
        }
    }

    public static class ArmadilloPanic extends AnimalPanic<ResourceArmadilloEntity> {
        public ArmadilloPanic(float p_316413_) {
            super(p_316413_, p_350284_ -> DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES);
        }

        protected void start(ServerLevel p_326201_, ResourceArmadilloEntity p_326188_, long p_325949_) {
            p_326188_.rollOut();
            super.start(p_326201_, p_326188_, p_325949_);
        }
    }
}
