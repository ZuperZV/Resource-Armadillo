package net.zuperz.resource_armadillo.screen.slot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.screen.RoostMenu;

import java.util.function.Supplier;


public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, ResourceArmadillo.MOD_ID);

    public static final Supplier<MenuType<RoostMenu>> ATOMIC_OVEN_MENU = MENUS.register("atomic_oven_menu",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new RoostMenu(windowId, inv.player, data.readBlockPos())));

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                            String name) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
