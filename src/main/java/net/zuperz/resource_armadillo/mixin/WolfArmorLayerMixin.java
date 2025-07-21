package net.zuperz.resource_armadillo.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
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

import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mixin(WolfArmorLayer.class)
public class WolfArmorLayerMixin {
    private final WolfModel<Wolf> model;

    private static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS = Map.of(
            Crackiness.Level.LOW,
            ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"),
            Crackiness.Level.MEDIUM,
            ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"),
            Crackiness.Level.HIGH,
            ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png")
    );

    public WolfArmorLayerMixin(WolfModel<Wolf> model, EntityModelSet entityModelSet) {
        this.model = new WolfModel<>(entityModelSet.bakeLayer(ModelLayers.WOLF_ARMOR));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void injectRender(
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

            this.model.prepareMobModel(wolf, limbSwing, limbSwingAmount, ageInTicks);
            this.model.setupAnim(wolf, limbSwing, limbSwingAmount, netHeadYaw, headPitch, scale);
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(armorTexture));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            this.maybeRenderColoredLayer(poseStack, bufferSource, packedLight, itemStack);
            this.maybeRenderCracks(poseStack, bufferSource, packedLight, itemStack);
            ci.cancel();
        }
    }

    //new WolfModel<>(p_316756_.bakeLayer(ModelLayers.WOLF_ARMOR)

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

    private void maybeRenderCracks(PoseStack p_331222_, MultiBufferSource p_331637_, int p_330931_, ItemStack p_331187_) {
        Crackiness.Level crackiness$level = Crackiness.WOLF_ARMOR.byDamage(p_331187_);
        if (crackiness$level != Crackiness.Level.NONE) {
            ResourceLocation resourcelocation = ARMOR_CRACK_LOCATIONS.get(crackiness$level);
            VertexConsumer vertexconsumer = p_331637_.getBuffer(RenderType.entityTranslucent(resourcelocation));
            this.model.renderToBuffer(p_331222_, vertexconsumer, p_330931_, OverlayTexture.NO_OVERLAY);
        }
    }
}
