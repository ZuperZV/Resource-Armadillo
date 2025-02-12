package net.zuperz.resource_armadillo.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.resources.ResourceLocation;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DyedItemColor.class)
public class DyedItemColorMixin {

    @Inject(method = "applyDyes", at = @At(value = "HEAD"), cancellable = true)
    private static void onApplyDyes(ItemStack p_331581_, List<DyeItem> p_330568_, CallbackInfoReturnable<ItemStack> cir) {
        if (isArmorItem(p_331581_)) {
            ItemStack itemstack = p_331581_.copyWithCount(1);
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int i1 = 0;
            DyedItemColor dyeditemcolor = itemstack.get(DataComponents.DYED_COLOR);
            if (dyeditemcolor != null) {
                int j1 = FastColor.ARGB32.red(dyeditemcolor.rgb());
                int k1 = FastColor.ARGB32.green(dyeditemcolor.rgb());
                int l1 = FastColor.ARGB32.blue(dyeditemcolor.rgb());
                l += Math.max(j1, Math.max(k1, l1));
                i += j1;
                j += k1;
                k += l1;
                i1++;
            }

            for (DyeItem dyeitem : p_330568_) {
                int j3 = dyeitem.getDyeColor().getTextureDiffuseColor();
                int i2 = FastColor.ARGB32.red(j3);
                int j2 = FastColor.ARGB32.green(j3);
                int k2 = FastColor.ARGB32.blue(j3);
                l += Math.max(i2, Math.max(j2, k2));
                i += i2;
                j += j2;
                k += k2;
                i1++;
            }

            int l2 = i / i1;
            int i3 = j / i1;
            int k3 = k / i1;
            float f = (float)l / (float)i1;
            float f1 = (float)Math.max(l2, Math.max(i3, k3));
            l2 = (int)((float)l2 * f / f1);
            i3 = (int)((float)i3 * f / f1);
            k3 = (int)((float)k3 * f / f1);
            int l3 = FastColor.ARGB32.color(0, l2, i3, k3);
            itemstack.set(DataComponents.DYED_COLOR, new DyedItemColor(l3, true));
            cir.setReturnValue(itemstack);
        }
    }

    private static boolean isArmorItem(ItemStack itemStack) {
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
}
