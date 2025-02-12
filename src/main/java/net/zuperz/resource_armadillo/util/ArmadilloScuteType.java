package net.zuperz.resource_armadillo.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.zuperz.resource_armadillo.item.ModArmadilloScutes;

import java.util.function.Supplier;

/*
 *  MIT License
 *  Copyright (c) 2020 BlakeBr0
 *
 *  This code is licensed under the "MIT License"
 *  https://github.com/BlakeBr0/MysticalCustomization/blob/1.21/LICENSE
 *
 *  Modified by: ZuperZ
 */

public class ArmadilloScuteType {

    private final ResourceLocation id;
    private ResourceLocation texture;
    private ResourceLocation armorTexture;
    private Component displayName;
    private Supplier<? extends Item> essence;
    private boolean enabled;
    private boolean registerEssenceItem;

    public ArmadilloScuteType(ResourceLocation id, ResourceLocation texture, ResourceLocation armorTexture, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
        this.registerEssenceItem = true;
        this.texture = texture;
        this.armorTexture = armorTexture;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public ResourceLocation getArmorTexture() {
        return this.armorTexture;
    }

    public ArmadilloScuteType setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.getId().getPath();
    }

    public String getModId() {
        return this.getId().getNamespace();
    }

    public String getNameWithSuffix(String suffix) {
        return String.format("%s_%s", this.getName(), suffix);
    }

    public Component getDisplayName() {
        return this.displayName != null
                ? this.displayName
                : Component.translatable(String.format("armadillo_scute.%s.%s", this.getModId(), this.getName()));
    }

    public ArmadilloScuteType setDisplayName(Component name) {
        this.displayName = name;
        return this;
    }

    public Item getEssenceItem() {
        return this.essence == null ? null : this.essence.get();
    }

    public ArmadilloScuteType setEssenceItem(Supplier<? extends Item> essence) {
        return this.setEssenceItem(essence, false);
    }

    public ArmadilloScuteType setEssenceItem(Supplier<? extends Item> essence, boolean register) {
        this.essence = essence;
        this.registerEssenceItem = register;
        return this;
    }

    public boolean shouldRegisterEssenceItem() {
        return this.registerEssenceItem;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ArmadilloScuteType setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}