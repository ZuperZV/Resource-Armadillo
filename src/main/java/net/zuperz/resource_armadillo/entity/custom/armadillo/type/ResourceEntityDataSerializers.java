package net.zuperz.resource_armadillo.entity.custom.armadillo.type;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;

public class ResourceEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, ResourceArmadillo.MOD_ID);

    public static final EntityDataSerializer<ResourceArmadilloEntity.ArmadilloState> RESOURCE_ARMADILLO_STATE = EntityDataSerializer.forValueType(
            ResourceArmadilloEntity.ArmadilloState.STREAM_CODEC
    );

    private ResourceEntityDataSerializers() {
    }

    public static void register(IEventBus eventBus) {
        ENTITY_DATA_SERIALIZERS.register(eventBus);
    }
}