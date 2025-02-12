package net.zuperz.resource_armadillo.api.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.recipes.ModRecipes;

@EmiEntrypoint
public class ResourceArmadilloEmiPlugin implements EmiPlugin {

    public static final EmiStack CENTRIFUGE_WORKSTATION = EmiStack.of(ModBlocks.CENTRIFUGE.get());
    public static final EmiStack ROOST_WORKSTATION = EmiStack.of(ModBlocks.ROOST.get());
    public static final EmiStack HIVE_WORKSTATION = EmiStack.of(ModBlocks.ARMADILLO_HIVE.get());

    public static final EmiRecipeCategory CENTRIFUGE_CATEGORY = new EmiRecipeCategory(CENTRIFUGE_WORKSTATION.getId(), CENTRIFUGE_WORKSTATION);
    public static final EmiRecipeCategory ROOST_CATEGORY = new EmiRecipeCategory(ROOST_WORKSTATION.getId(), ROOST_WORKSTATION);
    public static final EmiRecipeCategory HIVE_CATEGORY = new EmiRecipeCategory(HIVE_WORKSTATION.getId(), HIVE_WORKSTATION);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CENTRIFUGE_CATEGORY);
        registry.addCategory(ROOST_CATEGORY);
        registry.addCategory(HIVE_CATEGORY);

        registry.addWorkstation(CENTRIFUGE_CATEGORY, CENTRIFUGE_WORKSTATION);
        registry.addWorkstation(ROOST_CATEGORY, ROOST_WORKSTATION);
        registry.addWorkstation(HIVE_CATEGORY, HIVE_WORKSTATION);

        RecipeManager manager = registry.getRecipeManager();

        for (var recipe : manager.getAllRecipesFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get())) {
            registry.addRecipe(new EmiCentrifugeRecipe(recipe.value()));
        }

        for (var recipe : manager.getAllRecipesFor(ModRecipes.ROOST_RECIPE_TYPE.get())) {
            registry.addRecipe(new EmiRoostRecipe(recipe.value()));
        }

        for (var recipe : manager.getAllRecipesFor(ModRecipes.BREEDING_ARMADILLO_RECIPE_TYPE.get())) {
            registry.addRecipe(new EmiHiveRecipe(recipe.value()));
        }
    }
}