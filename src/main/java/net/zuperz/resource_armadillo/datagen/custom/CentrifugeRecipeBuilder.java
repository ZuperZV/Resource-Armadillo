package net.zuperz.resource_armadillo.datagen.custom;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CentrifugeRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final ItemStack output;
    private final Ingredient ingredient;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private CentrifugeRecipeBuilder(RecipeCategory pCategory, ItemStack pOutput, Ingredient pIngredient) {
        this.category = pCategory;
        this.output = pOutput;
        this.ingredient = pIngredient;
    }

    public static CentrifugeRecipeBuilder centrifuge(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pOutput) {
        return new CentrifugeRecipeBuilder(pCategory, pOutput, pIngredient);
    }

    public CentrifugeRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterion) {
        this.criteria.put(pCriterionName, pCriterion);
        return this;
    }

    public CentrifugeRecipeBuilder group(@Nullable String pGroup) {
        this.group = pGroup;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        Advancement.Builder advancementBuilder = pRecipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        CentrifugeRecipe recipe = new CentrifugeRecipe(this.output, this.ingredient);
        pRecipeOutput.accept(pRecipeId, recipe, advancementBuilder.build(pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation pRecipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pRecipeId);
        }
    }
}