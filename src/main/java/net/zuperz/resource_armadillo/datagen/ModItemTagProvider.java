package net.zuperz.resource_armadillo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                              CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, ResourceArmadillo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(Tags.Items.TOOLS_BRUSH)
                .add(Items.BRUSH)
                .add(ModItems.GOLD_BRUSH.get())
                .add(ModItems.IRON_BRUSH.get())
                .add(ModItems.CHROMIUM_BRUSH.get())
                .add(ModItems.DIAMOND_BRUSH.get())
                .add(ModItems.NETHERITE_BRUSH.get());

        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(Items.BRUSH,
                        ModItems.GOLD_BRUSH.get(),
                        ModItems.IRON_BRUSH.get(),
                        ModItems.CHROMIUM_BRUSH.get(),
                        ModItems.DIAMOND_BRUSH.get(),
                        ModItems.NETHERITE_BRUSH.get());

        this.tag(Tags.Items.INGOTS)
                .add(ModItems.CHROMIUM_INGOT.get());

        this.tag(Tags.Items.RAW_MATERIALS)
                .add(ModItems.RAW_CHROMIUM.get());
    }
}