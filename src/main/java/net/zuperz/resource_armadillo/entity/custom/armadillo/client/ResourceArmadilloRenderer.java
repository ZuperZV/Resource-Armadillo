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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.recipes.ResourceArmadilloEntityRecipe;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import java.util.Optional;

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