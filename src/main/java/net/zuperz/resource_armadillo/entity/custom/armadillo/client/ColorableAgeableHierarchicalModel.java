package net.zuperz.resource_armadillo.entity.custom.armadillo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class ColorableAgeableHierarchicalModel<E extends Entity> extends AgeableHierarchicalModel<E> {
    private int color = -1;

    private final float youngScaleFactor;
    private final float bodyYOffset;

    public ColorableAgeableHierarchicalModel(float p_273694_, float p_273578_) {
        this(p_273694_, p_273578_, RenderType::entityCutoutNoCull);
    }

    public ColorableAgeableHierarchicalModel(float p_273130_, float p_273302_, Function<ResourceLocation, RenderType> p_273636_) {
        super(p_273130_, p_273302_, p_273636_);
        this.youngScaleFactor = p_273130_;
        this.bodyYOffset = p_273302_;
    }

    public void setColor(int p_351056_) {
        this.color = p_351056_;
    }

    public float getYoungScaleFactor() {
        return this.youngScaleFactor;
    }

    public float getBodyYOffset() {
        return this.bodyYOffset;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public void renderToBuffer(PoseStack p_273029_, VertexConsumer p_272763_, int p_273665_, int p_272602_, int p_350346_) {
        if (this.young) {
            p_273029_.pushPose();
            p_273029_.scale(0.6f, 0.6f, 0.6f);
            p_273029_.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
            super.renderToBuffer(p_273029_, p_272763_, p_273665_, p_272602_, FastColor.ARGB32.multiply(p_350346_, this.color));
            p_273029_.popPose();
        } else {
            super.renderToBuffer(p_273029_, p_272763_, p_273665_, p_272602_, FastColor.ARGB32.multiply(p_350346_, this.color));
        }
    }
}