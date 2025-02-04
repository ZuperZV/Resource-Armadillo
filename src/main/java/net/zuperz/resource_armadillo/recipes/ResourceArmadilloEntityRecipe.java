package net.zuperz.resource_armadillo.recipes;

import com.mojang.serialization.Codec;
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

public class ResourceArmadilloEntityRecipe implements Recipe<RecipeInput> {

    public final ItemStack output;
    public final Ingredient resource;
    public final Ingredient scuteResource;
    public final String overlayTexture;

    public ResourceArmadilloEntityRecipe(ItemStack output, Ingredient resource, Ingredient scuteResource, String overlayTexture) {
        this.output = output;
        this.resource = resource;
        this.scuteResource = scuteResource;
        this.overlayTexture = overlayTexture;
    }
    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return output;
    }

    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "resource_armadillo");
    }

    public Ingredient getScuteResource() {
        return scuteResource;
    }

    public String getOverlayTexture() {
        return overlayTexture;
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
        if(pLevel.isClientSide()) {
            return false;
        }
        return resource.test(pContainer.getItem(0));
    }
    @Override
    public boolean isSpecial() {
        return true;
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, resource);
        return ingredients;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public String getGroup() {
        return "resource_armadillo";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<ResourceArmadilloEntityRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "resource_armadillo";
    }
    public static final class Serializer implements RecipeSerializer<ResourceArmadilloEntityRecipe> {
        private Serializer() {}
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "resource_armadillo");

        private final MapCodec<ResourceArmadilloEntityRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(
                    CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                        return recipe.output;
                    }),
                    Ingredient.CODEC_NONEMPTY.fieldOf("resource").forGetter((recipe) -> {
                        return recipe.resource;
                    }),
                    Ingredient.CODEC_NONEMPTY.fieldOf("scuteResource").forGetter((recipe) -> {
                        return recipe.scuteResource;
                    }),
                    Codec.STRING.fieldOf("overlayTexture").forGetter((recipe) -> {
                        return recipe.overlayTexture;
                    })
            ).apply(instance, ResourceArmadilloEntityRecipe::new);
        });


        private final StreamCodec<RegistryFriendlyByteBuf, ResourceArmadilloEntityRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<ResourceArmadilloEntityRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ResourceArmadilloEntityRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ResourceArmadilloEntityRecipe read(RegistryFriendlyByteBuf buffer) {
            Ingredient input0 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient input1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            String overlayTexture = buffer.readUtf();
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

            return new ResourceArmadilloEntityRecipe(output, input0, input1, overlayTexture);
        }

        private static void write(RegistryFriendlyByteBuf buffer, ResourceArmadilloEntityRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.resource);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.scuteResource);
            buffer.writeUtf(recipe.getOverlayTexture());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}