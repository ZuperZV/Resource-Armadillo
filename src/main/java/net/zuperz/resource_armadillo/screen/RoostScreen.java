package net.zuperz.resource_armadillo.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.network.ScreenButton;
import net.zuperz.resource_armadillo.screen.renderer.ComponentTooltip;
import net.zuperz.resource_armadillo.screen.renderer.Tooltip;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.MouseUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoostScreen extends AbstractContainerScreen<RoostMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/roost_gui.png");
    private ComponentTooltip ArmadilloTooltipTest;
    private Tooltip ResourceArmadilloTooltipTest;

    public RoostScreen(RoostMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        Button button_1 = new PlainTextButton(this.leftPos + 46, this.topPos + 11, 25, 25, Component.translatable(""), e -> {
            BlockPos pos = this.menu.blockentity.getBlockPos();
            PacketDistributor.sendToServer(new ScreenButton(0, pos.getX(), pos.getY(), pos.getZ()));
            System.out.println("Button clicked in RoostScreen!");
        }, this.font);
        this.addRenderableWidget(button_1);


        ArmadilloTooltip();
        ResourceArmadilloTooltip();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

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

        //int i = this.leftPos;
        //int j = this.topPos;
        //int l = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
        //guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace("container/furnace/lit_progress"), 14, 14, 0, 14 - l, i + 56, j + 36 + 14 - l, 14, l);

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            if (menu.isCrafting()) {

                int size = 0;
                if (menu.blockentity.isArmadilloBaby()) {
                    size = 1;
                }

                int scaledProgress = size == 1 ? menu.getBabyScaledEntityProgress() : menu.getScaledEntityProgress();
                ItemStack itemStack = menu.blockentity.getSlotInputItems(4);

                ResourceArmadilloEntity armadillo = ModEntities.RESOURCE_ARMADILLO.get().create(level);
                if (armadillo != null) {
                    armadillo.setYHeadRot(0);
                    armadillo.setPos(0, 0, 0);
                    armadillo.setResource(itemStack);
                    armadillo.updateVariantFromResource();
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
            if (!menu.blockentity.isArmadilloDataEmpty()) {
                int size = 20;
                if (menu.blockentity.isArmadilloBaby()) {
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
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 46, 11, 25, 25)) {
            if (!menu.blockentity.isArmadilloDataEmpty()) {
                guiGraphics.renderTooltip(this.font, ArmadilloTooltipTest.getComponentTooltips(),
                        Optional.empty(), pMouseX - x, pMouseY - y);
            }
        }
    }

    private void ArmadilloTooltip() {
        String itemName = "";

        String armadilloType = menu.blockentity.isArmadilloBaby() ? "Baby Armadillo" : "Armadillo";

        Component tooltipText = Component.literal(itemName + armadilloType);
        Component tooltipText2 = CommonComponents.EMPTY;
        Component tooltipText3 = Component.translatable("tool_tip.resource_armadillo.left_click")
                .append(Component.literal(" " + itemName + armadilloType).withStyle(ChatFormatting.GRAY))
                .append(Component.translatable("tool_tip.resource_armadillo.out_of_the_roost"));

        ArmadilloTooltipTest = new ComponentTooltip(46, 11, tooltipText, tooltipText2, tooltipText3, 25, 25);
    }

    private String formatItemName(String rawName) {
        if (rawName == null || rawName.isEmpty()) return "";

        return Arrays.stream(rawName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private void renderResourceArmadilloAreaTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 120, 32, 25, 25)) {
            if (ResourceArmadilloTooltipTest != null) {
                if (!menu.blockentity.isArmadilloDataEmpty()) {
                    guiGraphics.renderTooltip(this.font, ResourceArmadilloTooltipTest.getTooltips(),
                            Optional.empty(), pMouseX - x, pMouseY - y);
                }
            }
        }
    }

    private void ResourceArmadilloTooltip() {
        ItemStack itemStack = menu.blockentity.getSlotInputItems(4);
        String itemName = "Resource Armadillo";

        if (!itemStack.isEmpty()) {
            ResourceLocation itemID = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
            itemName = formatItemName(itemID.getPath());
            itemName = itemName + " Armadillo";
        }

        ResourceArmadilloTooltipTest = new Tooltip(120, 32, itemName, 25, 25);
    }



    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        ResourceArmadilloTooltip();
        ArmadilloTooltip();
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}