package net.zuperz.resource_armadillo.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.zuperz.resource_armadillo.ResourceArmadillo;

public class CentrifugeRecipe implements Recipe<RecipeInput> {

    public final ItemStack output;
    public final Ingredient ingredient;

    public CentrifugeRecipe(ItemStack output, Ingredient ingredient) {
        this.output = output;
        this.ingredient = ingredient;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return output;
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "centrifuge");
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    public ItemStack getResultEmi(){
        return output.copy();
    }

    @Override
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if (ingredient.test(pContainer.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, ingredient);
        return ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public String getGroup() {
        return "centrifuge";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<CentrifugeRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "centrifuge";
    }

    public static final class Serializer implements RecipeSerializer<CentrifugeRecipe> {
        private Serializer() {}
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "centrifuge");

        private final MapCodec<CentrifugeRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.ingredient;
            })).apply(instance, CentrifugeRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<CentrifugeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CentrifugeRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            return new CentrifugeRecipe(output, input);
        }

        private static void write(RegistryFriendlyByteBuf buffer, CentrifugeRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}