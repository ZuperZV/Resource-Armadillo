package net.zuperz.resource_armadillo.item;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.zuperz.resource_armadillo.ResourceArmadillo;
import net.zuperz.resource_armadillo.item.custom.ArmadilloScuteItem;
import net.zuperz.resource_armadillo.util.ArmadilloScuteRegistry;
import net.zuperz.resource_armadillo.util.ArmadilloScuteType;
import net.zuperz.resource_armadillo.util.ScuteJsonLoader;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedCollection;

public class ModArmadilloScutes {
    private static final boolean DEBUG = !FMLEnvironment.production;

    public static final List<ArmadilloScuteType> SCUTE_TYPES = new ArrayList<>();
    public static final List<ArmadilloScuteType> MOD_SCUTE_TYPES = new ArrayList<>();

    public static final ArmadilloScuteType NONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "none"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/white_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/wolf_armor.png"), true));

    public static final ArmadilloScuteType DIRT = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "dirt"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/dirt_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/dirt_armor.png"), true));;
    public static final ArmadilloScuteType COBBLESTONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "cobblestone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/cobblestone_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/cobblestone_armor.png"), true));
    public static final ArmadilloScuteType FLINT = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "flint"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/flint_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/flint_armor.png"), true));
    public static final ArmadilloScuteType STONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "stone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/stone_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/stone_armor.png"), true));
    public static final ArmadilloScuteType DEEPSLATE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "deepslate"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/deepslate_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/deepslate_armor.png"), true));
    public static final ArmadilloScuteType SAND = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "sand"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/sand_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/sand_armor.png"), true));
    public static final ArmadilloScuteType CLAY = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "clay"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/clay_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/clay_armor.png"), true));
    public static final ArmadilloScuteType HONEY = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "honey"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/honey_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/honey_armor.png"), true));
    public static final ArmadilloScuteType AMETHYST = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "amethyst"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/amethyst_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/amethyst_armor.png"), true));
    public static final ArmadilloScuteType SLIME = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "slime"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/slime_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/slime_armor.png"), true));
    public static final ArmadilloScuteType DYE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "dye"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/dye_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/dye_armor.png"), true));

    public static final ArmadilloScuteType NETHERRACK = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "netherrack"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/netherrack_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/netherrack_armor.png"), true));

    public static final ArmadilloScuteType COAL = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "coal"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/coal_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/coal_armor.png"), true));
    public static final ArmadilloScuteType IRON = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "iron"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/iron_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/iron_armor.png"), true));
    public static final ArmadilloScuteType DIAMOND = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "diamond"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/diamond_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/diamond_armor.png"), true));
    public static final ArmadilloScuteType QUARTZ = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "quartz"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/quartz_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/quartz_armor.png"), true));
    public static final ArmadilloScuteType CHROMIUM = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "chromium"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/chromium_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/chromium_armor.png"), true));
    public static final ArmadilloScuteType EMERALD = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "emerald"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/emerald_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/emerald_armor.png"), true));
    public static final ArmadilloScuteType GOLD = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "gold"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/gold_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/gold_armor.png"), true));
    public static final ArmadilloScuteType LAPIS = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "lapis"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/lapis_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/lapis_armor.png"), true));
    public static final ArmadilloScuteType REDSTONE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "redstone"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/redstone_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/redstone_armor.png"), true));
    public static final ArmadilloScuteType NETHERITE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "netherite"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/netherite_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/netherite_armor.png"), true));
    public static final ArmadilloScuteType COPPER = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "copper"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/copper_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/copper_armor.png"), true));

    public static final ArmadilloScuteType MAGMA = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "magma"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/magma_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/magma_armor.png"), true));
    public static final ArmadilloScuteType OBSIDIAN = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "obsidian"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/obsidian_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/obsidian_armor.png"), true));
    public static final ArmadilloScuteType PRISMARINE = register(new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "prismarine"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/prismarine_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/prismarine_armor.png"), true));

    public static final ArmadilloScuteType ENERGIZED_STEEL = registerWithRequiredMods("powah", new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "steel_energized"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/energized_steel_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/steel_energized_armor.png"), true));
    public static final ArmadilloScuteType CRYSTAL_NIOTIC = registerWithRequiredMods("powah", new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "crystal_niotic"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/crystal_niotic_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/crystal_niotic_armor.png"), true));
    public static final ArmadilloScuteType CRYSTAL_BLAZING = registerWithRequiredMods("powah", new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "crystal_blazing"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/crystal_blazing_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/crystal_blazing_armor.png"), true));
    public static final ArmadilloScuteType CRYSTAL_SPIRIED = registerWithRequiredMods("powah", new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "crystal_spirited"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/crystal_spirited_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/crystal_spirited_armor.png"), true));
    public static final ArmadilloScuteType CRYSTAL_NITRO = registerWithRequiredMods("powah", new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "crystal_nitro"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/crystal_nitro_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/crystal_nitro_armor.png"), true));
    public static final ArmadilloScuteType URANINITE = registerWithRequiredMods("powah", new ArmadilloScuteType(ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "uraninite"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/uraninite_armadillo.png"), ResourceLocation.fromNamespaceAndPath(ResourceArmadillo.MOD_ID, "textures/entity/wolf/uraninite_armor.png"), true));

    private static ArmadilloScuteType register(ArmadilloScuteType type) {

        type.setEssenceItem(() -> new ArmadilloScuteItem(type), true);
        SCUTE_TYPES.add(type);
        return type;
    }

    public static ArmadilloScuteType registerWithRequiredMods(String modId, ArmadilloScuteType type) {
        boolean modLoaded = ModList.get().isLoaded(modId);
        SCUTE_TYPES.add(type);
        MOD_SCUTE_TYPES.add(type);

        type.setEssenceItem(() -> new ArmadilloScuteItem(type), true);

        if (type.isEnabled()) {
            type.setEnabled(modLoaded);
        }

        return type;
    }

    public static void registerAll(ArmadilloScuteRegistry registry) {
        ArmadilloScuteRegistry.setAllowRegistration(true);

        SCUTE_TYPES.forEach(registry::register);

        ScuteJsonLoader.loadScutesFromJson();

        ScuteJsonLoader.getLoadedScutes().forEach(scute -> {
            registry.register(scute);
            SCUTE_TYPES.add(scute);
        });
    }

    public static SequencedCollection<ArmadilloScuteType> getModSucte() {
        return MOD_SCUTE_TYPES;
    }
}
