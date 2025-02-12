package net.zuperz.resource_armadillo.api.emi;

import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
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
 *  Modified to Emi by: ZuperZ
 *
 *
 *  Kaupenjoe Has it from
 *
 *  EntityWidget Class by DaRealTurtyWurty:
 *  https://github.com/DaRealTurtyWurty/TurtyLib/blob/main/src/main/java/io/github/darealturtywurty/turtylib/client/ui/components/EntityWidget.java
 *
 */

public class EmiEntityDrawable implements EmiRenderable {
    private final LivingEntity livingEntity;
    private final int size;
    private final int width;
    private final int height;

    public EmiEntityDrawable(int width, int height, Item resourceItem, int size, boolean isResourceArmadillo) {
        Minecraft mc = Minecraft.getInstance();
        if (isResourceArmadillo) {
            this.livingEntity = ModEntities.RESOURCE_ARMADILLO.get().create(mc.level);
            if (this.livingEntity instanceof ResourceArmadilloEntity resourceArmadillo) {
                resourceArmadillo.setResource(resourceItem.getDefaultInstance());
                resourceArmadillo.updateVariantFromResource();
            }
        } else {
            this.livingEntity = EntityType.ARMADILLO.create(mc.level);
        }

        this.size = size;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float delta) {
        if (livingEntity != null) {
            livingEntity.setYHeadRot(0);
            livingEntity.setPos(0, 0, 0);

            InventoryScreen.renderEntityInInventory(
                    guiGraphics,
                    x, y, size,
                    new Vector3f(0, 0, 0),
                    new Quaternionf().rotationYXZ((float) Math.PI / 1.5f, 0, (float) Math.PI),
                    null,
                    livingEntity
            );
        }
    }
}