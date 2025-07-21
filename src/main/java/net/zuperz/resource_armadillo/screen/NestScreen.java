package net.zuperz.resource_armadillo.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.network.ScreenButton;
import net.zuperz.resource_armadillo.screen.renderer.ComponentTooltip;
import net.zuperz.resource_armadillo.util.MouseUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NestScreen extends AbstractContainerScreen<NestMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/gui/nest_gui.png");
    private ComponentTooltip ArmadilloTooltipTest;

    private static final ResourceLocation EMPTY_SLOT_BRUSH = ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "item/empty_brush");

    private final CyclingSlotBackground brushIcon = new CyclingSlotBackground(0);

    public NestScreen(NestMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        Button button_1 = new PlainTextButton(this.leftPos + 41, this.topPos + 19, 25, 25, Component.translatable(""), e -> {
            BlockPos pos = this.menu.blockentity.getBlockPos();
            PacketDistributor.sendToServer(new ScreenButton(3, pos.getX(), pos.getY(), pos.getZ()));
        }, this.font);
        this.addRenderableWidget(button_1);

        ArmadilloTooltip();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.brushIcon.tick(Collections.singletonList(EMPTY_SLOT_BRUSH));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderArmadilloAreaTooltip(guiGraphics, pMouseX, pMouseY, x, y);
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
        guiGraphics.blit(TEXTURE, x + 77, y + 39, 176, 0, progressArrowWidth, 15);

        this.brushIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);

        Level level = Minecraft.getInstance().level;

        if (level != null) {
            if (!menu.blockentity.isArmadilloDataEmpty()) {
                int size = 20;
                if (menu.blockentity.isArmadilloBaby()) {
                    return;
                }

                LivingEntity armadillo = null;

                try {

                    if (menu.blockentity.isArmadilloDataAResourceArmadillo()) {
                        CompoundTag armadilloData = TagParser.parseTag(menu.blockentity.getStoredArmadilloData());

                        ListTag rotationTag = new ListTag();
                        rotationTag.add(FloatTag.valueOf(0.0f));
                        armadilloData.put("Rotation", rotationTag);

                        armadilloData.putString("state", "idle");
                        armadilloData.putString("HurtTime", "0s");

                        armadillo = ModEntities.RESOURCE_ARMADILLO.get().create(level);

                        if (armadillo instanceof ResourceArmadilloEntity resourceArmadillo) {
                            resourceArmadillo.load(armadilloData);
                            resourceArmadillo.updateVariantFromResource();
                        }
                    } else {
                        armadillo = EntityType.ARMADILLO.create(level);
                    }

                    if (armadillo != null) {
                        armadillo.setPos(0, 0, 0);
                        InventoryScreen.renderEntityInInventory(
                                guiGraphics,
                                x + 52, y + 39,
                                size,
                                new Vector3f(0, 0, 0),
                                new Quaternionf().rotationYXZ((float) Math.PI / 1.5f, 0, (float) Math.PI),
                                null,
                                armadillo
                        );
                    }
                } catch (CommandSyntaxException e) {
                    System.err.println("Failed to parse Armadillo data: " + e.getMessage());
                }
            }
        }
    }

    private void renderArmadilloAreaTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 41, 19, 25, 25)) {
            if (!menu.blockentity.isArmadilloDataEmpty()) {
                guiGraphics.renderTooltip(this.font, ArmadilloTooltipTest.getComponentTooltips(),
                        Optional.empty(), pMouseX - x, pMouseY - y);
            }
        }
    }

    private void ArmadilloTooltip() {
        String itemName = "";
        if (menu.blockentity.isArmadilloDataAResourceArmadillo()) {
            itemName = getFormattedItemName(menu.blockentity.getStoredArmadilloDataValue("resource_quality")) + " ";
        }

        String armadilloType = menu.blockentity.isArmadilloBaby() ? "Baby Armadillo" : "Armadillo";

        List<Component> pTooltipComponents;

        Component tooltipText = Component.literal(itemName + armadilloType);
        Component tooltipText2 = CommonComponents.EMPTY;
        Component tooltipText3 = Component.translatable("tool_tip.resource_armadillo.left_click")
                .append(Component.literal(" " + itemName + armadilloType).withStyle(ChatFormatting.GRAY))
                .append(Component.translatable("tool_tip.resource_armadillo.out_of_the_roost"));

        ArmadilloTooltipTest = new ComponentTooltip(25, 36, tooltipText, tooltipText2, tooltipText3, 25, 25);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        ArmadilloTooltip();
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }

    private String getFormattedItemName(Object itemSource) {
        String itemName = "";

        if (itemSource instanceof ItemStack itemStack && !itemStack.isEmpty()) {
            ResourceLocation itemID = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
            itemName = itemID.getPath();
        } else if (itemSource instanceof String str && !str.isEmpty()) {
            String[] parts = str.split(":");
            itemName = parts.length > 1 ? parts[1] : parts[0];
        }

        if (!itemName.isEmpty()) {
            itemName = itemName.replace("_", " ");
            itemName = capitalizeWords(itemName);
        }

        return itemName;
    }

    private String capitalizeWords(String input) {
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }
}