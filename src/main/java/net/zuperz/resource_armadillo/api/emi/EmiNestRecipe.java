package net.zuperz.resource_armadillo.api.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.zuperz.resource_armadillo.recipes.NestRecipe;

public class EmiNestRecipe extends BasicEmiRecipe {
    public EmiNestRecipe(NestRecipe recipe) {
        super(ResourceArmadilloEmiPlugin.NEST_CATEGORY, recipe.getId(), 84, 55);

        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));

        this.outputs.add(EmiStack.of(recipe.output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 30, 20);
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 30, 20, 2000, true, false, false);

        widgets.addTexture(EmiTexture.SLOT, 6, 36);

        widgets.addTexture(EmiTexture.LARGE_SLOT, 2, 2);
        widgets.addDrawable(8, 7, 32, 32, (draw, x, y, delta) ->
                new EmiEntityDrawable(32, 32, outputs.get(0).getEmiStacks().get(0).getItemStack().getItem(), 21, true)
                        .render(draw, 6, 16, delta));
        widgets.addSlot(inputs.get(0), 6, 6).drawBack(false);

        widgets.addTexture(EmiTexture.LARGE_SLOT, 57, 16);
        widgets.addSlot(outputs.get(0), 61, 20).recipeContext(this).drawBack(false);
    }
}
