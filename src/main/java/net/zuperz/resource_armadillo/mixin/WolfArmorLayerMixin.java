package net.zuperz.resource_armadillo.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.custom.ArmadilloAnimalArmorItem;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(WolfArmorLayer.class)
public class WolfArmorLayerMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectRender(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Wolf wolf,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            float scale,
            CallbackInfo ci
    ) {
        if (!wolf.hasArmor()) {
            return;
        }

        ItemStack itemStack = wolf.getBodyArmorItem();

        if (itemStack.getItem() instanceof ArmadilloAnimalArmorItem armorItem && armorItem.getBodyType() == ArmadilloAnimalArmorItem.BodyType.CANINE) {
            ResourceLocation armorTexture = armorItem.getTexture();

            WolfModel<Wolf> model = ((WolfArmorLayer) (Object) this).getParentModel();
            model.copyPropertiesTo(model);
            model.prepareMobModel(wolf, limbSwing, limbSwingAmount, ageInTicks);
            model.setupAnim(wolf, limbSwing, limbSwingAmount, netHeadYaw, headPitch, scale);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(armorTexture));
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

            maybeRenderColoredLayer(poseStack, bufferSource, packedLight, itemStack);

            ci.cancel();
        }
    }

    private void maybeRenderColoredLayer(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            ItemStack itemStack
    ) {
        if (itemStack.is(ItemTags.DYEABLE) || isArmorItem(itemStack)) {
            int color = DyedItemColor.getOrDefault(itemStack, 0);
            if (FastColor.ARGB32.alpha(color) == 0) {
                return;
            }

            ResourceLocation overlayTexture = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_overlay.png");
            if (overlayTexture != null) {
                WolfModel<Wolf> model = ((WolfArmorLayer) (Object) this).getParentModel();
                model.renderToBuffer(
                        poseStack,
                        bufferSource.getBuffer(RenderType.entityCutoutNoCull(overlayTexture)),
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        FastColor.ARGB32.opaque(color)
                );
            }
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
