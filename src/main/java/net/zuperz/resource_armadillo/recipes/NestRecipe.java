package net.zuperz.resource_armadillo.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;

public class NestRecipe implements Recipe<RecipeInput> {

    public final ItemStack output;
    public final Ingredient armadillo_ingredient_1;


    public NestRecipe(ItemStack output, Ingredient armadillo_ingredient_1) {
        this.output = output;
        this.armadillo_ingredient_1 = armadillo_ingredient_1;
    }
    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return output;
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "nest");
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
        if (pLevel.isClientSide()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, armadillo_ingredient_1);
        return ingredients;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public String getGroup() {
        return "nest";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NestRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return NestRecipe.Type.INSTANCE;
    }

    public static final class Type implements RecipeType<NestRecipe> {
        private Type() { }
        public static final NestRecipe.Type INSTANCE = new NestRecipe.Type();
        public static final String ID = "nest";
    }
    public static final class Serializer implements RecipeSerializer<NestRecipe> {
        private Serializer() {}
        public static final NestRecipe.Serializer INSTANCE = new NestRecipe.Serializer();
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "nest");

        private final MapCodec<NestRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("armadillo_ingredient_1").forGetter((recipe) -> {
                return recipe.armadillo_ingredient_1;
            })).apply(instance, NestRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, NestRecipe> STREAM_CODEC = StreamCodec.of(
                NestRecipe.Serializer::write, NestRecipe.Serializer::read);

        @Override
        public MapCodec<NestRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NestRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static NestRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input0 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new NestRecipe(output, input0);
        }


        private static void write(RegistryFriendlyByteBuf  buffer, NestRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.armadillo_ingredient_1);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}