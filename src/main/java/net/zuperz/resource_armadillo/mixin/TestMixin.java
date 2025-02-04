package net.zuperz.resource_armadillo.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class TestMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void useTest(Level p_41432_, Player p_41433_, InteractionHand p_41434_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        //p_41433_.kill();
    }
}
