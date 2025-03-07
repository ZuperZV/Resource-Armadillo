package net.zuperz.resource_armadillo.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ResourceArmadillo.MOD_ID);

    public static final Supplier<CreativeModeTab> RESOURCE_ARMADILLO_TAB =
            CREATIVE_MODE_TABS.register("resource_armadillo_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.resource_armadillo.resource_armadillo_tab"))
                    .icon(() -> new ItemStack(ModItems.ARMADILLO_TAB.get()))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.RESOURCE_ARMADILLO_SPAWN_EGG.get());
                        pOutput.accept(Items.ARMADILLO_SPAWN_EGG);
                        pOutput.accept(Items.SPIDER_EYE);

                        pOutput.accept(ModBlocks.ROOST.get());
                        pOutput.accept(ModBlocks.ARMADILLO_HIVE.get());
                        pOutput.accept(ModBlocks.NEST.get());

                        pOutput.accept(ModBlocks.CHROMIUM_ORE.get());
                        pOutput.accept(ModBlocks.CHROMIUM_DEEPSLATE_ORE.get());
                        pOutput.accept(ModItems.RAW_CHROMIUM.get());
                        pOutput.accept(ModItems.CHROMIUM_INGOT.get());
                        pOutput.accept(ModBlocks.CHROMIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.CENTRIFUGE.get());

                        pOutput.accept(Items.BRUSH);
                        pOutput.accept(ModItems.GOLD_BRUSH.get());
                        pOutput.accept(ModItems.IRON_BRUSH.get());
                        pOutput.accept(ModItems.CHROMIUM_BRUSH.get());
                        pOutput.accept(ModItems.DIAMOND_BRUSH.get());
                        pOutput.accept(ModItems.NETHERITE_BRUSH.get());
                        pOutput.accept(Items.ARMADILLO_SCUTE);
                        pOutput.accept(ModItems.ARMADILLO_PART.get());
                        pOutput.accept(Items.WOLF_ARMOR);

                        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();
                        for (var scute : registry.getArmadilloScuteTypes()) {
                            if (scute.isEnabled() && !scute.getName().equals("none")) {

                                String scuteName = scute.getName() + "_scute";
                                ResourceLocation scuteLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scuteName);
                                Item scuteItem = BuiltInRegistries.ITEM.get(scuteLocation);
                                pOutput.accept(new ItemStack(scuteItem));

                                String essenceName = scute.getName() + "_essence";
                                ResourceLocation essenceLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, essenceName);
                                Item essenceItem = BuiltInRegistries.ITEM.get(essenceLocation);
                                pOutput.accept(new ItemStack(essenceItem));

                                String armorName = scute.getName() + "_armor";
                                ResourceLocation armorLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, armorName);
                                Item armorItem = BuiltInRegistries.ITEM.get(armorLocation);
                                pOutput.accept(new ItemStack(armorItem));
                            }
                        }
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
