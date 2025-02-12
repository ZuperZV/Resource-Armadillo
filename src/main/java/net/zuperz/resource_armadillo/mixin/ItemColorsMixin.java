package net.zuperz.resource_armadillo.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.DyedItemColor;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public class ItemColorsMixin {

    @Inject(method = "createDefault", at = @At("RETURN"))
    private static void injectCreateDefault(BlockColors p_92684_, CallbackInfoReturnable<ItemColors> cir) {
        ItemColors itemColors = (ItemColors) (Object) cir.getReturnValue();

        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();
        for (var scute : registry.getArmadilloScuteTypes()) {
            if (scute.isEnabled() && !scute.getName().equals("none")) {
                String armorName = scute.getName() + "_armor";
                ResourceLocation armorLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, armorName);
                Item armorItem = BuiltInRegistries.ITEM.get(armorLocation);

                if (armorItem != null) {
                    itemColors.register(
                            (itemStack, tintIndex) -> tintIndex != 1 ? -1 : DyedItemColor.getOrDefault(itemStack, 0),
                            armorItem
                    );
                }
            }
        }
    }
}
