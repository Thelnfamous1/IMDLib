package dev.itsmeow.imdlib.entity.util.forge;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;

public class BiomeTypesImpl {

    public static void init() {
        BiomeTypes.HOT = get(Tags.Biomes.IS_HOT);
        BiomeTypes.COLD = get(Tags.Biomes.IS_COLD);
        BiomeTypes.SPARSE = get(Tags.Biomes.IS_SPARSE);
        BiomeTypes.DENSE = get(Tags.Biomes.IS_DENSE);
        BiomeTypes.WET = get(Tags.Biomes.IS_WET);
        BiomeTypes.DRY = get(Tags.Biomes.IS_DRY);
        BiomeTypes.SAVANNA = get(BiomeTags.IS_SAVANNA);
        BiomeTypes.CONIFEROUS = get(Tags.Biomes.IS_CONIFEROUS);
        BiomeTypes.JUNGLE = get(BiomeTags.IS_JUNGLE);
        BiomeTypes.SPOOKY = get(Tags.Biomes.IS_SPOOKY);
        BiomeTypes.DEAD = get(Tags.Biomes.IS_DEAD);
        BiomeTypes.LUSH = get(Tags.Biomes.IS_LUSH);
        BiomeTypes.MUSHROOM = get(Tags.Biomes.IS_MUSHROOM);
        BiomeTypes.MAGICAL = get(Tags.Biomes.IS_MAGICAL);
        BiomeTypes.RARE = get(Tags.Biomes.IS_RARE);
        BiomeTypes.PLATEAU = get(Tags.Biomes.IS_PLATEAU);
        BiomeTypes.MODIFIED = get(Tags.Biomes.IS_MODIFIED);
        BiomeTypes.OCEAN = get(BiomeTags.IS_OCEAN);
        BiomeTypes.RIVER = get(BiomeTags.IS_RIVER);
        BiomeTypes.WATER = get(Tags.Biomes.IS_WATER);
        BiomeTypes.BADLANDS = get(BiomeTags.IS_BADLANDS);
        BiomeTypes.FOREST = get(BiomeTags.IS_FOREST);
        BiomeTypes.PLAINS = get(Tags.Biomes.IS_PLAINS);
        BiomeTypes.HILL = get(BiomeTags.IS_HILL);
        BiomeTypes.SWAMP = get(Tags.Biomes.IS_SWAMP);
        BiomeTypes.SANDY = get(Tags.Biomes.IS_SANDY);
        BiomeTypes.SNOWY = get(Tags.Biomes.IS_SNOWY);
        BiomeTypes.WASTELAND = get(Tags.Biomes.IS_WASTELAND);
        BiomeTypes.BEACH = get(BiomeTags.IS_BEACH);
        BiomeTypes.VOID = get(Tags.Biomes.IS_VOID);
        BiomeTypes.UNDERGROUND = get(Tags.Biomes.IS_UNDERGROUND);
        BiomeTypes.PEAK = get(Tags.Biomes.IS_PEAK);
        BiomeTypes.SLOPE = get(Tags.Biomes.IS_SLOPE);
        BiomeTypes.MOUNTAIN = get(BiomeTags.IS_MOUNTAIN);
        BiomeTypes.OVERWORLD = get(BiomeTags.IS_OVERWORLD);
        BiomeTypes.NETHER = get(BiomeTags.IS_NETHER);
        BiomeTypes.END = get(BiomeTags.IS_END);
    }

    private static BiomeTypes.Type get(TagKey<Biome> tagKey) {
        return new BiomeTypes.Type(biome -> BiomeTypes.REG.get().getHolderOrThrow(ResourceKey.create(Registries.BIOME, biome.location())).is(tagKey), biomeContext -> BiomeTypes.REG.get().getHolderOrThrow(ResourceKey.create(Registries.BIOME, biomeContext.getKey().get())).is(tagKey));
    }

}
