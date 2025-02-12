package net.zuperz.resource_armadillo.datagen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.datagen.custom.BreedingRecipeBuilder;
import net.zuperz.resource_armadillo.datagen.custom.CentrifugeRecipeBuilder;
import net.zuperz.resource_armadillo.datagen.custom.RoostRecipeBuilder;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.RoostRecipe;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;

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
                Items.DIAMOND, 5,
                new String[]{
                        "AAA",
                        "A A",
                        "AAA"
                },
                Map.of(
                        'A', "diamond_essence"
                ),
                pWriter
        );

        RoostRecipe("dirt_scute", Blocks.DIRT.asItem(), Items.WHEAT_SEEDS, pWriter);

        RoostRecipe("cobblestone_scute", Blocks.COBBLESTONE.asItem(), Blocks.ANDESITE.asItem(), pWriter);

        RoostRecipe("stone_scute", Blocks.STONE.asItem(), "cobblestone_scute", pWriter);

        RoostRecipe("sand_scute", Blocks.SAND.asItem(), Blocks.GRAVEL.asItem(), pWriter);

        RoostRecipe("clay_scute", Blocks.CLAY.asItem(), Items.BRICK, pWriter);

        RoostRecipe("netherrack_scute", Blocks.NETHERRACK.asItem(), Items.NETHER_WART, pWriter);

        /* Metals */

        RoostRecipe("coal_scute", Blocks.COAL_ORE.asItem(), "cobblestone_scute", pWriter);

        RoostRecipe("iron_scute", Blocks.IRON_ORE.asItem(), "cobblestone_scute", pWriter);



        hiveBreeding("diamond_scute", "iron_scute", "coal_scute", pWriter);

        hiveBreeding("quartz_scute", "iron_scute", "diamond_scute", pWriter);

        hiveBreeding("chromium_scute", "quartz_scute", "iron_scute", pWriter);

        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();
        for (var scute : registry.getArmadilloScuteTypes()) {
            if (scute.isEnabled() && !scute.getName().equals("none")) {
                String scuteName = scute.getName() + "_scute";
                ResourceLocation scuteLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, scuteName);
                Item scuteItem = BuiltInRegistries.ITEM.get(scuteLocation);

                String essenceName = scute.getName() + "_essence";
                ResourceLocation essenceLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, essenceName);
                Item essenceItem = BuiltInRegistries.ITEM.get(essenceLocation);

                String armorName = scute.getName() + "_armor";
                ResourceLocation armorLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, armorName);
                Item armorItem = BuiltInRegistries.ITEM.get(armorLocation);

                if (scuteItem != null && essenceItem != null) {
                    CentrifugeRecipeBuilder.centrifuge(Ingredient.of(scuteItem), RecipeCategory.MISC, new ItemStack(essenceItem))
                            .group("centrifuge_" + scute.getName())
                            .unlockedBy("has_" + scuteName, has(scuteItem))
                            .save(pWriter, ResourceLocation.fromNamespaceAndPath("resource_armadillo", scute.getName() + "_crafted_from_centrifuge"));
                }

                if (scuteItem != null && armorItem != null) {
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, armorItem)
                            .pattern("A  ")
                            .pattern("AAA")
                            .pattern("A A")
                            .define('A', scuteItem)
                            .unlockedBy("has_" + armorName, inventoryTrigger(ItemPredicate.Builder.item().
                                    of(scuteItem).build()))
                            .save(pWriter, ResourceLocation.fromNamespaceAndPath("resource_armadillo", armorName + "_crafted_from_" + scuteName));
                }

                if (scuteItem != null) {
                    //hiveScuteBreeding( String.valueOf(scuteItem), String.valueOf(scuteItem), String.valueOf(scuteItem), pWriter);
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
                .save(pWriter, ResourceLocation.fromNamespaceAndPath("resource_armadillo", output + "from_hive_breeding"));
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


    private static void essenceShapedRecipe(Object output, int count, String[] pattern, Map<Character, String> ingredients, RecipeOutput pWriter) {
        ItemStack outputStack;
        if (output instanceof Item item) {
            outputStack = new ItemStack(item, count);
        } else if (output instanceof String itemName) {
            outputStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, itemName)), count);
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
        String outputName = (output instanceof Item item) ? BuiltInRegistries.ITEM.getKey((Item) output).getPath() : (String) output;
        builder.save(pWriter, ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, outputName + "_essence_recipe"));
    }

    private static void RoostRecipe(Object output, Object ingredient1, Object ingredient2, RecipeOutput pWriter) {
        // Konverter output til ItemStack
        ItemStack outputStack;
        if (output instanceof Item item) {
            outputStack = new ItemStack(item, 1);
        } else if (output instanceof String itemName) {
            Item outputItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, itemName));
            if (outputItem == Items.AIR) {
                throw new IllegalArgumentException("FEJL: Output-item '" + itemName + "' findes ikke i registrene!");
            }
            outputStack = new ItemStack(outputItem, 1);
        } else {
            throw new IllegalArgumentException("Output must be an Item or a String representing an item name.");
        }

        // Konverter ingredienser til Ingredient
        Ingredient ing1 = getIngredient(ingredient1);
        Ingredient ing2 = getIngredient(ingredient2);

        // Opret opskriftens ResourceLocation
        String outputName = (output instanceof Item item) ? BuiltInRegistries.ITEM.getKey(item).getPath() : (String) output;
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, outputName + "_roost_recipe");

        // Brug RoostRecipeBuilder
        RoostRecipeBuilder.roost(RecipeCategory.MISC, outputStack, ing1, ing2)
                .unlockedBy("has_" + outputName, inventoryTrigger(
                        ItemPredicate.Builder.item().of(outputStack.getItem()).build()
                ))
                .save(pWriter, recipeId);
    }

    private static Ingredient getIngredient(Object ingredient) {
        if (ingredient instanceof Item item) {
            return Ingredient.of(item);
        } else if (ingredient instanceof String itemName) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, itemName));
            if (item == Items.AIR) {
                throw new IllegalArgumentException("FEJL: Ingredient '" + itemName + "' findes ikke i registrene!");
            }
            return Ingredient.of(item);
        } else {
            throw new IllegalArgumentException("Ingredient must be an Item or a String.");
        }
    }
}