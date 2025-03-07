package net.zuperz.resource_armadillo.entity.custom.armadillo.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

public class ResourceArmadilloRenderer extends MobRenderer<ResourceArmadilloEntity, ResourceArmadilloModel> {

    public ResourceArmadilloRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceArmadilloModel(context.bakeLayer(ModModelLayers.RESOURCE_ARMADILLO)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(ResourceArmadilloEntity entity) {
        ResourceLocation overlayTexture = getOverlayTexture(entity);
        return overlayTexture;
    }

    private ResourceLocation getOverlayTexture(ResourceArmadilloEntity entity) {
        ArmadilloScuteType resource = entity.getVariant();
        ResourceLocation scuteTexture = resource.getTexture();
        return scuteTexture;
    }
}