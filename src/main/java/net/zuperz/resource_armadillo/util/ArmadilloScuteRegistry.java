package net.zuperz.resource_armadillo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.ModCreativeModeTabs;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;

import java.util.*;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ArmadilloScuteRegistry {
    private static final ArmadilloScuteRegistry INSTANCE = new ArmadilloScuteRegistry();

    private Map<ResourceLocation, ArmadilloScuteType> ArmadilloScuteTypes = new LinkedHashMap<>();

    private static boolean allowRegistration = false;

    public void register(ArmadilloScuteType armadilloScuteType) {
        if (this.allowRegistration && armadilloScuteType.isEnabled()) {
            if (this.ArmadilloScuteTypes.values().stream()
                    .noneMatch(c -> c.getName().equals(armadilloScuteType.getName()))) {
                this.ArmadilloScuteTypes.put(armadilloScuteType.getId(), armadilloScuteType);
            }
        }
    }

    public List<ArmadilloScuteType> getArmadilloScuteTypes() {
        return List.copyOf(this.ArmadilloScuteTypes.values());
    }

    public ArmadilloScuteType getArmadilloScuteTypeById(ResourceLocation id) {
        return this.ArmadilloScuteTypes.get(id);
    }

    public ArmadilloScuteType getArmadilloScuteTypeByName(String name) {
        return this.ArmadilloScuteTypes.values().stream().filter(c -> name.equals(c.getName())).findFirst().orElse(null);
    }

    public static ArmadilloScuteRegistry getInstance() {
        return INSTANCE;
    }

    public static void setAllowRegistration(boolean allowed) {
        allowRegistration = allowed;
    }

    public void onRegisterItems(RegisterEvent.RegisterHelper<Item> registry) {
        var ArmadilloScuteTypes = this.ArmadilloScuteTypes.values();

        ArmadilloScuteTypes.stream().filter(ArmadilloScuteType::shouldRegisterEssenceItem).forEach(scute -> {
            var essence = scute.getEssenceItem();
            if (essence == null) {
                var defaultEssence = new ArmadilloScuteItem(scute);
                essence = defaultEssence;
                scute.setEssenceItem(() -> defaultEssence, true);
                setAllowRegistration(true);
            }

            var id = ResourceLocation.fromNamespaceAndPath((ResourceArmadillo.MOD_ID), (scute.getNameWithSuffix("scute")));
            registry.register(id, essence);
        });
    }
}