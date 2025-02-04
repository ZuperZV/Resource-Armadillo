package net.zuperz.resource_armadillo.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;

import java.util.function.Supplier;

public class ArmadilloScuteType {

    private final ResourceLocation id;
    private Component displayName;
    private Supplier<? extends Item> essence;
    private Ingredient craftingMaterial;
    private boolean enabled;
    private boolean registerEssenceItem;

    public ArmadilloScuteType(ResourceLocation id, Ingredient craftingMaterial, boolean enabled) {
        this.id = id;
        this.craftingMaterial = craftingMaterial;
        this.enabled = enabled;
        this.registerEssenceItem = true;
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

    public Ingredient getCraftingMaterial() {
        return this.craftingMaterial;
    }

    public ArmadilloScuteType setCraftingMaterial(Ingredient ingredient) {
        this.craftingMaterial = ingredient;
        return this;
    }

    public Ingredient getIngredient() {
        return this.craftingMaterial;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ArmadilloScuteType setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}