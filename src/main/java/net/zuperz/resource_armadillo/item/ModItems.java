package net.zuperz.resource_armadillo.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.item.custom.RainbowItem;
import net.zuperz.resource_armadillo.item.custom.RainbowSpawnEggItem;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import java.util.List;
import java.util.Objects;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ResourceArmadillo.MOD_ID);

    public static final DeferredItem<Item> IRON_BRUSH = ITEMS.registerItem("iron_brush",
            BrushItem::new, (new Item.Properties().durability(94)));

    public static final DeferredItem<Item> ARMADILLO_PART = ITEMS.registerItem("armadillo_part",
            Item::new, (new Item.Properties().durability(94)));

    public static final DeferredItem<Item> RESOURCE_ARMADILLO_SPAWN_EGG = ITEMS.register("resource_armadillo_spawn_egg",
            () -> new RainbowSpawnEggItem(ModEntities.RESOURCE_ARMADILLO, 11366765, new Item.Properties()));

    public static final DeferredItem<Item> ARMADILLO_TAB = ITEMS.register("armadillo_tab",
            () -> new RainbowItem(new Item.Properties()));

    @SubscribeEvent
    public static void onRegisterItems(RegisterEvent event) {
        event.register(Registries.ITEM, registry -> {
            ArmadilloScuteRegistry.getInstance().onRegisterItems(registry);
        });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(ModItems::onRegisterItems);
    }
}