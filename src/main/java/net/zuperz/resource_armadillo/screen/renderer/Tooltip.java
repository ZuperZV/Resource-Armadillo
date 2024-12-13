package net.zuperz.resource_armadillo.screen.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  https://github.com/BluSunrize/ImmersiveEngineering/blob/1.19.2/LICENSE
 *
 *  Slightly Modified Version by: Kaupenjoe
 */
public class Tooltip {
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final String name;

    public Tooltip(int xMin, int yMin, String name, int width, int height)  {
        xPos = xMin;
        yPos = yMin;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public List<Component> getTooltips() {
        return List.of(Component.literal(name));
    }
}