package net.zuperz.resource_armadillo.item.custom;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

public class RainbowSpawnEggItem extends DeferredSpawnEggItem {
    private final int backgroundColor;

    public RainbowSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, Properties props) {
        super(type, backgroundColor, 0, props);
        this.backgroundColor = backgroundColor;
    }

    @Override
    public int getColor(int layer) {
        if (layer == 0) {
            return backgroundColor;
        } else {
            long time = Minecraft.getInstance().level.getGameTime();
            float hue = (time % 500) / 500.0f;
            return hsvToRgb(hue, 1.0f, 1.0f);
        }
    }

    private static int hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        float r, g, b;
        switch (h % 6) {
            case 0: r = value; g = t; b = p; break;
            case 1: r = q; g = value; b = p; break;
            case 2: r = p; g = value; b = t; break;
            case 3: r = p; g = q; b = value; break;
            case 4: r = t; g = p; b = value; break;
            case 5: r = value; g = p; b = q; break;
            default: r = g = b = 0;
        }

        return (255 << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }
}