package net.zuperz.resource_armadillo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.zuperz.resource_armadillo.ResourceArmadillo;

import java.util.concurrent.CompletableFuture;


public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {


    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.REINFORCED_DEEPSLATE)
                .pattern("BAB")
                .pattern("BAB")
                .pattern("BAB")
                .define('A', Blocks.DEEPSLATE)
                .define('B', Items.BONE)
                .unlockedBy("has_deepslate", has(Blocks.OBSIDIAN))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "reinforced_deepslate"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.REINFORCED_DEEPSLATE, 2)
                .requires(Blocks.REINFORCED_DEEPSLATE)
                .requires(Blocks.BONE_BLOCK)
                .requires(Blocks.DEEPSLATE)
                .unlockedBy("has_reinforced_deepslate", has(Blocks.OBSIDIAN))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "reinforced_deepslate_2x"));
    }
}