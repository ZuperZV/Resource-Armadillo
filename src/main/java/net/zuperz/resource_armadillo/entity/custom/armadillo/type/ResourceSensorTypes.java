package net.zuperz.resource_armadillo.entity.custom.armadillo.type;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.*;
import net.minecraft.world.entity.animal.armadillo.ArmadilloAi;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ai.ResourceArmadilloAi;

public class ResourceSensorTypes<U extends Sensor<?>> {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, ResourceArmadillo.MOD_ID);

    public static final DeferredHolder<SensorType<?>, SensorType<MobSensor<ResourceArmadilloEntity>>> ARMADILLO_SCARE_DETECTED = SENSOR_TYPES.register(
            "armadillo_scare_detected",
            () -> new SensorType<>(() -> new MobSensor<>(5, ResourceArmadilloEntity::isScaredBy, ResourceArmadilloEntity::canStayRolledUp, MemoryModuleType.DANGER_DETECTED_RECENTLY, 80))
    );

    public static final DeferredHolder<SensorType<?>, SensorType<TemptingSensor>> ARMADILLO_TEMPTATIONS = SENSOR_TYPES.register(
            "armadillo_temptations",
            () -> new SensorType<>(() -> new TemptingSensor(ResourceArmadilloAi.getTemptations()))
    );

    public static void register(IEventBus eventBus) {
        SENSOR_TYPES.register(eventBus);
    }
}
