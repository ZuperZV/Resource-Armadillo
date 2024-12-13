package net.zuperz.resource_armadillo.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.item.custom.ResourceArmadilloScuteItem;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ResourceArmadillo.MOD_ID);

    public static final DeferredItem<Item> IRON_BRUSH = ITEMS.registerItem("iron_brush",
            BrushItem::new, (new Item.Properties().durability(94)));

    public static final DeferredItem<Item> RESOURCE_ARMADILLO_SPAWN_EGG = ITEMS.register("resource_armadillo_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.RESOURCE_ARMADILLO, 0x282828, 0xac9781, new Item.Properties()));

    //public static final DeferredItem<Item> RESOURCE_ARMADILLO_SCUTE = ITEMS.register("resource_armadillo_scute",
    //        () -> new ResourceArmadilloScuteItem(new Item.Properties()));

    public static final DeferredItem<Item> DIAMOND_ARMADILLO_SCUTE = ITEMS.register("diamond_armadillo_scute",
            () -> new ResourceArmadilloScuteItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}