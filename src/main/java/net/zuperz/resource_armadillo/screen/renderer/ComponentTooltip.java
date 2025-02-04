package net.zuperz.resource_armadillo.screen.renderer;

import net.minecraft.network.chat.Component;

import java.util.List;

/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  https://github.com/BluSunrize/ImmersiveEngineering/blob/1.19.2/LICENSE
 *
 *  Slightly Modified Version by: Kaupenjoe
 *  Slightly Modified Version by: ZuperZ
 */
public class ComponentTooltip {
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final Component name;
    private final Component name2;
    private final Component name3;

    public ComponentTooltip(int xMin, int yMin, Component name, Component name2, Component name3, int width, int height)  {
        this.xPos = xMin;
        this.yPos = yMin;
        this.width = width;
        this.height = height;
        this.name = name;
        this.name2 = name2;
        this.name3 = name3;
    }

    public List<Component> getComponentTooltips() {
        return List.of(name, name2, name3);
    }
}