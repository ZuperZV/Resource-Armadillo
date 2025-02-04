package net.zuperz.resource_armadillo.item.custom.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public class ItemRecipeData {

    private String value;

    public ItemRecipeData(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof ItemRecipeData ex
                    && Objects.equals(this.value, ex.value);
        }
    }

    public static final Codec<ItemRecipeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("value").forGetter(ItemRecipeData::getValue)
            ).apply(instance, ItemRecipeData::new)
    );
}
