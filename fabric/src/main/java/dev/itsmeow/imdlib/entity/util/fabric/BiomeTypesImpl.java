package dev.itsmeow.imdlib.entity.util.fabric;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.Predicate;

public class BiomeTypesImpl {

    private static final LazyLoadedValue<WritableRegistry<Biome>> REG = new LazyLoadedValue(() -> IMDLib.getStaticServerInstance().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));

    public static void init() {
        BiomeTypes.HOT = new BiomeTypes.Type(biome -> {
            Biome biomeIn = get(biome);
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature > 0.85F || category == Biome.BiomeCategory.DESERT;
        }, biomeContext -> {
            float temperature = biomeContext.getProperties().getClimateProperties().getTemperature();
            Biome.BiomeCategory category = biomeContext.getProperties().getCategory();
            return temperature > 0.85F || category == Biome.BiomeCategory.DESERT;
        });
        BiomeTypes.COLD = new BiomeTypes.Type(biome -> {
            Biome biomeIn = get(biome);
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature < 0.15F || category == Biome.BiomeCategory.ICY;
        }, biomeContext -> {
            float temperature = biomeContext.getProperties().getClimateProperties().getTemperature();
            Biome.BiomeCategory category = biomeContext.getProperties().getCategory();
            return temperature < 0.15F || category == Biome.BiomeCategory.ICY;
        });
        BiomeTypes.SPARSE = pathContains("sparse");
        BiomeTypes.DENSE = pathContains("dense");
        BiomeTypes.WET = new BiomeTypes.Type(biome -> get(biome).isHumid(), biomeContext -> biomeContext.getProperties().getClimateProperties().getDownfall() > 0.85F);
        BiomeTypes.DRY = catPath(Biome.BiomeCategory.DESERT, "arid");
        BiomeTypes.SAVANNA = catPath(Biome.BiomeCategory.SAVANNA, "savanna");
        BiomeTypes.CONIFEROUS = catPath(Biome.BiomeCategory.TAIGA, "taiga");
        BiomeTypes.JUNGLE = catPath(Biome.BiomeCategory.JUNGLE, "jungle");
        BiomeTypes.SPOOKY = pathContains("spooky");
        BiomeTypes.DEAD = pathContains("dead");
        BiomeTypes.LUSH = pathContains("lush");
        BiomeTypes.MUSHROOM = catMatch(Biome.BiomeCategory.MUSHROOM);
        BiomeTypes.MAGICAL = pathContains("magic");
        BiomeTypes.RARE = new BiomeTypes.Type(biome -> biome.location().getPath().contains("rare") || biome.location().getPath().contains("modified"), biomeContext -> biomeContext.getKey().getPath().contains("rare") || biomeContext.getKey().getPath().contains("modified"));
        BiomeTypes.PLATEAU = pathContains("plateau");
        BiomeTypes.MODIFIED = pathContains("modified");
        BiomeTypes.OCEAN = catMatch(Biome.BiomeCategory.OCEAN);
        BiomeTypes.RIVER = catMatch(Biome.BiomeCategory.RIVER);
        BiomeTypes.WATER = catPredicate(c -> c == Biome.BiomeCategory.OCEAN || c == Biome.BiomeCategory.RIVER);
        BiomeTypes.MESA = catMatch(Biome.BiomeCategory.MESA);
        BiomeTypes.FOREST = catPath(Biome.BiomeCategory.FOREST, "forest");
        BiomeTypes.PLAINS = catPath(Biome.BiomeCategory.PLAINS, "plain");
        BiomeTypes.MOUNTAIN = catPath(Biome.BiomeCategory.EXTREME_HILLS, "mountain");
        BiomeTypes.HILL = catPath(Biome.BiomeCategory.EXTREME_HILLS, "hill");
        BiomeTypes.SWAMP = catPath(Biome.BiomeCategory.SWAMP, "swamp");
        BiomeTypes.SANDY = catMatch(Biome.BiomeCategory.DESERT);
        BiomeTypes.SNOWY = catPath(Biome.BiomeCategory.ICY, "snow");
        BiomeTypes.WASTELAND = pathContains("waste");
        BiomeTypes.BEACH = catMatch(Biome.BiomeCategory.BEACH);
        BiomeTypes.VOID = catMatch(Biome.BiomeCategory.NONE);
        //TODO come up with a better check for overworld
        BiomeTypes.OVERWORLD = catPredicate(c -> c != Biome.BiomeCategory.NETHER && c != Biome.BiomeCategory.THEEND);
        BiomeTypes.NETHER = catPath(Biome.BiomeCategory.NETHER, "nether");
        BiomeTypes.END = catMatch(Biome.BiomeCategory.THEEND);

        // Hardcoded defaults
        addTypes(Biomes.OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.PLAINS, BiomeTypes.PLAINS, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DESERT, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SANDY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MOUNTAINS, BiomeTypes.MOUNTAIN, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.FOREST, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.TAIGA, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SWAMP, BiomeTypes.WET, BiomeTypes.SWAMP, BiomeTypes.OVERWORLD);
        addTypes(Biomes.RIVER, BiomeTypes.RIVER, BiomeTypes.OVERWORLD);
        addTypes(Biomes.NETHER_WASTES, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.NETHER);
        addTypes(Biomes.THE_END, BiomeTypes.COLD, BiomeTypes.DRY, BiomeTypes.END);
        addTypes(Biomes.FROZEN_OCEAN, BiomeTypes.COLD, BiomeTypes.OCEAN, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.FROZEN_RIVER, BiomeTypes.COLD, BiomeTypes.RIVER, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_TUNDRA, BiomeTypes.COLD, BiomeTypes.SNOWY, BiomeTypes.WASTELAND, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_MOUNTAINS, BiomeTypes.COLD, BiomeTypes.SNOWY, BiomeTypes.MOUNTAIN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MUSHROOM_FIELDS, BiomeTypes.MUSHROOM, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MUSHROOM_FIELD_SHORE, BiomeTypes.MUSHROOM, BiomeTypes.BEACH, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BEACH, BiomeTypes.BEACH, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DESERT_HILLS, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SANDY, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WOODED_HILLS, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.TAIGA_HILLS, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MOUNTAIN_EDGE, BiomeTypes.MOUNTAIN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.JUNGLE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.DENSE, BiomeTypes.JUNGLE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.JUNGLE_HILLS, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.DENSE, BiomeTypes.JUNGLE, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.JUNGLE_EDGE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.JUNGLE, BiomeTypes.FOREST, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.STONE_SHORE, BiomeTypes.BEACH, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_BEACH, BiomeTypes.COLD, BiomeTypes.BEACH, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BIRCH_FOREST, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BIRCH_FOREST_HILLS, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DARK_FOREST, BiomeTypes.SPOOKY, BiomeTypes.DENSE, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_TAIGA, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_TAIGA_HILLS, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.SNOWY, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.GIANT_TREE_TAIGA, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.GIANT_TREE_TAIGA_HILLS, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WOODED_MOUNTAINS, BiomeTypes.MOUNTAIN, BiomeTypes.FOREST, BiomeTypes.SPARSE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SAVANNA, BiomeTypes.HOT, BiomeTypes.SAVANNA, BiomeTypes.PLAINS, BiomeTypes.SPARSE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SAVANNA_PLATEAU, BiomeTypes.HOT, BiomeTypes.SAVANNA, BiomeTypes.PLAINS, BiomeTypes.SPARSE, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU);
        addTypes(Biomes.BADLANDS, BiomeTypes.MESA, BiomeTypes.SANDY, BiomeTypes.DRY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WOODED_BADLANDS_PLATEAU, BiomeTypes.MESA, BiomeTypes.SANDY, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU);
        addTypes(Biomes.BADLANDS_PLATEAU, BiomeTypes.MESA, BiomeTypes.SANDY, BiomeTypes.DRY, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU);
        addTypes(Biomes.SMALL_END_ISLANDS, BiomeTypes.END);
        addTypes(Biomes.END_MIDLANDS, BiomeTypes.END);
        addTypes(Biomes.END_HIGHLANDS, BiomeTypes.END);
        addTypes(Biomes.END_BARRENS, BiomeTypes.END);
        addTypes(Biomes.WARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.HOT, BiomeTypes.OVERWORLD);
        addTypes(Biomes.LUKEWARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.COLD_OCEAN, BiomeTypes.OCEAN, BiomeTypes.COLD, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_WARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.HOT, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_LUKEWARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_COLD_OCEAN, BiomeTypes.OCEAN, BiomeTypes.COLD, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_FROZEN_OCEAN, BiomeTypes.OCEAN, BiomeTypes.COLD, BiomeTypes.OVERWORLD);
        addTypes(Biomes.THE_VOID, BiomeTypes.VOID);
        addTypes(Biomes.SUNFLOWER_PLAINS, BiomeTypes.PLAINS, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DESERT_LAKES, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SANDY, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.GRAVELLY_MOUNTAINS, BiomeTypes.MOUNTAIN, BiomeTypes.SPARSE, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.FLOWER_FOREST, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.TAIGA_MOUNTAINS, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SWAMP_HILLS, BiomeTypes.WET, BiomeTypes.SWAMP, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.ICE_SPIKES, BiomeTypes.COLD, BiomeTypes.SNOWY, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MODIFIED_JUNGLE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.DENSE, BiomeTypes.JUNGLE, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.MODIFIED);
        addTypes(Biomes.MODIFIED_JUNGLE_EDGE, BiomeTypes.HOT, BiomeTypes.SPARSE, BiomeTypes.JUNGLE, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.MODIFIED);
        addTypes(Biomes.TALL_BIRCH_FOREST, BiomeTypes.FOREST, BiomeTypes.DENSE, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.TALL_BIRCH_HILLS, BiomeTypes.FOREST, BiomeTypes.DENSE, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DARK_FOREST_HILLS, BiomeTypes.SPOOKY, BiomeTypes.DENSE, BiomeTypes.FOREST, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_TAIGA_MOUNTAINS, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.SNOWY, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.GIANT_SPRUCE_TAIGA, BiomeTypes.DENSE, BiomeTypes.FOREST, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.GIANT_SPRUCE_TAIGA_HILLS, BiomeTypes.DENSE, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, BiomeTypes.MOUNTAIN, BiomeTypes.SPARSE, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.MODIFIED);
        addTypes(Biomes.SHATTERED_SAVANNA, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.SAVANNA, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SHATTERED_SAVANNA_PLATEAU, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.SAVANNA, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU);
        addTypes(Biomes.ERODED_BADLANDS, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU, BiomeTypes.MODIFIED);
        addTypes(Biomes.MODIFIED_BADLANDS_PLATEAU, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU, BiomeTypes.MODIFIED);
        addTypes(Biomes.BAMBOO_JUNGLE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.RARE, BiomeTypes.JUNGLE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BAMBOO_JUNGLE_HILLS, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.RARE, BiomeTypes.JUNGLE, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SOUL_SAND_VALLEY, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.NETHER);
        addTypes(Biomes.CRIMSON_FOREST, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.NETHER, BiomeTypes.FOREST);
        addTypes(Biomes.WARPED_FOREST, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.NETHER, BiomeTypes.FOREST);
        addTypes(Biomes.BASALT_DELTAS, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.NETHER);
    }

    private static void addTypes(ResourceKey<Biome> biome, BiomeTypes.Type... types) {
        for(BiomeTypes.Type type : types) {
            type.addDefaults(biome);
        }
    }

    private static Biome get(ResourceKey<Biome> biome) {
        return REG.get().get(biome.location());
    }

    private static Biome.BiomeCategory cat(ResourceKey<Biome> biome) {
        return REG.get().get(biome).getBiomeCategory();
    }

    private static BiomeTypes.Type catMatch(Biome.BiomeCategory category) {
        return new BiomeTypes.Type(biome -> cat(biome) == category, biomeContext -> biomeContext.getProperties().getCategory() == category);
    }

    private static BiomeTypes.Type catPredicate(Predicate<Biome.BiomeCategory> predicate) {
        return new BiomeTypes.Type(biome -> predicate.test(cat(biome)), biomeContext -> predicate.test(biomeContext.getProperties().getCategory()));
    }

    private static BiomeTypes.Type pathContains(String content) {
        return new BiomeTypes.Type(biome -> biome.location().getPath().contains(content), biomeContext -> biomeContext.getKey().getPath().contains(content));
    }

    private static BiomeTypes.Type catPath(Biome.BiomeCategory category, String content) {
        return new BiomeTypes.Type(biome -> cat(biome) == category || biome.location().getPath().contains(content), biomeContext -> biomeContext.getProperties().getCategory() == category || biomeContext.getKey().getPath().contains(content));
    }

    private static BiomeTypes.Type reject() {
        return new BiomeTypes.Type(biome -> false, biomeContext -> false);
    }
}
