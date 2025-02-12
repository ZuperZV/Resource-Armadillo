package net.zuperz.resource_armadillo.api.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.zuperz.resource_armadillo.recipes.BreedingRecipe;

public class EmiHiveRecipe extends BasicEmiRecipe {

    public EmiHiveRecipe(BreedingRecipe recipe) {
        super(ResourceArmadilloEmiPlugin.HIVE_CATEGORY, recipe.getId(), 115, 55);

        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));
        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(1)));
        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(2)));
        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(3)));

        this.outputs.add(EmiStack.of(recipe.output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 63, 20);
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 63, 20, 2000, true, false, false);

        widgets.addSlot(inputs.get(0), 7, 36).drawBack(true);
        widgets.addSlot(inputs.get(1), 39, 36).drawBack(true);

        widgets.addDrawable(93, 21, 32, 32, (draw, x, y, delta) ->
                new EmiEntityDrawable(32, 32, outputs.get(0).getEmiStacks().get(0).getItemStack().getItem(), 21, true)
                        .render(draw, 6, 16, delta));

        widgets.addTexture(EmiTexture.LARGE_SLOT, 87, 16);
        widgets.addSlot(outputs.get(0), 92, 21).recipeContext(this).drawBack(false);



        widgets.addDrawable(8, 7, 32, 32, (draw, x, y, delta) ->
                new EmiEntityDrawable(32, 32, inputs.get(2).getEmiStacks().get(0).getItemStack().getItem(), 21, true)
                        .render(draw, 6, 16, delta));

        widgets.addTexture(EmiTexture.LARGE_SLOT, 2, 2);
        widgets.addSlot(inputs.get(2), 7, 7).recipeContext(this).drawBack(false);



        widgets.addDrawable(40, 7, 32, 32, (draw, x, y, delta) ->
                new EmiEntityDrawable(32, 32, inputs.get(3).getEmiStacks().get(0).getItemStack().getItem(), 21, true)
                        .render(draw, 6, 16, delta));

        widgets.addTexture(EmiTexture.LARGE_SLOT, 34, 2);
        widgets.addSlot(inputs.get(3), 39, 7).recipeContext(this).drawBack(false);
    }
}
