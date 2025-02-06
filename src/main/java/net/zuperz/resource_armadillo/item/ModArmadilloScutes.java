package net.zuperz.resource_armadillo.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModArmadilloScutes {

    private static final List<ArmadilloScuteType> SCUTE_TYPES = new ArrayList<>();

    public static final ArmadilloScuteType NONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "none"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/white_armadillo.png"), Ingredient.of(Items.AIR), true));

    public static final ArmadilloScuteType EMERALD = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "emerald"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/emerald_armadillo.png"), Ingredient.of(Items.EMERALD), true));
    public static final ArmadilloScuteType DIAMOND = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "diamond"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/diamond_armadillo.png"), Ingredient.of(Items.DIAMOND), true));
    public static final ArmadilloScuteType STONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "stone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/stone_armadillo.png"), Ingredient.of(Blocks.STONE), true));
    public static final ArmadilloScuteType COBBLESTONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "cobblestone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/cobblestone_armadillo.png"), Ingredient.of(Blocks.COBBLESTONE), true));
    public static final ArmadilloScuteType FLINT = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "flint"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/flint_armadillo.png"), Ingredient.of(Items.FLINT), true));
    public static final ArmadilloScuteType COAL = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "coal"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/coal_armadillo.png"), Ingredient.of(Items.COAL), true));

    private static ArmadilloScuteType register(ArmadilloScuteType type) {
        type.setEssenceItem(() -> new ArmadilloScuteItem(type), true);
        SCUTE_TYPES.add(type);
        return type;
    }

    public static void registerAll(ArmadilloScuteRegistry registry) {
        ArmadilloScuteRegistry.setAllowRegistration(true);
        SCUTE_TYPES.forEach(registry::register);
    }
}
