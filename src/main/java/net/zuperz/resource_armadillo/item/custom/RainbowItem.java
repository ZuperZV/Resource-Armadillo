package net.zuperz.resource_armadillo.item.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;

public class RainbowItem extends Item {

    public RainbowItem(Item.Properties properties) {
        super(properties);
    }

    public static int getRainbowColor() {
        long time = Minecraft.getInstance().level.getGameTime();
        float hue = (time % 500) / 500.0f;
        return hsvToRgb(hue, 1.0f, 1.0f);
    }

    private static int hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        float r, g, b;
        switch (h % 6) {
            case 0 -> { r = value; g = t; b = p; }
            case 1 -> { r = q; g = value; b = p; }
            case 2 -> { r = p; g = value; b = t; }
            case 3 -> { r = p; g = q; b = value; }
            case 4 -> { r = t; g = p; b = value; }
            case 5 -> { r = value; g = p; b = q; }
            default -> { r = g = b = 0; }
        }

        return FastColor.ARGB32.color(255, (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }
}

