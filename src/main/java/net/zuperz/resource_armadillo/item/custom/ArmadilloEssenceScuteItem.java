package net.zuperz.resource_armadillo.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

public class ArmadilloEssenceScuteItem extends Item {
    private final ArmadilloScuteType armadilloScuteType;

    public ArmadilloEssenceScuteItem(ArmadilloScuteType armadilloScuteType) {
        super(new Properties());
        this.armadilloScuteType = armadilloScuteType;
    }

    public ArmadilloScuteType getScuteType() {
        return this.armadilloScuteType;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.armadilloScuteType.getDisplayName().getString())
                .append(Component.literal(" "))
                .append(Component.translatable("item.resource_armadillo.resource_armadillo_essence_scute"));
    }


    @Override
    public Component getDescription() {
        return this.getName(ItemStack.EMPTY);
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        return "item.resource_armadillo.resource_armadillo_essence_scute";
    }

    public ResourceLocation getTexture() {
        return this.armadilloScuteType.getTexture();
    }
}
