package net.zuperz.resource_armadillo.api.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zuperz.resource_armadillo.api.jei.EntityDrawable;
import net.zuperz.resource_armadillo.recipes.RoostRecipe;

public class EmiRoostRecipe extends BasicEmiRecipe {

    public EmiRoostRecipe(RoostRecipe recipe) {
        super(ResourceArmadilloEmiPlugin.ROOST_CATEGORY, recipe.getId(), 115, 61);

        this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));
        if (recipe.getIngredients().size() > 1) {
            this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(1)));
        }

        this.outputs.add(EmiStack.of(recipe.output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inputs.get(0), 3, 7).drawBack(true);

        if (inputs.size() > 1) {
            widgets.addSlot(inputs.get(1), 41, 7).drawBack(true);
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 20);
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 62, 20, 2000, true, false, false);

        widgets.addTexture(EmiTexture.EMPTY_FLAME, 23, 26);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 23, 26, 2000, false, true, true);

        widgets.addTexture(EmiTexture.SLOT, 21, 41);
        widgets.addTexture(EmiTexture.SLOT, 21, 2);

        EmiIngredient outputIngredient = outputs.get(0);
        ItemStack outputStack = outputIngredient.getEmiStacks().get(0).getItemStack();
        Item outputItem = outputStack.getItem();

        widgets.addDrawable(92, 19, 32, 32, (draw, x, y, delta) ->
                new EmiEntityDrawable(32, 32, outputItem, 21, true)
                        .render(draw, 6, 16, delta));

        widgets.addDrawable(22, 3, 32, 32, (draw, x, y, delta) ->
                new EmiEntityDrawable(32, 32, outputItem, 14, false)
                        .render(draw, 8, 13, delta));

        widgets.addTexture(EmiTexture.LARGE_SLOT, 86, 16);
        widgets.addSlot(outputs.get(0), 90, 20).recipeContext(this).drawBack(false);
    }
}
