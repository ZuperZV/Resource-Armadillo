package net.zuperz.resource_armadillo.item.custom;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

public class ArmadilloAnimalArmorItem extends ArmorItem {
    private final ArmadilloScuteType armadilloScuteType;
    private final ResourceLocation textureLocation;
    @Nullable
    private final ResourceLocation overlayTextureLocation;
    private final ArmadilloAnimalArmorItem.BodyType bodyType;

    public ArmadilloAnimalArmorItem(Holder<ArmorMaterial> armorMaterial, ArmadilloScuteType scuteType, ArmadilloAnimalArmorItem.BodyType bodyType, boolean hasOverlay, Item.Properties properties, ArmadilloScuteType armadilloScuteType) {
        super(armorMaterial, ArmorItem.Type.BODY, properties);
        this.bodyType = bodyType;
        this.armadilloScuteType = armadilloScuteType;

        this.textureLocation = scuteType.getArmorTexture();

        if (hasOverlay) {
            this.overlayTextureLocation = ResourceLocation.fromNamespaceAndPath(this.textureLocation.getNamespace(), this.textureLocation.getPath().replace(".png", "_overlay.png"));
        } else {
            this.overlayTextureLocation = null;
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.armadilloScuteType.getDisplayName().getString())
                .append(Component.literal(" "))
                .append(Component.translatable("item.resource_armadillo.resource_armadillo_armor_scute"));
    }

    public ResourceLocation getTexture() {
        return this.textureLocation;
    }

    @Nullable
    public ResourceLocation getOverlayTexture() {
        return this.overlayTextureLocation;
    }

    public ArmadilloAnimalArmorItem.BodyType getBodyType() {
        return this.bodyType;
    }

    @Override
    public SoundEvent getBreakingSound() {
        return this.bodyType.breakingSound;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static enum BodyType {
        EQUESTRIAN(loc -> loc.withPath(p -> "textures/entity/horse/armor/horse_armor_" + p), SoundEvents.ITEM_BREAK),
        CANINE(loc -> loc.withPath("textures/entity/wolf/wolf_armor"), SoundEvents.WOLF_ARMOR_BREAK);

        final Function<ResourceLocation, ResourceLocation> textureLocator;
        final SoundEvent breakingSound;

        private BodyType(Function<ResourceLocation, ResourceLocation> textureLocator, SoundEvent breakingSound) {
            this.textureLocator = textureLocator;
            this.breakingSound = breakingSound;
        }
    }
}