package net.zuperz.resource_armadillo.entity.custom.armadillo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;

@OnlyIn(Dist.CLIENT)
public class ResourceArmadilloRenderer extends MobRenderer<ResourceArmadilloEntity, ResourceArmadilloModel> {
    private static final ResourceLocation ARMADILLO_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armadillo.png");

    public ResourceArmadilloRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceArmadilloModel(context.bakeLayer(ModModelLayers.RESOURCE_ARMADILLO)), 0.4F);
        this.addLayer(new ArmadilloOverlayLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ResourceArmadilloEntity entity) {
        return ARMADILLO_LOCATION;
    }

    public class ArmadilloOverlayLayer extends RenderLayer<ResourceArmadilloEntity, ResourceArmadilloModel> {
        private static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/armadillo_part_1.png");

        public ArmadilloOverlayLayer(RenderLayerParent<ResourceArmadilloEntity, ResourceArmadilloModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ResourceArmadilloEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float scale = entity.getAgeScale();
            poseStack.pushPose();
            if (entity.isBaby()) {
                poseStack.translate(0.0F, scale, 0.0F);
                poseStack.scale(scale + 0.0001f, scale, scale + 0.0001f);
            } else {
                poseStack.scale(scale, scale, scale);
            }


            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(OVERLAY_TEXTURE));
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.pack(15, 4), 30.6F, 40.0F, 18.0F, 1.0F);


            poseStack.popPose();
        }
    }
}