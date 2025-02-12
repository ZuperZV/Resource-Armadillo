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
import net.minecraft.world.item.ItemStack;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.recipes.CentrifugeRecipe;
import org.jetbrains.annotations.NotNull;

public class CentrifugeRecipeCategory implements IRecipeCategory<CentrifugeRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "centrifuge");
    public static final RecipeType<CentrifugeRecipe> RECIPE_TYPE = RecipeType.create(ResourceArmadillo.MOD_ID, "centrifuge", CentrifugeRecipe.class);

    public final static ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/slot.png");
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

    public CentrifugeRecipeCategory(IGuiHelper helper) {
        ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/arrow.png");

        this.background = helper.createBlankDrawable(100, 39);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CENTRIFUGE.get()));

        IDrawableStatic progressDrawable = helper.drawableBuilder(ARROW, 0, 0, 23, 15).setTextureSize(23, 15).addPadding(12, 0, 58, 0).build();
        this.arrowbacki = helper.drawableBuilder(ARROWBACK, 0, 0, 23, 15).setTextureSize(23, 15).addPadding(12, 0, 57, 0).build();

        this.slot_1 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(1,0,20,0).build();
        this.slot_2 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(20,0,20,0).build();
        this.slot_3 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(10,0,1,0).build();
        this.slot_4 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(10,0,39,0).build();

        this.slot_5 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(10,0,81,0).build();

        this.progress = helper.createAnimatedDrawable(progressDrawable, 200, IDrawableAnimated.StartDirection.LEFT,
                false);
    }

    @Override
    public RecipeType<CentrifugeRecipe> getRecipeType() {
        return JEIPlugin.CENTRIFRUGE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.resource_armadillo.centrifuge");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void draw(CentrifugeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 21, 21)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 82, 11)
                .addItemStack(recipe.output);
    }
}