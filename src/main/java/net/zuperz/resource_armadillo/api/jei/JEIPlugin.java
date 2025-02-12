package net.zuperz.resource_armadillo.api.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.datagen.custom.BreedingRecipeBuilder;
import net.zuperz.resource_armadillo.recipes.BreedingRecipe;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.RoostRecipe;
import net.zuperz.resource_armadillo.screen.ArmadilloHiveScreen;
import net.zuperz.resource_armadillo.screen.RoostScreen;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static mezz.jei.api.recipe.RecipeType<CentrifugeRecipe> CENTRIFRUGE_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(CentrifugeRecipeCategory.UID, CentrifugeRecipe.class);

    public static mezz.jei.api.recipe.RecipeType<RoostRecipe> ROOST_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(RoostRecipeCategory.UID, RoostRecipe.class);

    public static mezz.jei.api.recipe.RecipeType<BreedingRecipe> HIVE_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(HiveRecipeCategory.UID, BreedingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var jeiHelpers = registration.getJeiHelpers();

        registration.addRecipeCategories(new CentrifugeRecipeCategory(jeiHelpers.getGuiHelper()));
        registration.addRecipeCategories(new RoostRecipeCategory(jeiHelpers.getGuiHelper()));
        registration.addRecipeCategories(new HiveRecipeCategory(jeiHelpers.getGuiHelper()));
    }


    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        var world = Minecraft.getInstance().level;
        if (world != null) {

            var centrifuge = world.getRecipeManager();
            registration.addRecipes(CentrifugeRecipeCategory.RECIPE_TYPE,
                    getRecipe(centrifuge, ModRecipes.CENTRIFUGE_RECIPE_TYPE.get()));

            var roost = world.getRecipeManager();
            registration.addRecipes(RoostRecipeCategory.RECIPE_TYPE,
                    getRecipe(roost, ModRecipes.ROOST_RECIPE_TYPE.get()));

            var hive = world.getRecipeManager();
            registration.addRecipes(HiveRecipeCategory.RECIPE_TYPE,
                    getRecipe(hive, ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get()));
        }
    }

    public <C extends RecipeInput, T extends Recipe<C>> List<T> getRecipe(RecipeManager manager, RecipeType<T> recipeType){
        List<T> list = new ArrayList<>();
        manager.getAllRecipesFor(recipeType).forEach(tRecipeHolder -> {
            list.add(tRecipeHolder.value());
        });
        return list;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){

        var centrifuge = new ItemStack(ModBlocks.CENTRIFUGE.get());
        registration.addRecipeCatalyst(centrifuge, CentrifugeRecipeCategory.RECIPE_TYPE);

        var roost = new ItemStack(ModBlocks.ROOST.get());
        registration.addRecipeCatalyst(roost, RoostRecipeCategory.RECIPE_TYPE, RecipeTypes.FUELING);

        var hive = new ItemStack(ModBlocks.ARMADILLO_HIVE.get());
        registration.addRecipeCatalyst(hive, HiveRecipeCategory.RECIPE_TYPE);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addRecipeClickArea(RoostScreen.class, 80, 37, 24, 17, JEIPlugin.ROOST_TYPE);
        registration.addRecipeClickArea(RoostScreen.class, 52, 39, 13, 11, RecipeTypes.FUELING);

        registration.addRecipeClickArea(ArmadilloHiveScreen.class, 80, 37, 24, 17, JEIPlugin.ROOST_TYPE);
    }
}