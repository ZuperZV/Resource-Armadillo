package net.zuperz.resource_armadillo.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.screen.renderer.Tooltip;
import net.zuperz.resource_armadillo.util.MouseUtil;
import org.apache.logging.log4j.core.appender.rolling.action.IfAccumulatedFileCount;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class RoostScreen extends AbstractContainerScreen<RoostMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/atomic_oven_gui.png");
    private Tooltip ArmadilloTooltipTest;
    private Tooltip ResourceArmadilloTooltipTest;

    public RoostScreen(RoostMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        ArmadilloTooltip();
        ResourceArmadilloTooltip();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        updateResourceArmadilloTooltip(pMouseX, pMouseY, x, y);

        renderArmadilloAreaTooltip(guiGraphics, pMouseX, pMouseY, x, y);
        renderResourceArmadilloAreaTooltip(guiGraphics, pMouseX, pMouseY, x, y);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int progressArrowWidth = menu.getScaledProgress();
        guiGraphics.blit(TEXTURE, x + 81, y + 38, 176, 0, progressArrowWidth, 15);

        int scaledFuel = menu.getScaledFuelBurnTime();
        guiGraphics.blit(TEXTURE, x + 52, y + 38, 199, 0, 14, scaledFuel);

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            if (menu.isCrafting()) {

                int size = 0;
                if (menu.isSlotItem(3).getItem() == Items.STONE) {
                    size = 1;
                }

                int scaledProgress = size == 1 ? menu.getBabyScaledEntityProgress() : menu.getScaledEntityProgress();

                ResourceArmadilloEntity armadillo = ModEntities.RESOURCE_ARMADILLO.get().create(level);
                if (armadillo != null) {
                    armadillo.setPos(0, 0, 0);
                    InventoryScreen.renderEntityInInventory(
                            guiGraphics,
                            x + 132, y + 52,
                            scaledProgress,
                            new Vector3f(0, 0, 0),
                            new Quaternionf().rotationYXZ((float) Math.PI / 1.5f, 0, (float) Math.PI),
                            null,
                            armadillo
                    );
                }
            }
        }


        if (level != null) {
            if (menu.isSlotItem(3).getItem() == Items.DIRT || menu.isSlotItem(3).getItem() == Items.STONE) {
                int size = 20;
                if (menu.isSlotItem(3).getItem() == Items.STONE) {
                    size = 10;
                }

                Armadillo armadillo = EntityType.ARMADILLO.create(level);
                if (armadillo != null) {
                    armadillo.setPos(0, 0, 0);
                    InventoryScreen.renderEntityInInventory(
                            guiGraphics,
                            x + 58, y + 32,
                            size,
                            new Vector3f(0, 0, 0),
                            new Quaternionf().rotationYXZ((float) Math.PI / 1.5f, 0, (float) Math.PI),
                            null,
                            armadillo
                    );
                }
            }
        }
    }

    private void renderArmadilloAreaTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 46, 11, 25, 25)) {
            if (menu.isSlotItem(3).getItem() == Items.DIRT || menu.isSlotItem(3).getItem() == Items.STONE) {
                guiGraphics.renderTooltip(this.font, ArmadilloTooltipTest.getTooltips(),
                        Optional.empty(), pMouseX - x, pMouseY - y);
            }
        }
    }

    private void ArmadilloTooltip() {
        String newTooltipText;
        if (menu.isSlotItem(3).getItem() == Items.STONE) {
            newTooltipText = "Baby Armadillo";
        } else {
            newTooltipText = "Armadillo";
        }
        ArmadilloTooltipTest = new Tooltip(46,
                11, newTooltipText, 25, 25);
    }




    private void renderResourceArmadilloAreaTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 120, 32, 25, 25)) {
            if (ResourceArmadilloTooltipTest != null) {
                if (menu.isSlotItem(3).getItem() == Items.DIRT || menu.isSlotItem(3).getItem() == Items.STONE) {
                    guiGraphics.renderTooltip(this.font, ResourceArmadilloTooltipTest.getTooltips(),
                            Optional.empty(), pMouseX - x, pMouseY - y);
                }
            }
        }
    }

    private void ResourceArmadilloTooltip() {
        String stringGoingToBeCrafted = menu.blockentity.getSlotInputItems(4).toString();

        if (stringGoingToBeCrafted != null && !stringGoingToBeCrafted.isEmpty()) {
            String[] parts = stringGoingToBeCrafted.split(":");
            String itemName = parts.length > 1 ? parts[1] : parts[0];

            itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
            ResourceArmadilloTooltipTest = new Tooltip(120, 32, itemName + " Resource Armadillo", 25, 25);
        } else {
            ResourceArmadilloTooltipTest = new Tooltip(120, 32, "Resource Armadillo", 25, 25);
        }
    }

    private String lastTooltipText = "";

    private void updateResourceArmadilloTooltip(int pMouseX, int pMouseY, int guiX, int guiY) {
        if (isMouseAboveArea(pMouseX, pMouseY, guiX, guiY, 120, 32, 25, 25)) {
            String stringGoingToBeCrafted = menu.blockentity.getSlotInputItems(4).toString();
            String newTooltipText = null;
            int size = 0;
            if (menu.isSlotItem(3).getItem() == Items.STONE) {
                size = 1;
            }

            if (stringGoingToBeCrafted != null && !stringGoingToBeCrafted.isEmpty() && menu.blockentity.getProgress() > 0) {
                String[] parts = stringGoingToBeCrafted.split(":");
                String babyText = "";
                if (size == 1) {
                    babyText = "Baby ";
                }
                String itemName = parts.length > 1 ? parts[1] : parts[0];
                newTooltipText = babyText + itemName.substring(0, 1).toUpperCase() + itemName.substring(1) + " Resource Armadillo";
            }

            else if (menu.blockentity.getProgress() > 0) {
                newTooltipText = "Resource Armadillo";
                if (size == 1) {
                    newTooltipText = "Baby Resource Armadillo";
                }
            } else {
                newTooltipText = null;
            }

            if (newTooltipText != null && !newTooltipText.equals(lastTooltipText)) {
                lastTooltipText = newTooltipText;
                ResourceArmadilloTooltipTest = new Tooltip(120, 32, newTooltipText, 25, 25);
            }
            else if (newTooltipText == null) {
                lastTooltipText = "";
                ResourceArmadilloTooltipTest = null;
            }
        } else {
            lastTooltipText = "";
            ResourceArmadilloTooltipTest = null;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}