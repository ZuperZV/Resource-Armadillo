package net.zuperz.resource_armadillo.api.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;
import net.zuperz.resource_armadillo.util.ModTags;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/*
 *  MIT License
 *  Copyright (c) 2022 Kaupenjoe
 *
 *  This code is licensed under the "MIT License"
 *  https://github.com/Kaupenjoe/Resource-Slimes/blob/master/LICENSE
 *
 *  EntityDrawable Class by Kaupenjoe:
 *  https://github.com/Kaupenjoe/Resource-Slimes/blob/master/src/main/java/net/kaupenjoe/resourceslimes/integration/EntityDrawable.java
 *
 *  Modified by: ZuperZ
 *
 *
 *  Kaupenjoe Has it from
 *
 *  EntityWidget Class by DaRealTurtyWurty:
 *  https://github.com/DaRealTurtyWurty/TurtyLib/blob/main/src/main/java/io/github/darealturtywurty/turtylib/client/ui/components/EntityWidget.java
 *
 */

public class EntityDrawable implements IDrawableAnimated {
    private LivingEntity livingEntity;
    private ResourceArmadilloEntity resourceArmadillo;
    private Armadillo armadillo;
    private int size;
    private int width;
    private int height;
    private boolean isResourceArmadillo;

    public EntityDrawable(int height, int width, Item resourceItem, int size, boolean isResourceArmadillo) {
        this.isResourceArmadillo = isResourceArmadillo;
        if (isResourceArmadillo) {
            this.livingEntity = ModEntities.RESOURCE_ARMADILLO.get().create(Minecraft.getInstance().level);
            resourceArmadillo = (ResourceArmadilloEntity) livingEntity;
            resourceArmadillo.setResource(resourceItem.getDefaultInstance());
            resourceArmadillo.updateVariantFromResource();
        } else {
            this.livingEntity = EntityType.ARMADILLO.create(Minecraft.getInstance().level);
            armadillo = (Armadillo) livingEntity;
        }

        this.size = size;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        if (livingEntity != null) {
            livingEntity.setYHeadRot(0);
            livingEntity.setPos(0, 0, 0);
            InventoryScreen.renderEntityInInventory(
                    guiGraphics,
                    xOffset, yOffset,
                    size,
                    new Vector3f(0, 0, 0),
                    new Quaternionf().rotationYXZ((float) Math.PI / 1.5f, 0, (float) Math.PI),
                    null,
                    livingEntity
            );
        }
    }
}