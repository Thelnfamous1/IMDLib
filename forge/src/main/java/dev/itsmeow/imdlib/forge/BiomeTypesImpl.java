package dev.itsmeow.imdlib.forge;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeTypesImpl {

    public static void init() {
        BiomeTypes.HOT = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT));
        BiomeTypes.COLD = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD));
        BiomeTypes.SPARSE = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.SPARSE));
        BiomeTypes.DENSE = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.DENSE));
        BiomeTypes.WET = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET));
        BiomeTypes.DRY = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY));
        BiomeTypes.SAVANNA = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.SAVANNA));
        BiomeTypes.CONIFEROUS = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.CONIFEROUS));
        BiomeTypes.JUNGLE = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE));
        BiomeTypes.SPOOKY = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.SPOOKY));
        BiomeTypes.DEAD = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.DEAD));
        BiomeTypes.LUSH = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.LUSH));
        BiomeTypes.MUSHROOM = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM));
        BiomeTypes.MAGICAL = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL));
        BiomeTypes.RARE = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.RARE));
        BiomeTypes.PLATEAU = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLATEAU));
        BiomeTypes.MODIFIED = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.MODIFIED));
        BiomeTypes.OCEAN = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN));
        BiomeTypes.RIVER = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER));
        BiomeTypes.WATER = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER));
        BiomeTypes.MESA = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA));
        BiomeTypes.FOREST = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST));
        BiomeTypes.PLAINS = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS));
        BiomeTypes.MOUNTAIN = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.MOUNTAIN));
        BiomeTypes.HILL = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS));
        BiomeTypes.SWAMP = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP));
        BiomeTypes.SANDY = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY));
        BiomeTypes.SNOWY = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY));
        BiomeTypes.WASTELAND = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND));
        BiomeTypes.BEACH = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH));
        BiomeTypes.VOID = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.VOID));
        BiomeTypes.NETHER = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER));
        BiomeTypes.END = new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.END));

    }


}
