package net.zuperz.resource_armadillo.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;
import net.zuperz.resource_armadillo.util.ScuteJsonLoader;

import java.util.ArrayList;
import java.util.List;

public class ModArmadilloScutes {

    public static final List<ArmadilloScuteType> SCUTE_TYPES = new ArrayList<>();

    public static final ArmadilloScuteType NONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "none"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/white_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/wolf_armor.png"), true));

    public static final ArmadilloScuteType DIRT = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "dirt"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/dirt_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/dirt_armor.png"), true));;
    public static final ArmadilloScuteType COBBLESTONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "cobblestone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/cobblestone_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/cobblestone_armor.png"), true));
    public static final ArmadilloScuteType FLINT = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "flint"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/flint_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/flint_armor.png"), true));
    public static final ArmadilloScuteType STONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "stone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/stone_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/stone_armor.png"), true));
    public static final ArmadilloScuteType SAND = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "sand"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/sand_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/sand_armor.png"), true));
    public static final ArmadilloScuteType CLAY = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "clay"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/clay_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/clay_armor.png"), true));
    public static final ArmadilloScuteType NETHERRACK = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "netherrack"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/netherrack_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/netherrack_armor.png"), true));

    public static final ArmadilloScuteType COAL = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "coal"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/coal_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/coal_armor.png"), true));
    public static final ArmadilloScuteType IRON = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "iron"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/iron_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/iron_armor.png"), true));
    public static final ArmadilloScuteType DIAMOND = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "diamond"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/diamond_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/diamond_armor.png"), true));
    public static final ArmadilloScuteType QUARTZ = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "quartz"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/quartz_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/quartz_armor.png"), true));


    public static final ArmadilloScuteType CHROMIUM = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "chromium"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/chromium_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/chromium_armor.png"), true));
    public static final ArmadilloScuteType EMERALD = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "emerald"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/emerald_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/emerald_armor.png"), true));

    private static ArmadilloScuteType register(ArmadilloScuteType type) {
        type.setEssenceItem(() -> new ArmadilloScuteItem(type), true);
        SCUTE_TYPES.add(type);
        return type;
    }

    public static void registerAll(ArmadilloScuteRegistry registry) {
        ArmadilloScuteRegistry.setAllowRegistration(true);

        SCUTE_TYPES.forEach(registry::register);

        ScuteJsonLoader.loadScutesFromJson();

        ScuteJsonLoader.getLoadedScutes().forEach(scute -> {
            registry.register(scute);
            SCUTE_TYPES.add(scute);
        });
    }
}
