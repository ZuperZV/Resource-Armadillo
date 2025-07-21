package net.zuperz.resource_armadillo.entity.custom.armadillo;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.zuperz.resource_armadillo.ResourceArmadillo;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ResourceArmadillo.MOD_ID);

    public static final Supplier<EntityType<ResourceArmadilloEntity>> RESOURCE_ARMADILLO =
            ENTITY_TYPES.register("resource_armadillo", () -> EntityType.Builder.of(ResourceArmadilloEntity::new, MobCategory.CREATURE)
                    .sized(0.7F, 0.65F).eyeHeight(0.26F).clientTrackingRange(10).build("resource_armadillo"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
