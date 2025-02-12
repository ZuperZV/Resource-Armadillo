package net.zuperz.resource_armadillo.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfMixin {
    private static final TagKey<Item> WOLF_ARMOR_TAG = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "wolf_armor"));

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void injectedMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (itemstack.is(WOLF_ARMOR_TAG) || isArmadilloArmor(item)) {
            Wolf wolf = (Wolf) (Object) this;

            if (wolf.isOwnedBy(player) && wolf.getBodyArmorItem().isEmpty() && !wolf.isBaby()) {
                wolf.setBodyArmorItem(itemstack.copyWithCount(1));
                itemstack.consume(1, player);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    private boolean isArmadilloArmor(Item item) {
        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();

        for (var scute : registry.getArmadilloScuteTypes()) {
            if (scute.isEnabled() && !scute.getName().equals("none")) {
                String armorName = scute.getName() + "_armor";
                ResourceLocation armorLocation = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, armorName);
                Item armorItem = BuiltInRegistries.ITEM.get(armorLocation);
                if (armorItem == item) {
                    return true;
                }
            }
        }
        return false;
    }
}
