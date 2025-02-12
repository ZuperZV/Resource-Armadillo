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
import net.zuperz.resource_armadillo.recipes.BreedingRecipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class BreedingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final ItemStack output;
    private final Ingredient armadilloIngredient1;
    private final Ingredient armadilloIngredient2;
    private final Ingredient food1;
    private final Ingredient food2;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private BreedingRecipeBuilder(RecipeCategory category, ItemStack output, Ingredient armadilloIngredient1, Ingredient armadilloIngredient2, Ingredient food1, Ingredient food2) {
        this.category = category;
        this.output = output;
        this.armadilloIngredient1 = armadilloIngredient1;
        this.armadilloIngredient2 = armadilloIngredient2;
        this.food1 = food1;
        this.food2 = food2;
    }

    public static BreedingRecipeBuilder breeding(RecipeCategory category, ItemStack output, Ingredient armadilloIngredient1, Ingredient armadilloIngredient2, Ingredient food1, Ingredient food2) {
        return new BreedingRecipeBuilder(category, output, armadilloIngredient1, armadilloIngredient2, food1, food2);
    }

    public BreedingRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        this.criteria.put(criterionName, criterion);
        return this;
    }

    public BreedingRecipeBuilder group(@Nullable String group) {
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

        BreedingRecipe recipe = new BreedingRecipe(this.output, this.armadilloIngredient1, this.armadilloIngredient2, this.food1, this.food2);

        recipeOutput.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}