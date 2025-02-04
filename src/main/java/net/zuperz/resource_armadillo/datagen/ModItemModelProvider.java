package net.zuperz.resource_armadillo.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ResourceArmadillo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModItems.RESOURCE_ARMADILLO_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ARMADILLO_TAB.getId().getPath(), mcLoc("resource_armadillo:item/template_resource_armadillo"));

        basicItem(ModItems.IRON_BRUSH.get());
        basicItem(ModItems.ARMADILLO_PART.get());

        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();
        for (var scute : registry.getArmadilloScuteTypes()) {
            if (scute.isEnabled()) {
                String scuteName = scute.getName() + "_scute";
                ResourceLocation scuteLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scuteName);
                Item scuteItem = BuiltInRegistries.ITEM.get(scuteLocation);
                if (scuteItem != null) {
                    basicItem(scuteItem);
                }
            }
        }
    }

    private ItemModelBuilder handheldItem(DeferredItem<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID,"block/" + item.getId().getPath()));
    }
    
    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

}