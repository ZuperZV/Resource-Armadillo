package net.zuperz.resource_armadillo;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = ResourceArmadillo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue MAKE_A_NEW_RESOURCE = BUILDER
            .comment("To create a new resource, follow these steps:")
            .comment("")
            .comment("1. Create a new folder called `resource_armadillo` inside the `config` folder, where this file is located.")
            .comment("2. Inside the `resource_armadillo` folder, create a subfolder named `scute`.")
            .comment("3. In the `scute` folder, create a `.json` file. You can name this file anything you like, but ensure it has a `.json` extension.")
            .comment("   Example path: `config/resource_armadillo/scute/your_resource.json`")
            .comment("4. The content of the `your_resource.json` file should look like this:")
            .comment("")
            .comment("{")
            .comment("    \"id\": \"unique_scute_id\",    // this is example \"redstone\" or \"diamond\"")
            .comment("    \"texture\": \"textures/path/to/scute_texture.png\",")
            .comment("    \"armor_texture\": \"textures/path/to/armor_texture.png\",")
            .comment("    \"enabled\": true")
            .comment("}")
            .comment("")
            .comment("5. Set the `enabled` field to `true` or `false` to determine whether the resource is active.")
            .comment("")
            .comment("After these steps, restart the game to load the new resource.")
            .define("makeANewResource", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean makeANewResource;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        makeANewResource = MAKE_A_NEW_RESOURCE.get();
    }
}
