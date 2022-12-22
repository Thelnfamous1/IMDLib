package dev.itsmeow.imdlib.entity.util.fabric;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeTypesImpl {

    public static void init() {
        BiomeTypes.HOT = get("is_hot", "climate_hot");
        BiomeTypes.COLD = get("is_cold", "climate_cold");
        BiomeTypes.SPARSE = get("is_sparse", "vegetation_sparse");
        BiomeTypes.DENSE = get("is_dense", "vegetation_dense");
        BiomeTypes.WET = get("is_wet", "climate_wet");
        BiomeTypes.DRY = get("is_dry", "climate_dry");
        BiomeTypes.SAVANNA = get("is_savanna", "savanna");
        BiomeTypes.CONIFEROUS = get("is_coniferous", "tree_coniferous");
        BiomeTypes.JUNGLE = getVanilla(BiomeTags.IS_JUNGLE, "tree_jungle");
        BiomeTypes.SPOOKY = get("is_spooky", "spooky");
        BiomeTypes.DEAD = get("is_dead", "dead");
        BiomeTypes.LUSH = get("is_lush", "lush");
        BiomeTypes.MUSHROOM = get("is_mushroom", "mushroom");
        BiomeTypes.MAGICAL = get("is_magical", "magical");
        BiomeTypes.RARE = get("is_rare", "rare");
        BiomeTypes.PLATEAU = get("is_plateau", "plateau");
        BiomeTypes.MODIFIED = get("is_modified", "modified");
        BiomeTypes.OCEAN = getVanilla(BiomeTags.IS_OCEAN, "ocean");
        BiomeTypes.RIVER = getVanilla(BiomeTags.IS_RIVER, "river");
        BiomeTypes.WATER = getMixTag("is_water", BiomeTags.IS_OCEAN, BiomeTags.IS_RIVER);
        BiomeTypes.BADLANDS = getVanilla(BiomeTags.IS_BADLANDS, "badlands");
        BiomeTypes.FOREST = getVanilla(BiomeTags.IS_FOREST, "forest");
        BiomeTypes.PLAINS = get("is_plains", "plains");
        BiomeTypes.HILL = getVanilla(BiomeTags.IS_HILL, "hill");
        BiomeTypes.SWAMP = get("is_swamp", "swamp");
        BiomeTypes.SANDY = get("is_sandy", "sandy");
        BiomeTypes.SNOWY = get("is_snowy", "snowy");
        BiomeTypes.WASTELAND = get("is_wasteland", "wasteland");
        BiomeTypes.BEACH = get("is_beach", "beach");
        BiomeTypes.VOID = get("is_void", "void");
        BiomeTypes.UNDERGROUND = get("is_underground", "underground");
        BiomeTypes.PEAK = get("is_peak", "mountain_peak");
        BiomeTypes.SLOPE = get("is_slope", "mountain_slope");
        BiomeTypes.MOUNTAIN = getVanilla(BiomeTags.IS_MOUNTAIN, "mountain");
        BiomeTypes.OVERWORLD = get("is_overworld", "in_overworld");
        BiomeTypes.NETHER = getVanilla(BiomeTags.IS_NETHER, "in_nether");
        BiomeTypes.END = get("is_end", "in_the_end");

        // Hardcoded defaults
        addTypes(Biomes.OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.PLAINS, BiomeTypes.PLAINS, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DESERT, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SANDY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WINDSWEPT_HILLS, BiomeTypes.MOUNTAIN, BiomeTypes.HILL, BiomeTypes.OVERWORLD);
        addTypes(Biomes.FOREST, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.TAIGA, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SWAMP, BiomeTypes.WET, BiomeTypes.SWAMP, BiomeTypes.OVERWORLD);
        addTypes(Biomes.RIVER, BiomeTypes.RIVER, BiomeTypes.OVERWORLD);
        addTypes(Biomes.NETHER_WASTES, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.NETHER);
        addTypes(Biomes.THE_END, BiomeTypes.COLD, BiomeTypes.DRY, BiomeTypes.END);
        addTypes(Biomes.FROZEN_OCEAN, BiomeTypes.COLD, BiomeTypes.OCEAN, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.FROZEN_RIVER, BiomeTypes.COLD, BiomeTypes.RIVER, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_PLAINS, BiomeTypes.COLD, BiomeTypes.SNOWY, BiomeTypes.WASTELAND, BiomeTypes.OVERWORLD);
        addTypes(Biomes.MUSHROOM_FIELDS, BiomeTypes.MUSHROOM, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BEACH, BiomeTypes.BEACH, BiomeTypes.OVERWORLD);
        addTypes(Biomes.JUNGLE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.DENSE, BiomeTypes.JUNGLE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SPARSE_JUNGLE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.JUNGLE, BiomeTypes.FOREST, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.STONY_SHORE, BiomeTypes.BEACH, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_BEACH, BiomeTypes.COLD, BiomeTypes.BEACH, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BIRCH_FOREST, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DARK_FOREST, BiomeTypes.SPOOKY, BiomeTypes.DENSE, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SNOWY_TAIGA, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.SNOWY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.OLD_GROWTH_PINE_TAIGA, BiomeTypes.COLD, BiomeTypes.CONIFEROUS, BiomeTypes.FOREST, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WINDSWEPT_FOREST, BiomeTypes.MOUNTAIN, BiomeTypes.FOREST, BiomeTypes.SPARSE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SAVANNA, BiomeTypes.HOT, BiomeTypes.SAVANNA, BiomeTypes.PLAINS, BiomeTypes.SPARSE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.SAVANNA_PLATEAU, BiomeTypes.HOT, BiomeTypes.SAVANNA, BiomeTypes.PLAINS, BiomeTypes.SPARSE, BiomeTypes.RARE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU);
        addTypes(Biomes.BADLANDS, BiomeTypes.BADLANDS, BiomeTypes.SANDY, BiomeTypes.DRY, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WOODED_BADLANDS, BiomeTypes.BADLANDS, BiomeTypes.SANDY, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.OVERWORLD, BiomeTypes.PLATEAU);
        addTypes(Biomes.SMALL_END_ISLANDS, BiomeTypes.END);
        addTypes(Biomes.END_MIDLANDS, BiomeTypes.END);
        addTypes(Biomes.END_HIGHLANDS, BiomeTypes.END);
        addTypes(Biomes.END_BARRENS, BiomeTypes.END);
        addTypes(Biomes.WARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.HOT, BiomeTypes.OVERWORLD);
        addTypes(Biomes.LUKEWARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.COLD_OCEAN, BiomeTypes.OCEAN, BiomeTypes.COLD, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_LUKEWARM_OCEAN, BiomeTypes.OCEAN, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_COLD_OCEAN, BiomeTypes.OCEAN, BiomeTypes.COLD, BiomeTypes.OVERWORLD);
        addTypes(Biomes.DEEP_FROZEN_OCEAN, BiomeTypes.OCEAN, BiomeTypes.COLD, BiomeTypes.OVERWORLD);
        addTypes(Biomes.THE_VOID, BiomeTypes.VOID);
        addTypes(Biomes.SUNFLOWER_PLAINS, BiomeTypes.PLAINS, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WINDSWEPT_GRAVELLY_HILLS, BiomeTypes.MOUNTAIN, BiomeTypes.SPARSE, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.FLOWER_FOREST, BiomeTypes.FOREST, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.ICE_SPIKES, BiomeTypes.COLD, BiomeTypes.SNOWY, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.OLD_GROWTH_BIRCH_FOREST, BiomeTypes.FOREST, BiomeTypes.DENSE, BiomeTypes.HILL, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.OLD_GROWTH_SPRUCE_TAIGA, BiomeTypes.DENSE, BiomeTypes.FOREST, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.WINDSWEPT_SAVANNA, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.SAVANNA, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.ERODED_BADLANDS, BiomeTypes.HOT, BiomeTypes.DRY, BiomeTypes.SPARSE, BiomeTypes.MOUNTAIN, BiomeTypes.RARE, BiomeTypes.OVERWORLD);
        addTypes(Biomes.BAMBOO_JUNGLE, BiomeTypes.HOT, BiomeTypes.WET, BiomeTypes.RARE, BiomeTypes.JUNGLE, BiomeTypes.OVERWORLD);
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

    private static TagKey<Biome> ckey(String key) {
        return key("c", key);
    }

    private static TagKey<Biome> fkey(String key) {
        return key("forge", key);
    }

    private static TagKey<Biome> key(String space, String key) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(space, key));
    }

    private static BiomeTypes.Type get(String forge, String tagKey) {
        return get(forge, ckey(tagKey));
    }

    private static BiomeTypes.Type get(String forge, TagKey<Biome> tagKey) {
        return new BiomeTypes.Type(biome -> hasAny(biome.location(), tagKey, fkey(forge), key(biome.location().getNamespace(), forge)), biomeContext -> hasAny(biomeContext.getKey().get(), tagKey, fkey(forge), key(biomeContext.getKey().get().getNamespace(), forge)));
    }

    private static BiomeTypes.Type getVanilla(TagKey<Biome> tagKey, String additional) {
        TagKey<Biome> c = ckey(additional);
        return new BiomeTypes.Type(biome -> hasAny(biome.location(), tagKey, c), biomeContext -> hasAny(biomeContext.getKey().get(), tagKey, c));
    }

    private static BiomeTypes.Type getMixTag(String forge, TagKey<Biome> tagKey, TagKey<Biome> additional) {
        return new BiomeTypes.Type(biome -> hasAny(biome.location(), tagKey, additional, fkey(forge), key(biome.location().getNamespace(), forge)), biomeContext -> hasAny(biomeContext.getKey().get(), tagKey, additional, fkey(forge), key(biomeContext.getKey().get().getNamespace(), forge)));
    }

    private static boolean hasAny(ResourceLocation biome, TagKey<Biome>... keys) {
        Holder<Biome> holder = BiomeTypes.REG.get().getHolderOrThrow(ResourceKey.create(Registries.BIOME, biome));
        for(TagKey<Biome> k : keys) {
            if(holder.is(k)) {
                return true;
            }
        }
        return false;
    }
}
