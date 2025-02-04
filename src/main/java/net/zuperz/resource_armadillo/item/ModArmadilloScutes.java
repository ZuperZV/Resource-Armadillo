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

    public static final ArmadilloScuteType EMERALD = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "emerald"), Ingredient.of(Items.EMERALD), false));
    public static final ArmadilloScuteType DIAMOND = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "diamond"), Ingredient.of(Items.DIAMOND), true));
    public static final ArmadilloScuteType STONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "stone"), Ingredient.of(Blocks.STONE), true));

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
