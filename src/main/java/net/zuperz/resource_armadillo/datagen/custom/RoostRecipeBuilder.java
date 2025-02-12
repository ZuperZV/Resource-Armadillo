package net.zuperz.resource_armadillo.datagen.custom;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.zuperz.resource_armadillo.recipes.RoostRecipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoostRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final ItemStack output;
    private final Ingredient ingredient1;
    private final Ingredient ingredient2;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private RoostRecipeBuilder(RecipeCategory category, ItemStack output, Ingredient ingredient1, Ingredient ingredient2) {
        this.category = category;
        this.output = output;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
    }

    public static RoostRecipeBuilder roost(RecipeCategory category, ItemStack output, Ingredient ingredient1, Ingredient ingredient2) {
        return new RoostRecipeBuilder(category, output, ingredient1, ingredient2);
    }

    public RoostRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        this.criteria.put(criterionName, criterion);
        return this;
    }

    public RoostRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
        this.ensureValid(recipeId);

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        RoostRecipe recipe = new RoostRecipe(this.output, this.ingredient1, this.ingredient2);

        recipeOutput.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
