package net.zuperz.resource_armadillo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.custom.ArmadilloAnimalArmorItem;
import net.zuperz.resource_armadillo.item.custom.ArmadilloEssenceScuteItem;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;

import java.util.*;

/*
 *  MIT License
 *  Copyright (c) 2020 BlakeBr0
 *
 *  This code is licensed under the "MIT License"
 *  https://github.com/BlakeBr0/MysticalCustomization/blob/1.21/LICENSE
 *
 *  Modified by: ZuperZ
 */

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
        var armadilloScuteTypes = this.ArmadilloScuteTypes.values();

        armadilloScuteTypes.stream().filter(ArmadilloScuteType::shouldRegisterEssenceItem).forEach(scute -> {
            var essence = scute.getEssenceItem();
            if (essence == null) {
                var defaultEssence = new ArmadilloScuteItem(scute);
                essence = defaultEssence;
                scute.setEssenceItem(() -> defaultEssence, true);
                setAllowRegistration(true);
            }

            var scuteId = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scute.getNameWithSuffix("scute"));
            registry.register(scuteId, essence);

            var essenceItem = new ArmadilloEssenceScuteItem(scute);
            var essenceId = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scute.getNameWithSuffix("essence"));
            registry.register(essenceId, essenceItem);


            var armorItem = new ArmadilloAnimalArmorItem(
                    ArmorMaterials.ARMADILLO,
                    scute,
                    ArmadilloAnimalArmorItem.BodyType.CANINE,
                    true,
                    new Item.Properties().durability(ArmorItem.Type.BODY.getDurability(5)),
                    scute
            );

            var armorId = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scute.getNameWithSuffix("armor"));
            registry.register(armorId, armorItem);
        });
    }
}