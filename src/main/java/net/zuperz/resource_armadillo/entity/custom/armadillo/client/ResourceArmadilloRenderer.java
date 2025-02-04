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
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.ResourceArmadilloEntityRecipe;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ResourceArmadilloRenderer extends MobRenderer<ResourceArmadilloEntity, ResourceArmadilloModel> {
    public static final ResourceLocation ARMADILLO_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armadillo.png");
    public static final ResourceLocation WHiTE_OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/white_armadillo.png");

    public ResourceArmadilloRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceArmadilloModel(context.bakeLayer(ModModelLayers.RESOURCE_ARMADILLO)), 0.4F);
        this.addLayer(new ArmadilloOverlayLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ResourceArmadilloEntity entity) {
        if(entity.resource == Items.DIAMOND.getDefaultInstance()) {
            System.out.println("Text: " + WHiTE_OVERLAY_TEXTURE);
            return WHiTE_OVERLAY_TEXTURE;
        } else {
            return ARMADILLO_LOCATION;
        }
    }

    public class ArmadilloOverlayLayer extends RenderLayer<ResourceArmadilloEntity, ResourceArmadilloModel> {
        public static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/armadillo_part_1.png");

        public ArmadilloOverlayLayer(RenderLayerParent<ResourceArmadilloEntity, ResourceArmadilloModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ResourceArmadilloEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            poseStack.pushPose();

            float scale = entity.getAgeScale();
            if (entity.isBaby()) {
            } else {
                poseStack.scale(scale, scale, scale);
            }

            this.getParentModel().setColor(-1);

            VertexConsumer vertexConsumer;

            if (!(entity.craftItemAndHasRecipe() == null)) {
                vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(entity.craftItemAndHasRecipe()));
            } else {
                vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(OVERLAY_TEXTURE));
            }

            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 10, 66, 65);

            poseStack.popPose();
        }
    }
}