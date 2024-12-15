package net.zuperz.resource_armadillo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ResourceArmadillo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ROOST.get())
                .add(ModBlocks.ARMADILLO_HIVE.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.ROOST.get())
                .add(ModBlocks.ARMADILLO_HIVE.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.ARMADILLO_HIVE.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.ARMADILLO_HIVE.get());
    }
}