package net.zuperz.resource_armadillo.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.block.entity.custom.ArmadilloHiveBlockEntity;
import net.zuperz.resource_armadillo.block.entity.custom.AtomicOvenBlockEntity;

import java.util.function.Supplier;


public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceArmadillo.MOD_ID);

    public static final Supplier<BlockEntityType<AtomicOvenBlockEntity>> ATOMIC_OVEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("atomic_oven_be", () -> BlockEntityType.Builder.of(
                    AtomicOvenBlockEntity::new, ModBlocks.ATOMIC_OVEN.get()).build(null));

    public static final Supplier<BlockEntityType<ArmadilloHiveBlockEntity>> ARMADILLO_HIVE =
            BLOCK_ENTITIES.register("armadillo_hive", () -> BlockEntityType.Builder.of(
                    ArmadilloHiveBlockEntity::new, ModBlocks.ARMADILLO_HIVE.get()).build(null));



    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}