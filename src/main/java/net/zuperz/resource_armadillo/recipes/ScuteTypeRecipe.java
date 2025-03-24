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
import net.zuperz.resource_armadillo.recipes.CodecFix;

public class ScuteTypeRecipe implements Recipe<RecipeInput> {

    public final String name;
    public final String entityTextures;
    public final String wolfTextures;

    public ScuteTypeRecipe(String name, String entityTextures, String wolfTextures) {
        this.name = name;
        this.entityTextures = entityTextures;
        this.wolfTextures = wolfTextures;
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "scute_type");
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
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
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(0);
        return ingredients;
    }
    
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public String getGroup() {
        return "scute_type";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Type.INSTANCE;
    }

    public static final class Type implements RecipeType<net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe> {
        private Type() { }
        public static final net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Type INSTANCE = new net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Type();
        public static final String ID = "scute_type";
    }
    public static final class Serializer implements RecipeSerializer<net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe> {
        private Serializer() {}
        public static final net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Serializer INSTANCE = new net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Serializer();
        public static final ResourceLocation ID =
                ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "scute_type");

        private final MapCodec<net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.STRING.fieldOf("name").forGetter((recipe) -> {
                return recipe.name;
            }), Codec.STRING.fieldOf("entity_texture").forGetter((recipe) -> {
                return recipe.entityTextures;
            }), Codec.STRING.fieldOf("wolf_texture").forGetter((recipe) -> {
                return recipe.wolfTextures;
            })).apply(instance, net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe::new);
        });

        private final StreamCodec<RegistryFriendlyByteBuf, net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe> STREAM_CODEC = StreamCodec.of(
                net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Serializer::write, net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe.Serializer::read);

        @Override
        public MapCodec<net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, net.zuperz.resource_armadillo.recipes.ScuteTypeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ScuteTypeRecipe read(RegistryFriendlyByteBuf buffer) {
            String name = buffer.readUtf();
            String entityTexture = buffer.readUtf();
            String wolfTexture = buffer.readUtf();

            return new ScuteTypeRecipe(name, entityTexture, wolfTexture);
        }


        private static void write(RegistryFriendlyByteBuf buffer, ScuteTypeRecipe recipe) {
            buffer.writeUtf(recipe.name);
            buffer.writeUtf(recipe.entityTextures);
            buffer.writeUtf(recipe.wolfTextures);
        }

    }
}