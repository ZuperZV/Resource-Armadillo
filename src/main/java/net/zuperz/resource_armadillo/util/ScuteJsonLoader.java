package net.zuperz.resource_armadillo.util;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.loading.FMLPaths;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScuteJsonLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final List<ArmadilloScuteType> LOADED_SCUTES = new ArrayList<>();

    public static void loadScutesFromJson() {
        Path directory = FMLPaths.GAMEDIR.get().resolve("config/resource_armadillo/scute/");

        if (!Files.exists(directory)) {
            System.out.println("[ScuteJsonLoader] JSON-folder findes ikke: " + directory);
            return;
        }

        try {
            Files.list(directory).filter(path -> path.toString().endsWith(".json")).forEach(path -> {
                loadScuteFromFile(path);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadScuteFromFile(Path path) {
        try (FileReader reader = new FileReader(path.toFile())) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);

            String id = json.get("id").getAsString();
            String texture = json.get("texture").getAsString();
            String armorTexture = json.get("armor_texture").getAsString();
            boolean enabled = json.has("enabled") && json.get("enabled").getAsBoolean();


            ArmadilloScuteType scute = new ArmadilloScuteType(
                    ResourceLocation.fromNamespaceAndPath("resource_armadillo", id),
                    ResourceLocation.fromNamespaceAndPath("resource_armadillo", texture),
                    ResourceLocation.fromNamespaceAndPath("resource_armadillo", armorTexture),
                    enabled
            );

            LOADED_SCUTES.add(scute);
        } catch (Exception e) {
            System.err.println("Failed to load scute from " + path + ": " + e.getMessage());
        }
    }

    public static List<ArmadilloScuteType> getLoadedScutes() {
        return LOADED_SCUTES;
    }
}