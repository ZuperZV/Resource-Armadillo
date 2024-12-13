package net.zuperz.resource_armadillo.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public final class CodecFix {
    private CodecFix() {}

    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(ItemStack::getItemHolder),
                        ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
                        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).
                                forGetter(ItemStack::getComponentsPatch)).
                apply(instance, ItemStack::new);
    });

    public static final Codec<ItemStack> OPTIONAL_ITEM_STACK_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.ITEM_NON_AIR_CODEC.optionalFieldOf("item").forGetter(stack ->
                            stack.isEmpty() ? Optional.empty() : Optional.of(stack.getItemHolder())),

                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),

                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(ItemStack::getComponentsPatch)
            ).apply(instance, (itemHolderOpt, count, components) -> {
                if (itemHolderOpt.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                return new ItemStack(itemHolderOpt.get(), count, components);
            })
    );

    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ResourceLocation.CODEC.fieldOf("fluid").forGetter(fluidStack -> BuiltInRegistries.FLUID.getKey(fluidStack.getFluid())),
                Codec.INT.optionalFieldOf("amount", 1000).forGetter(FluidStack::getAmount)
        ).apply(instance, (fluidLocation, amount) -> {
            Fluid fluid = BuiltInRegistries.FLUID.get(fluidLocation);
            return new FluidStack(fluid, amount);
        });
    });

}