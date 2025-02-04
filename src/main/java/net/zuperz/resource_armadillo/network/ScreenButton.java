package net.zuperz.resource_armadillo.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.block.entity.custom.ArmadilloHiveBlockEntity;
import net.zuperz.resource_armadillo.block.entity.custom.RoostBlockEntity;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record ScreenButton(int buttonID, int x, int y, int z) implements CustomPacketPayload {

    public static final Type<ScreenButton> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "gui_buttons"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenButton> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ScreenButton message) -> {
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }, (RegistryFriendlyByteBuf buffer) -> new ScreenButton(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
    @Override
    public Type<ScreenButton> type() {
        return TYPE;
    }

    public static void handleData(final ScreenButton message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                Player player = context.player();
                Level level = player.level();
                BlockPos pos = new BlockPos(message.x, message.y, message.z);

                handleButtonAction(message.buttonID, pos, level, player);
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }


    public static void handleButtonAction(int buttonID, BlockPos pos, Level level, Player player) {
        if (buttonID == 0) {
            if (level != null && pos != null) {
                if (level.getBlockEntity(pos) instanceof RoostBlockEntity blockEntity) {

                    System.out.println("Interacting with RoostBlockEntity: " + blockEntity);

                    blockEntity.spawnResourceArmadilloFromData(blockEntity, "0.5");
                }
            }
        } else if (buttonID == 1) {
            if (level != null && pos != null) {
                if (level.getBlockEntity(pos) instanceof ArmadilloHiveBlockEntity blockEntity) {

                    System.out.println("Interacting with ArmadilloHiveBlockEntity: " + blockEntity);

                    blockEntity.spawnResourceArmadilloFromData(blockEntity, "0.5", 1);
                }
            }
        } else if (buttonID == 2) {
            if (level != null && pos != null) {
                if (level.getBlockEntity(pos) instanceof ArmadilloHiveBlockEntity blockEntity) {

                    System.out.println("Interacting with ArmadilloHiveBlockEntity: " + blockEntity);

                    blockEntity.spawnResourceArmadilloFromData(blockEntity, "0.5", 2);
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        ResourceArmadillo.addNetworkMessage(ScreenButton.TYPE, ScreenButton.STREAM_CODEC, ScreenButton::handleData);
    }
}
