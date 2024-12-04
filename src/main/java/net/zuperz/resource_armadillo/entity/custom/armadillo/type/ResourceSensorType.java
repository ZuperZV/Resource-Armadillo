package net.zuperz.resource_armadillo.entity.custom.armadillo.type;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.*;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ai.ResourceArmadilloAi;

public class ResourceSensorType<U extends Sensor<?>> {

    public static final SensorType<MobSensor<ResourceArmadilloEntity>> ARMADILLO_SCARE_DETECTED = register(
            "armadillo_scare_detected", () -> new MobSensor<>(5, ResourceArmadilloEntity::isScaredBy, ResourceArmadilloEntity::canStayRolledUp, MemoryModuleType.DANGER_DETECTED_RECENTLY, 80)
    );

    public static final SensorType<TemptingSensor> ARMADILLO_TEMPTATIONS = register(
            "armadillo_temptations", () -> new TemptingSensor(ResourceArmadilloAi.getTemptations())
    );

    private final Supplier<U> factory;

    public ResourceSensorType(Supplier<U> pFactory) {
        this.factory = pFactory;
    }

    public U create() {
        return this.factory.get();
    }

    private static <U extends Sensor<?>> net.minecraft.world.entity.ai.sensing.SensorType<U> register(String pKey, Supplier<U> pSensorSupplier) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, ResourceLocation.withDefaultNamespace(pKey), new net.minecraft.world.entity.ai.sensing.SensorType<>(pSensorSupplier));
    }
}
