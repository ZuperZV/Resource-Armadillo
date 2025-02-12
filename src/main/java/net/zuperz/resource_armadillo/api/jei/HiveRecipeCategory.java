package net.zuperz.resource_armadillo.api.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.recipes.BreedingRecipe;
import org.jetbrains.annotations.NotNull;

public class HiveRecipeCategory implements IRecipeCategory<BreedingRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "breeding");
    public static final RecipeType<BreedingRecipe> RECIPE_TYPE = RecipeType.create(ResourceArmadillo.MOD_ID, "breeding", BreedingRecipe.class);
    public final static ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/slot.png");
    public final static ResourceLocation BIG_SLOT = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/big_slot.png");
    public final static ResourceLocation ARROWBACK = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/null_arrow.png");
    public final static ResourceLocation LIT = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/null_flame.png");
    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawableStatic slot_1;
    private final IDrawableStatic slot_2;
    private final IDrawableStatic slot_3;
    private final IDrawableStatic slot_4;
    private final IDrawableStatic slot_5;

    private final IDrawableStatic arrowbacki;
    private final IDrawableAnimated progress;

    public HiveRecipeCategory(IGuiHelper helper) {
        ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/arrow.png");

        this.background = helper.createBlankDrawable(115, 55);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ARMADILLO_HIVE.get()));

        IDrawableStatic progressDrawable = helper.drawableBuilder(ARROW, 0, 0, 23, 15).setTextureSize(23, 15).addPadding(20, 0, 63, 0).build();
        this.arrowbacki = helper.drawableBuilder(ARROWBACK, 0, 0, 23, 15).setTextureSize(23, 15).addPadding(20, 0, 62, 0).build();

        this.slot_1 = helper.drawableBuilder(BIG_SLOT, 0, 26, 26, 26).setTextureSize(26, 26).addPadding(2,0,2,0).build();
        this.slot_2 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(35,0,6,0).build();
        this.slot_3 = helper.drawableBuilder(BIG_SLOT, 0, 26, 26, 26).setTextureSize(26, 26).addPadding(2,0,34,0).build();
        this.slot_4 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(35,0,38,0).build();

        this.slot_5 = helper.drawableBuilder(BIG_SLOT, 0, 26, 26, 26).setTextureSize(26, 26).addPadding(16,0,87,0).build();

        this.progress = helper.createAnimatedDrawable(progressDrawable, 200, IDrawableAnimated.StartDirection.LEFT,
                false);
    }

    @Override
    public RecipeType<BreedingRecipe> getRecipeType() {
        return JEIPlugin.HIVE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.resource_armadillo.armadillo_hive");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void draw(BreedingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
                     double mouseY) {

        this.slot_1.draw(guiGraphics);
        this.slot_2.draw(guiGraphics);
        this.slot_3.draw(guiGraphics);
        this.slot_4.draw(guiGraphics);
        this.slot_5.draw(guiGraphics);

        this.arrowbacki.draw(guiGraphics);
        this.progress.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, BreedingRecipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 7, 36)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.INPUT, 39, 36)
                .addIngredients(recipe.getIngredients().get(1));

        builder.addSlot(RecipeIngredientRole.INPUT, 7, 7)
                .addIngredients(recipe.getIngredients().get(2));

        builder.addSlot(RecipeIngredientRole.INPUT, 39, 7)
                .addIngredients(recipe.getIngredients().get(3));


        builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 21)
                .addItemStack(recipe.output);


        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 7, 7)
                .setOverlay(new EntityDrawable(32, 32,
                        recipe.getIngredients().get(2).getItems()[0].getItem(), 22, true), 7, 15);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 39, 7)
                .setOverlay(new EntityDrawable(32, 32,
                        recipe.getIngredients().get(3).getItems()[0].getItem(), 22, true), 7, 15);


        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 92, 21)
                .setOverlay(new EntityDrawable(32, 32,
                        recipe.output.getItem(), 22, true), 7, 15);

    }
}