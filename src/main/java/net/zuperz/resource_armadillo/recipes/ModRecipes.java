package net.zuperz.resource_armadillo.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.zuperz.resource_armadillo.ResourceArmadillo;

import java.util.List;
import java.util.function.Consumer;
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

    // Recipes
    public static final Supplier<RecipeType<RoostRecipe>> ROOST_RECIPE_TYPE =
            RECIPE_TYPES.register("roost", () -> RoostRecipe.Type.INSTANCE);

    public static final Supplier<RecipeType<BreedingRecipe>> BREEDING_ARMADILLO_RECIPE_TYPE =
            RECIPE_TYPES.register("breeding", () -> BreedingRecipe.Type.INSTANCE);

    public static final Supplier<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE =
            RECIPE_TYPES.register("centrifuge", () -> CentrifugeRecipe.Type.INSTANCE);

    public static final Supplier<RecipeType<NestRecipe>> NEST_RECIPE_TYPE =
            RECIPE_TYPES.register("nest", () -> NestRecipe.Type.INSTANCE);

    public static final Supplier<RecipeType<ScuteTypeRecipe>> SCUTE_TYPE_RECIPE_TYPE =
            RECIPE_TYPES.register("scute_type", () -> ScuteTypeRecipe.Type.INSTANCE);

    // Serializers
    public static final Supplier<RecipeSerializer<RoostRecipe>> ATOMIC_OVEN_SERIALIZER =
            SERIALIZERS.register("roost", () -> RoostRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<BreedingRecipe>> BREEDING_ARMADILLO_SERIALIZER =
            SERIALIZERS.register("breeding", () -> BreedingRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<CentrifugeRecipe>> CENTRIFUGE_SERIALIZER =
            SERIALIZERS.register("centrifuge", () -> CentrifugeRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<NestRecipe>> NEST_SERIALIZER =
            SERIALIZERS.register("nest", () -> NestRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<ScuteTypeRecipe>> SCUTE_TYPE_SERIALIZER =
            SERIALIZERS.register("scute_type", () -> ScuteTypeRecipe.Serializer.INSTANCE);
}