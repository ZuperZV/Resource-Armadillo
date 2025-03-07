package net.zuperz.resource_armadillo.datagen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.datagen.custom.BreedingRecipeBuilder;
import net.zuperz.resource_armadillo.datagen.custom.CentrifugeRecipeBuilder;
import net.zuperz.resource_armadillo.datagen.custom.NestRecipeBuilder;
import net.zuperz.resource_armadillo.datagen.custom.RoostRecipeBuilder;
import net.zuperz.resource_armadillo.item.ModArmadilloScutes;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.RoostRecipe;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pWriter) {

        fourBlockStorageRecipes(pWriter, RecipeCategory.BUILDING_BLOCKS, ModItems.CHROMIUM_INGOT.get(), RecipeCategory.MISC,
                ModBlocks.CHROMIUM_BLOCK.get());

        rawToIngot(ModItems.RAW_CHROMIUM.get(), RecipeCategory.MISC, ModItems.CHROMIUM_INGOT.get(), 0.2f, 300, pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CENTRIFUGE.get())
                .pattern("A A")
                .pattern("ACA")
                .pattern("DBD")
                .define('A', ModItems.CHROMIUM_INGOT)
                .define('B', ModBlocks.CHROMIUM_BLOCK)
                .define('C', Blocks.GLASS)
                .define('D', Items.REDSTONE)
                .unlockedBy("has_chromium_ingot", inventoryTrigger(ItemPredicate.Builder.item().
                        of(ModItems.CHROMIUM_INGOT).build()))
                .save(pWriter);

        essenceShapedRecipe(
                Items.IRON_INGOT, 5,
                new String[]{
                        " A ",
                        " A ",
                        " A "
                },
                Map.of('A', "iron_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.GOLD_INGOT, 4,
                new String[]{
                        "A A",
                        " A ",
                        "A A"
                },
                Map.of('A', "gold_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.LAPIS_LAZULI, 6,
                new String[]{
                        "AA",
                        "AA",
                        "AA"
                },
                Map.of('A', "lapis_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.REDSTONE, 8,
                new String[]{
                        " A ",
                        "A A",
                        " A "
                },
                Map.of('A', "redstone_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.DIAMOND, 3,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "diamond_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.EMERALD, 3,
                new String[]{
                        "AAA",
                        " A ",
                        "AAA"
                },
                Map.of('A', "emerald_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.COPPER_INGOT, 5,
                new String[]{
                        "AAA",
                        "   ",
                        "AAA"
                },
                Map.of('A', "copper_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.OBSIDIAN.asItem(), 2,
                new String[]{
                        "A A",
                        " A ",
                        "A A"
                },
                Map.of('A', "obsidian_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.PRISMARINE_CRYSTALS, 10,
                new String[]{
                        " A ",
                        "AAA",
                        " A "
                },
                Map.of('A', "prismarine_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.DIRT.asItem(), 10,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "dirt_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.COBBLESTONE.asItem(), 16,
                new String[]{
                        "A A",
                        " A ",
                        "A A"
                },
                Map.of('A', "cobblestone_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.FLINT, 6,
                new String[]{
                        "A A",
                        "   ",
                        "A A"
                },
                Map.of('A', "flint_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.GRAVEL.asItem(), 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "flint_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.STONE.asItem(), 16,
                new String[]{
                        "AAA",
                        " A ",
                        "AAA"
                },
                Map.of('A', "stone_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.STONE.asItem(), 32,
                new String[]{
                        "AAA",
                        "BAB",
                        "AAA"
                },
                Map.of('A', "stone_essence",
                        'B', "cobblestone_essence"),
                pWriter,
                "stone_2"
        );

        essenceShapedRecipe(
                Blocks.DEEPSLATE.asItem(), 10,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "deepslate_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.DEEPSLATE.asItem(), 15,
                new String[]{
                        "AAA",
                        "B B",
                        "AAA"
                },
                Map.of('A', "deepslate_essence",
                        'B', "stone_essence"),
                pWriter,
                "deepslate_2"
        );

        essenceShapedRecipe(
                Blocks.DEEPSLATE.asItem(), 10,
                new String[]{
                        "ABA",
                        "B B",
                        "ABA"
                },
                Map.of('A', "deepslate_essence",
                        'B', "cobblestone_essence"),
                pWriter,
                "deepslate_3"
        );

        essenceShapedRecipe(
                Blocks.SAND.asItem(), 16,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "sand_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.CLAY.asItem(), 16,
                new String[]{
                        " A ",
                        "AAA",
                        "A A"
                },
                Map.of('A', "clay_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.CLAY_BALL, 32,
                new String[]{
                        " A ",
                        "AAA",
                        "AAA"
                },
                Map.of('A', "clay_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.HONEYCOMB, 16,
                new String[]{
                        " A ",
                        "AAA",
                        "AAA"
                },
                Map.of('A', "honey_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.HONEY_BOTTLE, 8,
                new String[]{
                        " A ",
                        "AAA",
                        "A A"
                },
                Map.of('A', "honey_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.AMETHYST_SHARD, 10,
                new String[]{
                        " AA",
                        "AAA",
                        "AA "
                },
                Map.of('A', "amethyst_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.SLIME_BALL, 8,
                new String[]{
                        " A ",
                        "AAA",
                        " A "
                },
                Map.of('A', "slime_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.SLIME_BLOCK.asItem(), 2,
                new String[]{
                        " A ",
                        "AAA",
                        "AAA"
                },
                Map.of('A', "slime_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.BLACK_DYE, 8,
                new String[]{
                        " A ",
                        "AAA",
                        "   "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.BLUE_DYE, 8,
                new String[]{
                        "   ",
                        "AAA",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.BROWN_DYE, 8,
                new String[]{
                        " A ",
                        "AAA",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.CYAN_DYE, 8,
                new String[]{
                        " A ",
                        " AA",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.GRAY_DYE, 8,
                new String[]{
                        " A ",
                        "AA ",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.GREEN_DYE, 8,
                new String[]{
                        "AA ",
                        " A ",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.LIGHT_BLUE_DYE, 8,
                new String[]{
                        " AA",
                        " A ",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.LIGHT_GRAY_DYE, 8,
                new String[]{
                        "AAA",
                        " A ",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.LIME_DYE, 8,
                new String[]{
                        " A ",
                        " A ",
                        " A "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.MAGENTA_DYE, 8,
                new String[]{
                        " A ",
                        " A ",
                        " AA"
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.ORANGE_DYE, 8,
                new String[]{
                        " A ",
                        " A ",
                        "AAA"
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.PINK_DYE, 8,
                new String[]{
                        " A ",
                        " A ",
                        "AA "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.RED_DYE, 8,
                new String[]{
                        " AA",
                        " A ",
                        "AA "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.WHITE_DYE, 8,
                new String[]{
                        "AA ",
                        " A ",
                        "AA "
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.YELLOW_DYE, 8,
                new String[]{
                        " AA",
                        " A ",
                        " AA"
                },
                Map.of('A', "dye_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Blocks.NETHERRACK.asItem(), 8,
                new String[]{
                        "AAA",
                        " A ",
                        "AAA"
                },
                Map.of('A', "netherrack_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.COAL, 12,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "coal_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.CHARCOAL, 12,
                new String[]{
                        "A A",
                        "AAA",
                        "A A"
                },
                Map.of('A', "coal_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.MAGMA_CREAM, 4,
                new String[]{
                        "A A",
                        "AAA",
                        "A A"
                },
                Map.of('A', "magma_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.BLAZE_ROD, 1,
                new String[]{
                        "  A",
                        " A ",
                        "A  "
                },
                Map.of('A', "magma_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                Items.QUARTZ, 12,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "quartz_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                ModItems.CHROMIUM_INGOT.get(), 12,
                new String[]{
                        "AAA",
                        " A ",
                        "AAA"
                },
                Map.of('A', "chromium_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                ModItems.CHROMIUM_INGOT.get(), 3,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "chromium_scute"),
                pWriter,
                "_scute"
        );

        essenceShapedRecipe(
                Items.NETHERITE_SCRAP, 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "netherite_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                "powah:steel_energized", 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "steel_energized_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                "powah:crystal_niotic", 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "crystal_niotic_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                "powah:crystal_blazing", 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "crystal_blazing_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                "powah:crystal_spirited", 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "crystal_spirited_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                "powah:crystal_nitro", 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "crystal_nitro_essence"),
                pWriter,
                ""
        );

        essenceShapedRecipe(
                "powah:uraninite", 8,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of('A', "uraninite_essence"),
                pWriter,
                ""
        );

        //powah
        RoostRecipe("steel_energized_scute", Tags.Items.INGOTS, "powah:steel_energized", pWriter);
        RoostRecipe("crystal_niotic_scute", Items.DIAMOND, "powah:crystal_niotic", pWriter);
        RoostRecipe("crystal_blazing_scute", Items.BLAZE_ROD, "powah:crystal_blazing", pWriter);
        RoostRecipe("crystal_spirited_scute", Items.EMERALD, "powah:crystal_spirited", pWriter);
        RoostRecipe("crystal_nitro_scute", Items.REDSTONE, "powah:crystal_nitro", pWriter);
        RoostRecipe("uraninite_scute", Items.ENDER_EYE, "powah:uraninite", pWriter);


        // Ground Resource
        RoostRecipe("dirt_scute", Blocks.DIRT.asItem(), Items.WHEAT, pWriter);
        RoostRecipe("cobblestone_scute", Blocks.COBBLESTONE.asItem(), "dirt_scute", pWriter);
        RoostRecipe("deepslate_scute", Blocks.COBBLED_DEEPSLATE.asItem(), "dirt_scute", pWriter);
        RoostRecipe("sand_scute", Blocks.SAND.asItem(), "cobblestone_scute", pWriter);
        RoostRecipe("dye_scute", ItemTags.FLOWERS, "sand_scute", pWriter);
        RoostRecipe("netherrack_scute", ItemTags.FLOWERS, "sand_scute", pWriter);

        RoostRecipe("flint_scute", Items.FLINT, "cobblestone_scute", pWriter);
        RoostRecipe("coal_scute", Items.COAL, "flint_scute", pWriter);
        RoostRecipe("iron_scute", Items.IRON_INGOT, "coal_scute", pWriter);
        RoostRecipe("copper_scute", Items.COPPER_INGOT, "iron_scute", pWriter);
        RoostRecipe("redstone_scute", Items.REDSTONE, "quartz_scute", pWriter);
        RoostRecipe("lapis_scute", Items.LAPIS_LAZULI, "quartz_scute", pWriter);

        // Gem
        RoostRecipe("quartz_scute", Items.QUARTZ, "iron_scute", pWriter);
        RoostRecipe("diamond_scute", Items.DIAMOND, "iron_scute", pWriter);
        RoostRecipe("emerald_scute", Items.EMERALD, "diamond_scute", pWriter);
        RoostRecipe("amethyst_scute", Items.AMETHYST_SHARD, "quartz_scute", pWriter);

        // Special materials
        hiveBreeding("magma_scute", "netherrack_scute", "slime_scute", pWriter);
        hiveBreeding("obsidian_scute", "clay_scute", "magma_scute", pWriter);

        RoostRecipe("slime_scute", Items.SLIME_BALL, "sand_scute", pWriter);
        RoostRecipe("prismarine_scute", Items.PRISMARINE_SHARD, "sand_scute", pWriter);
        RoostRecipe("honey_scute", Items.HONEYCOMB, "sand_scute", pWriter);

        RoostRecipe("chromium_scute", "obsidian_scute", "iron_scute", pWriter);

        // Gem
        hiveBreeding("gold_scute", "iron_scute", "copper_scute", pWriter);
        hiveBreeding("redstone_scute", "quartz_scute", "iron_scute", pWriter);
        hiveBreeding("lapis_scute", "iron_scute", "quartz_scute", pWriter);
        hiveBreeding("emerald_scute", "diamond_scute", "gold_scute", pWriter);
        hiveBreeding("prismarine_scute", "quartz_scute", "lapis_scute", pWriter);

        // Mob Tings
        hiveBreeding("slime_scute", "sand_scute", "clay_scute", pWriter);
        hiveBreeding("honey_scute", "sand_scute", "dye_scute", pWriter);

        hiveBreeding("stone_scute", "dirt_scute", "cobblestone_scute", pWriter);
        hiveBreeding("clay_scute", "sand_scute", "prismarine_scute", pWriter);
        hiveBreeding("netherite_scute", "obsidian_scute", "gold_scute", pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ROOST.get())
                .pattern("A A")
                .pattern("CDC")
                .pattern("EBE")
                .define('A', Tags.Items.RODS_WOODEN)
                .define('B', Blocks.FURNACE)
                .define('C', ItemTags.LOGS)
                .define('D', Blocks.HAY_BLOCK)
                .define('E', Tags.Items.GEMS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ARMADILLO_HIVE.get())
                .pattern("A A")
                .pattern("CBC")
                .pattern("CDC")
                .define('A', Tags.Items.RODS_WOODEN)
                .define('B', Blocks.HAY_BLOCK)
                .define('C', ItemTags.LOGS)
                .define('D', Tags.Items.GEMS)
                .unlockedBy("has_logs", has(ItemTags.LOGS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.NEST.get())
                .pattern("D D")
                .pattern("BDB")
                .pattern("CEC")
                .define('B', Blocks.HAY_BLOCK)
                .define('C', ItemTags.LOGS)
                .define('D', Tags.Items.GEMS)
                .define('E', ModBlocks.ARMADILLO_HIVE.get())
                .unlockedBy("has_hive", has(ModBlocks.ARMADILLO_HIVE.get()))
                .save(pWriter);


        netheriteSmithing(pWriter, Items.AIR, Items.BRUSH, Items.GOLD_INGOT, RecipeCategory.TOOLS, ModItems.GOLD_BRUSH.get());

        netheriteSmithing(pWriter, Items.AIR, ModItems.GOLD_BRUSH.get(), Items.IRON_INGOT, RecipeCategory.TOOLS, ModItems.IRON_BRUSH.get());

        netheriteSmithing(pWriter, Items.AIR, ModItems.IRON_BRUSH.get(), ModItems.CHROMIUM_INGOT.get(), RecipeCategory.TOOLS, ModItems.CHROMIUM_BRUSH.get());

        netheriteSmithing(pWriter, Items.AIR, ModItems.CHROMIUM_BRUSH.get(), Items.DIAMOND, RecipeCategory.TOOLS, ModItems.DIAMOND_BRUSH.get());

        netheriteSmithing(pWriter, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModItems.DIAMOND_BRUSH.get(), Items.NETHERITE_INGOT, RecipeCategory.TOOLS, ModItems.NETHERITE_BRUSH.get());


        for (var scute : ArmadilloScuteRegistry.getInstance().getArmadilloScuteTypes()) {
            if (scute == ModArmadilloScutes.NONE) continue;

            if (!scute.getName().equals("none")) {
                String scuteName = scute.getName() + "_scute";
                ItemStack scuteItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scuteName)), 1);

                String essenceName = scute.getName() + "_essence";
                ItemStack essenceItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, essenceName)), 1);


                String armorName = scute.getName() + "_armor";
                ItemStack armorItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, armorName)), 1);

                if (scuteItem != null && essenceItem != null && essenceItem != Items.AIR.getDefaultInstance()) {
                    CentrifugeRecipeBuilder.centrifuge(Ingredient.of(scuteItem), RecipeCategory.MISC, essenceItem)
                            .group("centrifuge_" + scute.getName())
                            .unlockedBy("has_" + scuteName, inventoryTrigger(ItemPredicate.Builder.item().of(scuteItem.getItem()).build()))
                            .save(pWriter, ResourceLocation.fromNamespaceAndPath("resource_armadillo", scute.getName() + "_crafted_from_centrifuge"));
                }

                if (scuteItem != null && armorItem != null && armorItem != Items.AIR.getDefaultInstance()) {
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, armorItem)
                            .pattern("A  ")
                            .pattern("AAA")
                            .pattern("A A")
                            .define('A', scuteItem.getItem())
                            .unlockedBy("has_" + armorName, inventoryTrigger(ItemPredicate.Builder.item().of(scuteItem.getItem()).build()))
                            .save(pWriter, ResourceLocation.fromNamespaceAndPath("resource_armadillo", armorName + "_crafted_from_" + scuteName));
                }

                if (scuteItem != null) {
                    //hiveScuteBreeding( String.valueOf(scuteItem), String.valueOf(scuteItem), String.valueOf(scuteItem), pWriter);
                    nestRecipe(String.valueOf(scuteItem), String.valueOf(scuteItem), pWriter);
                }
            }
        }
    }

    protected static void oreSmelting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    public static void smithingRecipe(RecipeOutput pRecipeOutput, Item ingredient1, Item ingredient2, Item ingredient3, RecipeCategory category, Item result) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(ingredient1.getDefaultInstance()), Ingredient.of(ingredient2.getDefaultInstance()), Ingredient.of(ingredient3.getDefaultInstance()), category, result)
                .unlocks("has_brush_ingot", has(Items.BRUSH))
                .save(pRecipeOutput, "_smithing");
    }


    protected static void netheriteSmithing(RecipeOutput pRecipeOutput, Item ingredient1, Item ingredient2, Item ingredient3, RecipeCategory p_248986_, Item p_250389_) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ingredient1), Ingredient.of(ingredient2), Ingredient.of(ingredient3), p_248986_, p_250389_
                )
                .unlocks("has_" + ingredient2.getDescriptionId(), has(ingredient2))
                .save(pRecipeOutput, getItemName(p_250389_) + "_smithing");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput pRecipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, ResourceArmadillo.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    protected static void fourBlockStorageRecipes(
            RecipeOutput p_301057_, RecipeCategory p_251203_, ItemLike p_251689_, RecipeCategory p_251376_, ItemLike p_248771_
    ) {
        fourBlockStorageRecipes(
                p_301057_, p_251203_, p_251689_, p_251376_, p_248771_, getSimpleRecipeName(p_248771_), null, getSimpleRecipeName(p_251689_), null
        );
    }

    protected static void fourBlockStorageRecipes(
            RecipeOutput p_301222_,
            RecipeCategory p_250083_,
            ItemLike p_250042_,
            RecipeCategory p_248977_,
            ItemLike p_251911_,
            String p_250475_,
            @Nullable String p_248641_,
            String p_252237_,
            @Nullable String p_250414_
    ) {
        ShapelessRecipeBuilder.shapeless(p_250083_, p_250042_, 4)
                .requires(p_251911_)
                .group(p_250414_)
                .unlockedBy(getHasName(p_251911_), has(p_251911_))
                .save(p_301222_, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, p_252237_));
        ShapedRecipeBuilder.shaped(p_248977_, p_251911_)
                .define('#', p_250042_)
                .pattern("##")
                .pattern("##")
                .group(p_248641_)
                .unlockedBy(getHasName(p_250042_), has(p_250042_))
                .save(p_301222_, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, p_250475_));
    }

    public static void rawToIngot(ItemLike rawItem, RecipeCategory category, ItemLike ingot, float experience, int cookingTime, RecipeOutput pWriter) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawItem), category, ingot, experience, cookingTime)
                .unlockedBy("has_" + getItemName(rawItem), inventoryTrigger(ItemPredicate.Builder.item().of(rawItem).build()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, getItemName(ingot) + "_from_smelting"));

        float blastingExperience = experience - 0.10f;
        int blastingTime = cookingTime - 100 >= 0 ? cookingTime - 100 : cookingTime;

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(rawItem), category, ingot, blastingExperience, blastingTime)
                .unlockedBy("has_" + getItemName(rawItem), inventoryTrigger(ItemPredicate.Builder.item().of(rawItem).build()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, getItemName(ingot) + "_from_blasting"));
    }

    private static void hiveBreeding(String output, String armadillo1, String armadillo2, RecipeOutput pWriter) {
        BreedingRecipeBuilder.breeding(
                        RecipeCategory.MISC,
                        new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("resource_armadillo", output))),
                        Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("resource_armadillo", armadillo1))),
                        Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("resource_armadillo", armadillo2))),
                        Ingredient.of(ItemTags.create(ResourceLocation.parse("armadillo_food"))),
                        Ingredient.of(ItemTags.create(ResourceLocation.parse("armadillo_food")))
                ).unlockedBy("has_" + armadillo1, inventoryTrigger(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("resource_armadillo", armadillo1))).build()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath("resource_armadillo", output + "_from_hive_breeding"));
    }

    private static void hiveScuteBreeding(String output, String armadillo1, String armadillo2, RecipeOutput pWriter) {
        BreedingRecipeBuilder.breeding(
                        RecipeCategory.MISC,
                        new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(output))),
                        Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(armadillo1))),
                        Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(armadillo2))),
                        Ingredient.of(ItemTags.create(ResourceLocation.parse("armadillo_food"))),
                        Ingredient.of(ItemTags.create(ResourceLocation.parse("armadillo_food")))
                ).unlockedBy("has_" + armadillo1, inventoryTrigger(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(armadillo1))).build()))
                .save(pWriter, ResourceLocation.parse(output + "from_2_of_the_same_scute_hive_breeding"));
    }

    private static void nestRecipe(String output, String armadillo1, RecipeOutput pWriter) {
        NestRecipeBuilder.nest(
                        RecipeCategory.MISC,
                        new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(output.replaceAll("^\\d+\\s+", "")))),
                        Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(armadillo1.replaceAll("^\\d+\\s+", ""))))
                ).unlockedBy("has_" + armadillo1.replaceAll("^\\d+\\s+", ""), inventoryTrigger(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(armadillo1.replaceAll("^\\d+\\s+", "")))).build()))
                .save(pWriter, ResourceLocation.parse(output.replaceAll("^\\d+\\s+", "") + "_from_nest"));
    }

    private static void essenceShapedRecipe(Object output, int count, String[] pattern, Map<Character, String> ingredients, RecipeOutput pWriter, String recipeName) {
        ItemStack outputStack;
        String outputName;

        if (output instanceof Item item) {
            outputStack = new ItemStack(item, count);
            outputName = BuiltInRegistries.ITEM.getKey(item).getPath();
        } else if (output instanceof String itemName) {
            outputStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)), count);
            outputName = ResourceLocation.parse(itemName).getPath();
        } else {
            throw new IllegalArgumentException("Output must be either an Item or a String representing an item name.");
        }

        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputStack);
        for (String line : pattern) {
            builder.pattern(line);
        }

        for (Map.Entry<Character, String> entry : ingredients.entrySet()) {
            builder.define(entry.getKey(), BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, entry.getValue())));
        }

        for (String ingredient : ingredients.values()) {
            builder.unlockedBy("has_" + ingredient, inventoryTrigger(
                    ItemPredicate.Builder.item()
                            .of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, ingredient)))
                            .build()
            ));
        }

        if (recipeName != null && !recipeName.isEmpty()) {
            outputName += "_" + recipeName;
        }

        builder.save(pWriter, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, outputName + "_essence_recipe"));
    }

    private static void RoostRecipe(Object output, Object ingredient1, Object ingredient2, RecipeOutput pWriter) {
        ItemStack outputStack;

        if (output instanceof Item item) {
            outputStack = new ItemStack(item, 1);
        } else if (output instanceof String itemName) {
            ResourceLocation itemLocation;

            if (itemName.contains(":")) {
                itemLocation = ResourceLocation.parse(itemName);
            } else {
                itemLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, itemName);
            }

            Item outputItem = BuiltInRegistries.ITEM.get(itemLocation);
            if (outputItem == Items.AIR) {
                throw new IllegalArgumentException("FEJL: Output-item '" + itemLocation + "' findes ikke i registrene!");
            }
            outputStack = new ItemStack(outputItem, 1);
        } else {
            throw new IllegalArgumentException("Output must be an Item or a String representing an item name.");
        }

        Ingredient ing1 = getIngredient(ingredient1);
        Ingredient ing2 = getIngredient(ingredient2);

        String outputName = (output instanceof Item item) ? BuiltInRegistries.ITEM.getKey(item).getPath() : (String) output;
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, outputName + "_roost_recipe");

        RoostRecipeBuilder.roost(RecipeCategory.MISC, outputStack, ing1, ing2)
                .unlockedBy("has_" + outputName, inventoryTrigger(
                        ItemPredicate.Builder.item().of(outputStack.getItem()).build()
                ))
                .save(pWriter, recipeId);
    }

    private static Ingredient getIngredient(Object ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("FEJL: En af ingredienserne er null!");
        }

        // Hvis ingrediensen er et Item-objekt, lav en Ingredient deraf
        if (ingredient instanceof Item item) {
            return Ingredient.of(item);
        }

        // Hvis ingrediensen er en TagKey<Item>, brug den som tag
        if (ingredient instanceof TagKey<?> tagKey && tagKey.isFor(Registries.ITEM)) {
            return Ingredient.of((TagKey<Item>) tagKey);
        }

        // Hvis ingrediensen er en String
        if (ingredient instanceof String itemName) {
            // Hvis den starter med "#", fortolkes den som et tag
            if (itemName.startsWith("#")) {
                ResourceLocation tagLocation = ResourceLocation.parse(itemName.substring(1)); // Fjern '#'
                TagKey<Item> itemTag = TagKey.create(Registries.ITEM, tagLocation);
                return Ingredient.of(itemTag);
            }

            // Hvis det er et almindeligt item-navn
            ResourceLocation itemLocation;

            if (itemName.contains(":")) {
                itemLocation = ResourceLocation.parse(itemName);
            } else {
                itemLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, itemName);
            }

            Item item = BuiltInRegistries.ITEM.get(itemLocation);
            if (item == Items.AIR) {
                throw new IllegalArgumentException("FEJL: Ingredient '" + itemName + "' findes ikke i registrene!");
            }
            return Ingredient.of(item);
        }

        throw new IllegalArgumentException("Ingredient must be an Item, a String, or a TagKey<Item>. Modtog: " + ingredient.getClass().getName());
    }

}