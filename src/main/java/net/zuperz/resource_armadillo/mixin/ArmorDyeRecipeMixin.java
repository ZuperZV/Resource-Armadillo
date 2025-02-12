package net.zuperz.resource_armadillo.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.minecraft.world.item.Item;
import com.google.common.collect.Lists;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ArmorDyeRecipe.class)
public class ArmorDyeRecipeMixin {

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void onMatches(CraftingInput p_344736_, Level p_43770_, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemstack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < p_344736_.size(); i++) {
            ItemStack itemstack1 = p_344736_.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ItemTags.DYEABLE) || isArmorItem(itemstack1)) {
                    if (!itemstack.isEmpty()) {
                        cir.setReturnValue(false);
                        return;
                    }
                    itemstack = itemstack1;
                } else if (itemstack1.getItem() instanceof DyeItem) {
                    list.add(itemstack1);
                } else {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }

        if (!itemstack.isEmpty() && !list.isEmpty()) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

    private boolean isArmorItem(ItemStack itemStack) {
        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();
        for (var scute : registry.getArmadilloScuteTypes()) {
            if (scute.isEnabled() && !scute.getName().equals("none")) {
                String armorName = scute.getName() + "_armor";
                ResourceLocation armorLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, armorName);
                Item armorItem = BuiltInRegistries.ITEM.get(armorLocation);
                if (armorItem != null && itemStack.getItem().equals(armorItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "assemble", at = @At("HEAD"), cancellable = true)
    private void onAssemble(CraftingInput p_344909_, HolderLookup.Provider p_335722_, CallbackInfoReturnable<ItemStack> cir) {
        List<DyeItem> list = Lists.newArrayList();
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < p_344909_.size(); i++) {
            ItemStack itemstack1 = p_344909_.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ItemTags.DYEABLE) || isArmorItem(itemstack1)) {
                    if (!itemstack.isEmpty()) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        return;
                    }
                    itemstack = itemstack1.copy();
                } else if (itemstack1.getItem() instanceof DyeItem dyeitem) {
                    list.add(dyeitem);
                } else {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }
        }

        if (!itemstack.isEmpty() && !list.isEmpty()) {
            ItemStack result = DyedItemColor.applyDyes(itemstack, list);
            cir.setReturnValue(result);
        } else {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}