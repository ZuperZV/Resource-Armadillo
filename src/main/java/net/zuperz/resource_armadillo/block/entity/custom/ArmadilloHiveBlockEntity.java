package net.zuperz.resource_armadillo.block.entity.custom;

import com.google.common.collect.Lists;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.custom.ArmadilloHiveBlock;
import net.zuperz.resource_armadillo.block.entity.ModBlockEntities;
import net.zuperz.resource_armadillo.component.ModDataComponentTypes;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.util.ModTags;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArmadilloHiveBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_FLOWER_POS = "flower_pos";
    private static final String ARMADILLO = "armadillo";

    public static final int MAX_OCCUPANTS = 3;
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<ArmadilloHiveBlockEntity.BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos;

    public ArmadilloHiveBlockEntity(BlockPos p_155134_, BlockState p_155135_) {
        super(ModBlockEntities.ARMADILLO_HIVE.get(), p_155134_, p_155135_);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        } else {
            for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
                if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 3;
    }

    @VisibleForDebug
    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(BlockState p_58753_) {
        return p_58753_.getValue(ArmadilloHiveBlock.HONEY_LEVEL);
    }

    @VisibleForDebug
    public boolean isSedated() {
        return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    public void addOccupant(Entity p_58742_) {
        if (this.stored.size() < 3) {
            p_58742_.stopRiding();
            p_58742_.ejectPassengers();
            this.storeBee(ArmadilloHiveBlockEntity.Occupant.of(p_58742_));
            if (this.level != null) {

                BlockPos blockpos = this.getBlockPos();
                this.level
                        .playSound(
                                null,
                                (double)blockpos.getX(),
                                (double)blockpos.getY(),
                                (double)blockpos.getZ(),
                                SoundEvents.BEEHIVE_ENTER,
                                SoundSource.BLOCKS,
                                1.0F,
                                1.0F
                        );
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(p_58742_, this.getBlockState()));
            }

            p_58742_.discard();
            super.setChanged();
        }
    }

    public void storeBee(ArmadilloHiveBlockEntity.Occupant p_330820_) {
        this.stored.add(new ArmadilloHiveBlockEntity.BeeData(p_330820_));
    }



    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private static void tickOccupants(
            Level p_155150_, BlockPos p_155151_, BlockState p_155152_, List<ArmadilloHiveBlockEntity.BeeData> p_155153_, @Nullable BlockPos p_155154_
    ) {
        boolean flag = false;
        Iterator<ArmadilloHiveBlockEntity.BeeData> iterator = p_155153_.iterator();

        while (iterator.hasNext()) {
            ArmadilloHiveBlockEntity.BeeData ArmadilloHiveBlockEntity$beedata = iterator.next();
            if (ArmadilloHiveBlockEntity$beedata.tick()) {
                ArmadilloHiveBlockEntity.BeeReleaseStatus ArmadilloHiveBlockEntity$beereleasestatus = ArmadilloHiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            }
        }


        if (flag) {
            setChanged(p_155150_, p_155151_, p_155152_);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArmadilloHiveBlockEntity hiveEntity) {
        tickOccupants(level, pos, state, hiveEntity.stored, hiveEntity.savedFlowerPos);

        if (!hiveEntity.stored.isEmpty() && level.getRandom().nextDouble() < 0.005) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;
            level.playSound(null, x, y, z, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (!hiveEntity.stored.isEmpty() && level.getRandom().nextDouble() < 0.01) {
            ArmadilloHiveBlockEntity.BeeData armadillo = hiveEntity.stored.remove(0);
            armadillo.startReentryTimer(400);
            assert hiveEntity.level != null;
            hiveEntity.level.addFreshEntity(Objects.requireNonNull(armadillo.toOccupant().createEntity(level, pos)));
        }

        if (!hiveEntity.isFull()) {
            AABB searchArea = new AABB(pos).inflate(2);
            List<ResourceArmadilloEntity> nearbyArmadillos = level.getEntitiesOfClass(
                    ResourceArmadilloEntity.class,
                    searchArea,
                    armadillo -> armadillo.isAlive()
            );

            for (ResourceArmadilloEntity armadillo : nearbyArmadillos) {
                boolean canEnter = hiveEntity.stored.stream()
                        .noneMatch(data -> data.occupant.entityData.equals(CustomData.of(armadillo.saveWithoutId(new CompoundTag()))) && !data.canReenter());
                if (!hiveEntity.isFull() && canEnter) {
                    hiveEntity.addOccupant(armadillo);
                }
            }
        }

        ResourceArmadillo.sendArmadilloHiveInfo(level, pos, state, hiveEntity);
    }



    @Override
    protected void loadAdditional(CompoundTag p_338675_, HolderLookup.Provider p_338666_) {
        super.loadAdditional(p_338675_, p_338666_);
        this.stored.clear();
        if (p_338675_.contains("armadillo")) {
            ArmadilloHiveBlockEntity.Occupant.LIST_CODEC
                    .parse(NbtOps.INSTANCE, p_338675_.get("armadillo"))
                    .resultOrPartial(p_330133_ -> LOGGER.error("Failed to parse bees: '{}'", p_330133_))
                    .ifPresent(p_330134_ -> p_330134_.forEach(this::storeBee));
        }

        this.savedFlowerPos = NbtUtils.readBlockPos(p_338675_, "flower_pos").orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187467_, HolderLookup.Provider p_324426_) {
        super.saveAdditional(p_187467_, p_324426_);
        p_187467_.put("armadillo", ArmadilloHiveBlockEntity.Occupant.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.getBees()).getOrThrow());
        if (this.hasSavedFlowerPos()) {
            p_187467_.put("flower_pos", NbtUtils.writeBlockPos(this.savedFlowerPos));
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput p_338335_) {
        super.applyImplicitComponents(p_338335_);
        this.stored.clear();
        List<ArmadilloHiveBlockEntity.Occupant> list = p_338335_.getOrDefault(ModDataComponentTypes.ARMADILLO, List.of());
        list.forEach(this::storeBee);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder p_338773_) {
        super.collectImplicitComponents(p_338773_);
        p_338773_.set(ModDataComponentTypes.ARMADILLO, this.getBees());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag p_331127_) {
        super.removeComponentsFromTag(p_331127_);
        p_331127_.remove("armadillo");
    }

    private List<ArmadilloHiveBlockEntity.Occupant> getBees() {
        return this.stored.stream().map(ArmadilloHiveBlockEntity.BeeData::toOccupant).toList();
    }

    public static class BeeData {
        private final ArmadilloHiveBlockEntity.Occupant occupant;
        private int ticksInHive;
        private int cannotEnterHiveTicks;

        BeeData(ArmadilloHiveBlockEntity.Occupant p_331832_) {
            this.occupant = p_331832_;
            this.ticksInHive = p_331832_.ticksInHive();
            this.cannotEnterHiveTicks = 0;
        }

        public boolean tick() {
            if (cannotEnterHiveTicks > 0) {
                cannotEnterHiveTicks--;
            }
            return ticksInHive++ > this.occupant.minTicksInHive;
        }

        public void startReentryTimer(int ticks) {
            this.cannotEnterHiveTicks = ticks;
        }

        public boolean canReenter() {
            return this.cannotEnterHiveTicks <= 0;
        }

        public ArmadilloHiveBlockEntity.Occupant toOccupant() {
            return new ArmadilloHiveBlockEntity.Occupant(this.occupant.entityData, this.ticksInHive, this.occupant.minTicksInHive);
        }
    }


    public static enum BeeReleaseStatus {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;
    }

    public static record Occupant(CustomData entityData, int ticksInHive, int minTicksInHive) {
        public static final Codec<ArmadilloHiveBlockEntity.Occupant> CODEC = RecordCodecBuilder.create(
                p_337984_ -> p_337984_.group(
                                CustomData.CODEC.optionalFieldOf("entity_data", CustomData.EMPTY).forGetter(ArmadilloHiveBlockEntity.Occupant::entityData),
                                Codec.INT.fieldOf("ticks_in_hive").forGetter(ArmadilloHiveBlockEntity.Occupant::ticksInHive),
                                Codec.INT.fieldOf("min_ticks_in_hive").forGetter(ArmadilloHiveBlockEntity.Occupant::minTicksInHive)
                        )
                        .apply(p_337984_, ArmadilloHiveBlockEntity.Occupant::new)
        );
        public static final Codec<List<ArmadilloHiveBlockEntity.Occupant>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<ByteBuf, ArmadilloHiveBlockEntity.Occupant> STREAM_CODEC = StreamCodec.composite(
                CustomData.STREAM_CODEC,
                ArmadilloHiveBlockEntity.Occupant::entityData,
                ByteBufCodecs.VAR_INT,
                ArmadilloHiveBlockEntity.Occupant::ticksInHive,
                ByteBufCodecs.VAR_INT,
                ArmadilloHiveBlockEntity.Occupant::minTicksInHive,
                ArmadilloHiveBlockEntity.Occupant::new
        );

        public static ArmadilloHiveBlockEntity.Occupant of(Entity p_331485_) {
            CompoundTag compoundtag = new CompoundTag();
            p_331485_.save(compoundtag);
            boolean flag = compoundtag.getBoolean("HasNectar");
            return new ArmadilloHiveBlockEntity.Occupant(CustomData.of(compoundtag), 0, flag ? 2400 : 600);
        }

        public static ArmadilloHiveBlockEntity.Occupant create(int p_331115_) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.RESOURCE_ARMADILLO.get()).toString());
            return new ArmadilloHiveBlockEntity.Occupant(CustomData.of(compoundtag), p_331115_, 600);
        }

        @Nullable
        public Entity createEntity(Level p_331790_, BlockPos p_330712_) {
            CompoundTag compoundtag = this.entityData.copyTag();
            Entity entity = EntityType.loadEntityRecursive(compoundtag, p_331790_, p_331097_ -> p_331097_);
            if (entity != null && entity.getType().is(ModTags.Entities.ARMADILLO_HIVE_INHABITORS)) {
                entity.setNoGravity(true);
                if (entity instanceof ResourceArmadilloEntity ResourceArmadilloEntity) {
                    ResourceArmadilloEntity.setHivePos(p_330712_);
                    setBeeReleaseData(this.ticksInHive, ResourceArmadilloEntity);
                }

                return entity;
            } else {
                return null;
            }
        }

        private static void setBeeReleaseData(int p_331728_, ResourceArmadilloEntity p_331988_) {
            int i = p_331988_.getAge();
            if (i < 0) {
                p_331988_.setAge(Math.min(0, i + p_331728_));
            } else if (i > 0) {
                p_331988_.setAge(Math.max(0, i - p_331728_));
            }

            p_331988_.setInLoveTime(Math.max(0, p_331988_.getInLoveTime() - p_331728_));
        }
    }
}
