package net.zuperz.resource_armadillo.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.zuperz.resource_armadillo.ResourceArmadillo;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ResourceArmadillo.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ResourceArmadillo.MOD_ID);

    public static void register(IEventBus eventBus){
        RECIPE_TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }

    public static final Supplier<RecipeType<AtomicOvenRecipe>> ATOMIC_OVEN_RECIPE_TYPE =
            RECIPE_TYPES.register("atomic_oven", () -> AtomicOvenRecipe.Type.INSTANCE);

    public static final Supplier<RecipeType<ResourceArmadilloEntityRecipe>> RESOURCE_ARMADILLO_RECIPE_TYPE =
            RECIPE_TYPES.register("resource_armadillo", () -> ResourceArmadilloEntityRecipe.Type.INSTANCE);


    public static final Supplier<RecipeSerializer<AtomicOvenRecipe>> ATOMIC_OVEN_SERIALIZER =
            SERIALIZERS.register("atomic_oven", () -> AtomicOvenRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<ResourceArmadilloEntityRecipe>> RESOURCE_ARMADILLO_SERIALIZER =
            SERIALIZERS.register("resource_armadillo", () -> ResourceArmadilloEntityRecipe.Serializer.INSTANCE);
}