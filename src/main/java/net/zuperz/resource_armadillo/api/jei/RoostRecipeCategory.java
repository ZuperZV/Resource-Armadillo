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
import net.zuperz.resource_armadillo.recipes.RoostRecipe;
import org.jetbrains.annotations.NotNull;

public class RoostRecipeCategory implements IRecipeCategory<RoostRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "roost");
    public static final RecipeType<RoostRecipe> RECIPE_TYPE = RecipeType.create(ResourceArmadillo.MOD_ID, "roost", RoostRecipe.class);
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

    private final IDrawableStatic lit;
    private final IDrawableAnimated lit_progress;

    public RoostRecipeCategory(IGuiHelper helper) {
        ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/arrow.png");
        ResourceLocation LIT_PROGRESS = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/lit_progress.png");

        this.background = helper.createBlankDrawable(115, 61);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ROOST.get()));

        IDrawableStatic progressDrawable = helper.drawableBuilder(ARROW, 0, 0, 23, 15).setTextureSize(23, 15).addPadding(20, 0, 61, 0).build();
        this.arrowbacki = helper.drawableBuilder(ARROWBACK, 0, 0, 23, 15).setTextureSize(23, 15).addPadding(20, 0, 60, 0).build();

        IDrawableStatic litProgressDrawable = helper.drawableBuilder(LIT_PROGRESS, 0, 0, 14, 14).setTextureSize(14, 14).addPadding(25, 0, 22, 0).build();
        this.lit = helper.drawableBuilder(LIT, 0, 0, 13, 13).setTextureSize(13, 13).addPadding(26, 0, 23, 0).build();

        this.slot_1 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(6,0,2,0).build();
        this.slot_2 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(3,0,21,0).build();
        this.slot_3 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(6,0,40,0).build();
        this.slot_4 = helper.drawableBuilder(SLOT, 0, 18, 18, 18).setTextureSize(18, 18).addPadding(41,0,21,0).build();

        this.slot_5 = helper.drawableBuilder(BIG_SLOT, 0, 26, 26, 26).setTextureSize(26, 26).addPadding(14,0,87,0).build();

        this.progress = helper.createAnimatedDrawable(progressDrawable, 200, IDrawableAnimated.StartDirection.LEFT,
                false);

        this.lit_progress = helper.createAnimatedDrawable(litProgressDrawable, 150, IDrawableAnimated.StartDirection.TOP,
                true);
    }

    @Override
    public RecipeType<RoostRecipe> getRecipeType() {
        return JEIPlugin.ROOST_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.resource_armadillo.roost");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void draw(RoostRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
                     double mouseY) {

        this.slot_1.draw(guiGraphics);
        this.slot_2.draw(guiGraphics);
        this.slot_3.draw(guiGraphics);
        this.slot_4.draw(guiGraphics);
        this.slot_5.draw(guiGraphics);

        this.arrowbacki.draw(guiGraphics);
        this.progress.draw(guiGraphics);

        this.lit.draw(guiGraphics);
        this.lit_progress.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, RoostRecipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 3, 7)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.INPUT, 41, 7)
                .addIngredients(recipe.getIngredients().get(1));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 19)
                .addItemStack(recipe.output);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 92, 19)
                .setOverlay(new EntityDrawable(32, 32,
                        recipe.output.getItem(), 22, true), 7, 15);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 22, 4)
                .setOverlay(new EntityDrawable(32, 32,
                        recipe.output.getItem(), 14, false), 8, 14);
    }
}