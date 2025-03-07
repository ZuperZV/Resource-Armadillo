package net.zuperz.resource_armadillo.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.zuperz.resource_armadillo.block.custom.RoostBlock;
import net.zuperz.resource_armadillo.block.entity.custom.NestBlockEntity;
import net.zuperz.resource_armadillo.block.entity.custom.RoostBlockEntity;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;

/*
 *  MIT License
 *  Copyright (c) 2022 Kaupenjoe
 *
 *  This code is licensed under the "MIT License"
 *  https://github.com/Kaupenjoe/Resource-Slimes/blob/master/LICENSE
 *
 *  And this is the code Github
 *  https://github.com/Kaupenjoe/Resource-Slimes/blob/master/src/main/java/net/kaupenjoe/resourceslimes/block/entity/client/SlimeIncubationStationBlockEntityRenderer.java
 *
 *  Modified Version by: ZuperZ
 */

public class NestBlockEntityRenderer implements BlockEntityRenderer<NestBlockEntity> {

    private final EntityRenderDispatcher entityRenderer;

    public NestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public void render(NestBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        if (level == null || !level.isClientSide()) {
            return;
        }

        if (blockEntity.isArmadilloDataEmpty()) {
            return;
        }

        String armadillo1Item = blockEntity.getStoredArmadilloDataValue("resource_quality");
        ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));
        float rot = blockEntity.getBlockState().getValue(RoostBlock.FACING).toYRot();

        Vec3 position = new Vec3(0.0d, 0.425d, 0.1875d)
                .yRot(-Mth.DEG_TO_RAD * rot)
                .add(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        poseStack.pushPose();

        poseStack.translate(position.x - pos.getX(), position.y - pos.getY(), position.z - pos.getZ());

        poseStack.mulPose(Axis.YN.rotationDegrees(rot));

        if (!blockEntity.isArmadilloBaby()) {
            poseStack.scale(1.0f, 1.0f, 1.0f);
        } else {
            poseStack.scale(0.6F, 0.6F, 0.6F);
        }

        ResourceArmadilloEntity renderEntity = ModEntities.RESOURCE_ARMADILLO.get().create(level);


        if (renderEntity != null) {
            renderEntity.setPos(position.x, position.y, position.z);
            renderEntity.setNoGravity(true);
            renderEntity.setResource(armadillo1Stack);
            renderEntity.updateVariantFromResource();
            renderEntity.setYHeadRot(0);

            entityRenderer.render(renderEntity, 0.0d, 0.0d, 0.0d, 0.0f, partialTick, poseStack, bufferSource, getLightLevel(level, pos));
            entityRenderer.setRenderShadow(false);
        }

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
