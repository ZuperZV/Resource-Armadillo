package net.zuperz.resource_armadillo.entity.custom.armadillo.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;

@OnlyIn(Dist.CLIENT)
public class ResourceArmadilloRenderer extends MobRenderer<ResourceArmadilloEntity, ResourceArmadilloModel> {
    private static final ResourceLocation ARMADILLO_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armadillo.png");

    public ResourceArmadilloRenderer(EntityRendererProvider.Context p_316729_) {
        super(p_316729_, new ResourceArmadilloModel(p_316729_.bakeLayer(ModelLayers.ARMADILLO)), 0.4F);
    }

    public ResourceLocation getTextureLocation(ResourceArmadilloEntity p_316224_) {
        return ARMADILLO_LOCATION;
    }
}
