package net.zuperz.resource_armadillo.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.zuperz.resource_armadillo.block.custom.RoostBlock;
import net.zuperz.resource_armadillo.block.entity.custom.ArmadilloHiveBlockEntity;
import net.zuperz.resource_armadillo.block.entity.custom.ArmadilloHiveBlockEntity;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.util.ModTags;

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

public class HiveBlockEntityRenderer implements BlockEntityRenderer<ArmadilloHiveBlockEntity> {

    private final EntityRenderDispatcher entityRenderer;

    public HiveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public void render(ArmadilloHiveBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        if (level == null || !level.isClientSide()) {
            return;
        }

        float rot = blockEntity.getBlockState().getValue(RoostBlock.FACING).toYRot();

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(rot));

        String armadillo1Item = blockEntity.getStoredArmadilloDataValue("resource_quality");
        String armadillo2Item = blockEntity.getStoredArmadilloData2Value("resource_quality");

        ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item)));
        ItemStack armadillo2Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo2Item)));

        if (!blockEntity.getSlotInputItems(4).isEmpty()) {
            float scaledProgress = blockEntity.getBabyScaledProgress();

            poseStack.pushPose();
            poseStack.translate(0, 0.38, 0.0535d);
            poseStack.scale(scaledProgress, scaledProgress, scaledProgress);

            ResourceArmadilloEntity renderEntity = ModEntities.RESOURCE_ARMADILLO.get().create(level);
            if (renderEntity != null) {
                renderEntity.setNoGravity(true);
                renderEntity.setYHeadRot(0);
                renderEntity.setResource(blockEntity.getSlotInputItems(4));
                renderEntity.updateVariantFromResource();

                entityRenderer.render(renderEntity, 0.0d, 0.0d, 0.0d, 0.0f, partialTick, poseStack, bufferSource, getLightLevel(level, pos));
                entityRenderer.setRenderShadow(false);
            }
            poseStack.popPose();
        }

        if (!blockEntity.getStoredArmadilloData().isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(-0.2, 0.38, -0.03);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            ResourceArmadilloEntity renderEntity = ModEntities.RESOURCE_ARMADILLO.get().create(level);
            if (renderEntity != null) {
                renderEntity.setNoGravity(true);
                renderEntity.setYHeadRot(0);
                renderEntity.setResource(armadillo1Stack);
                renderEntity.updateVariantFromResource();

                entityRenderer.render(renderEntity, 0.0d, 0.0d, 0.0d, 0.0f, partialTick, poseStack, bufferSource, getLightLevel(level, pos));
                entityRenderer.setRenderShadow(false);
            }
            poseStack.popPose();
        }

        if (!blockEntity.getStoredArmadilloData2().isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.2, 0.38, -0.03);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            ResourceArmadilloEntity renderEntity = ModEntities.RESOURCE_ARMADILLO.get().create(level);
            if (renderEntity != null) {
                renderEntity.setNoGravity(true);
                renderEntity.setYHeadRot(0);
                renderEntity.setResource(armadillo2Stack);
                renderEntity.updateVariantFromResource();

                entityRenderer.render(renderEntity, 0.0d, 0.0d, 0.0d, 0.0f, partialTick, poseStack, bufferSource, getLightLevel(level, pos));
                entityRenderer.setRenderShadow(false);
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
