package net.zuperz.resource_armadillo.block.entity.ItemHandler;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class ContainerRecipeInput implements RecipeInput {
    private final SimpleContainer container;

    public ContainerRecipeInput(SimpleContainer container) {
        this.container = container;
    }

    @Override
    public ItemStack getItem(int index) {
        return container.getItem(index);
    }

    @Override
    public int size() {
        return container.getContainerSize();
    }
}
