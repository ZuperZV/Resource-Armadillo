package net.zuperz.resource_armadillo.datagen;

import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.ModArmadilloScutes;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ResourceArmadillo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModItems.RESOURCE_ARMADILLO_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ARMADILLO_TAB.getId().getPath(), mcLoc("resource_armadillo:item/template_resource_armadillo"));

        basicItem(ModItems.GOLD_BRUSH.get());
        basicItem(ModItems.IRON_BRUSH.get());
        basicItem(ModItems.CHROMIUM_BRUSH.get());
        basicItem(ModItems.DIAMOND_BRUSH.get());
        basicItem(ModItems.NETHERITE_BRUSH.get());

        basicItem(ModItems.ARMADILLO_PART.get());

        basicItem(ModItems.CHROMIUM_INGOT.get());
        basicItem(ModItems.RAW_CHROMIUM.get());

        for (ArmadilloScuteType scute : ModArmadilloScutes.SCUTE_TYPES) {
            if (scute == ModArmadilloScutes.NONE) continue;

            if (!scute.getName().equals("none")) {
                String scuteName = scute.getName() + "_scute";
                 basicModItem(scuteName);

                String essenceName = scute.getName() + "_essence";
                basicModItem(essenceName);

                String armorName = scute.getName() + "_armor";
                    generateItemWithWolfArmorOverlay(armorName);
            }
        }
    }

    public ItemModelBuilder basicModItem(String item) {
        return getBuilder(item)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "item/" + item));
    }

    private ItemModelBuilder generateItemWithWolfArmorOverlay(String itemName) {
        return getBuilder(itemName)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "item/" + itemName))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "item/wolf_armor_overlay"));
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