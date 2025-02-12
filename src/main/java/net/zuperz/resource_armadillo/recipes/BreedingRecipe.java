package net.zuperz.resource_armadillo.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.entity.custom.ArmadilloHiveBlockEntity;

public class BreedingRecipe implements Recipe<RecipeInput> {

    public final ItemStack output;
    public final Ingredient armadillo_ingredient_1;
    public final Ingredient armadillo_ingredient_2;

    public final Ingredient food_1;
    public final Ingredient food_2;


    public BreedingRecipe(ItemStack output, Ingredient armadillo_ingredient_1, Ingredient armadillo_ingredient_2, Ingredient food_1, Ingredient food_2) {
        this.output = output;
        this.armadillo_ingredient_1 = armadillo_ingredient_1;
        this.armadillo_ingredient_2 = armadillo_ingredient_2;

        this.food_1 = food_1;
        this.food_2 = food_2;
    }
    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return output;
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "breeding");
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
        /*BlockPos blockPos = BlockPos.of(Long.parseLong(position));
        BlockEntity blockEntity = pLevel.getBlockEntity(blockPos);
        System.out.println("position: " + blockPos);
        if (blockEntity instanceof ArmadilloHiveBlockEntity armadilloHive) {
            String armadillo1Item = armadilloHive.getStoredArmadilloDataValue("resource_quality");
            String armadillo2Item = armadilloHive.getStoredArmadilloData2Value("resource_quality");

            ItemStack armadillo1Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo1Item.split(" ")[1])));
            ItemStack armadillo2Stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(armadillo2Item.split(" ")[1])));

        }
         */
        return food_1.test(pContainer.getItem(0)) && food_2.test(pContainer.getItem(1));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(4);
        ingredients.add(0, food_1);
        ingredients.add(1, food_2);
        ingredients.add(2, armadillo_ingredient_1);
        ingredients.add(3, armadillo_ingredient_2);
        return ingredients;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public String getGroup() {
        return "breeding";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<BreedingRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "breeding";
    }
    public static final class Serializer implements RecipeSerializer<BreedingRecipe> {
        private Serializer() {}
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "breeding");

        private final MapCodec<BreedingRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("armadillo_ingredient_1").forGetter((recipe) -> {
                return recipe.armadillo_ingredient_1;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("armadillo_ingredient_2").forGetter((recipe) -> {
                return recipe.armadillo_ingredient_2;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("food_1").forGetter((recipe) -> {
                return recipe.food_1;
            }), Ingredient.CODEC_NONEMPTY.fieldOf("food_2").forGetter((recipe) -> {
                return recipe.food_2;
            })).apply(instance, BreedingRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, BreedingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<BreedingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BreedingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BreedingRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input0 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient input1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            Ingredient input2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient input3 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new BreedingRecipe(output, input0, input1, input2, input3);
        }


        private static void write(RegistryFriendlyByteBuf  buffer, BreedingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.armadillo_ingredient_1);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.armadillo_ingredient_2);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.food_1);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.food_2);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}