package net.zuperz.resource_armadillo.api.emi;


import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;

public class EmiCentrifugeRecipe extends BasicEmiRecipe {

    public EmiCentrifugeRecipe(CentrifugeRecipe recipe) {
        super(ResourceArmadilloEmiPlugin.CENTRIFUGE_CATEGORY, recipe.getId(), 100, 20);

        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));
        if (recipe.getIngredients().size() > 1) {
            this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(1)));
        }if (recipe.getIngredients().size() > 2) {
            this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(2)));
        }
        this.outputs.add(EmiStack.of(recipe.output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 8, 1).drawBack(true);

        widgets.addSlot(outputs.get(0), 76, 1).recipeContext(this).drawBack(true);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 39, 2);
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 39, 2, 20000, true, false, false);
    }
}