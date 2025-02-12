package net.zuperz.resource_armadillo;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.block.entity.ModBlockEntities;
import net.zuperz.resource_armadillo.block.entity.renderer.CentrifugeBlockEntityRenderer;
import net.zuperz.resource_armadillo.block.entity.renderer.RoostBlockEntityRenderer;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ModEntities;
import net.zuperz.resource_armadillo.entity.custom.armadillo.ResourceArmadilloEntity;
import net.zuperz.resource_armadillo.entity.custom.armadillo.client.ModModelLayers;
import net.zuperz.resource_armadillo.entity.custom.armadillo.client.ResourceArmadilloModel;
import net.zuperz.resource_armadillo.entity.custom.armadillo.client.ResourceArmadilloRenderer;
import net.zuperz.resource_armadillo.entity.custom.armadillo.type.ResourceEntityDataSerializers;
import net.zuperz.resource_armadillo.entity.custom.armadillo.type.ResourceSensorTypes;
import net.zuperz.resource_armadillo.item.ModArmadilloScutes;
import net.zuperz.resource_armadillo.item.ModCreativeModeTabs;
import net.zuperz.resource_armadillo.item.ModItems;
import net.zuperz.resource_armadillo.item.custom.RainbowItem;
import net.zuperz.resource_armadillo.item.custom.component.ModDataComponentTypes;
import net.zuperz.resource_armadillo.recipes.ModRecipes;
import net.zuperz.resource_armadillo.screen.ArmadilloHiveScreen;
import net.zuperz.resource_armadillo.screen.RoostScreen;
import net.zuperz.resource_armadillo.screen.slot.ModMenuTypes;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import org.slf4j.Logger;import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ResourceArmadillo.MOD_ID)
public class ResourceArmadillo
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "resource_armadillo";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean networkingRegistered = false;
    private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ResourceArmadillo(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerNetworking);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);
        ModDataComponentTypes.register(modEventBus);

        ModMenuTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);

        ModEntities.register(modEventBus);

        ResourceEntityDataSerializers.register(modEventBus);
        ResourceSensorTypes.register(modEventBus);

        ArmadilloScuteRegistry registry = ArmadilloScuteRegistry.getInstance();
        ModArmadilloScutes.registerAll(registry);
        modEventBus.addListener(ModItems::onRegisterItems);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.RESOURCE_ARMADILLO.get(), ResourceArmadilloRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ModModelLayers.RESOURCE_ARMADILLO, ResourceArmadilloModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.RESOURCE_ARMADILLO.get(), ResourceArmadilloEntity.createAttributes().build());
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.ROOST_BLOCK_ENTITY.get(), RoostBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.CENTRIFUGE_BLOCK_ENTITY.get(), CentrifugeBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.ATOMIC_OVEN_MENU.get(), RoostScreen::new);
            event.register(ModMenuTypes.ARMADILLO_HIVE_MENU.get(), ArmadilloHiveScreen::new);
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, layer) -> {
                if (layer == 1) {
                    return RainbowItem.getRainbowColor();
                }
                return -1;
            }, ModItems.ARMADILLO_TAB.get());
        }
    }

    private static record NetworkMessage<T extends CustomPacketPayload>(
            StreamCodec<? extends FriendlyByteBuf, T> reader,
            IPayloadHandler<T> handler) {
    }

    public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id,
                                                                         StreamCodec<? extends FriendlyByteBuf, T> reader,
                                                                         IPayloadHandler<T> handler) {
        if (networkingRegistered) {
            throw new IllegalStateException("Cannot register new network messages after networking has been registered");
        }
        MESSAGES.put(id, new NetworkMessage<>(reader, handler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID);
        MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
        networkingRegistered = true;
    }
}
