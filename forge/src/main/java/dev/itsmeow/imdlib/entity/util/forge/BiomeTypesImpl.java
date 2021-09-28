package dev.itsmeow.imdlib.entity.util.forge;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeTypesImpl {

    public static void init() {
        BiomeTypes.HOT = get(BiomeDictionary.Type.HOT);
        BiomeTypes.COLD = get(BiomeDictionary.Type.COLD);
        BiomeTypes.SPARSE = get(BiomeDictionary.Type.SPARSE);
        BiomeTypes.DENSE = get(BiomeDictionary.Type.DENSE);
        BiomeTypes.WET = get(BiomeDictionary.Type.WET);
        BiomeTypes.DRY = get(BiomeDictionary.Type.DRY);
        BiomeTypes.SAVANNA = get(BiomeDictionary.Type.SAVANNA);
        BiomeTypes.CONIFEROUS = get(BiomeDictionary.Type.CONIFEROUS);
        BiomeTypes.JUNGLE = get(BiomeDictionary.Type.JUNGLE);
        BiomeTypes.SPOOKY = get(BiomeDictionary.Type.SPOOKY);
        BiomeTypes.DEAD = get(BiomeDictionary.Type.DEAD);
        BiomeTypes.LUSH = get(BiomeDictionary.Type.LUSH);
        BiomeTypes.MUSHROOM = get(BiomeDictionary.Type.MUSHROOM);
        BiomeTypes.MAGICAL = get(BiomeDictionary.Type.MAGICAL);
        BiomeTypes.RARE = get(BiomeDictionary.Type.RARE);
        BiomeTypes.PLATEAU = get(BiomeDictionary.Type.PLATEAU);
        BiomeTypes.MODIFIED = get(BiomeDictionary.Type.MODIFIED);
        BiomeTypes.OCEAN = get(BiomeDictionary.Type.OCEAN);
        BiomeTypes.RIVER = get(BiomeDictionary.Type.RIVER);
        BiomeTypes.WATER = get(BiomeDictionary.Type.WATER);
        BiomeTypes.MESA = get(BiomeDictionary.Type.MESA);
        BiomeTypes.FOREST = get(BiomeDictionary.Type.FOREST);
        BiomeTypes.PLAINS = get(BiomeDictionary.Type.PLAINS);
        BiomeTypes.MOUNTAIN = get(BiomeDictionary.Type.MOUNTAIN);
        BiomeTypes.HILL = get(BiomeDictionary.Type.HILLS);
        BiomeTypes.SWAMP = get(BiomeDictionary.Type.SWAMP);
        BiomeTypes.SANDY = get(BiomeDictionary.Type.SANDY);
        BiomeTypes.SNOWY = get(BiomeDictionary.Type.SNOWY);
        BiomeTypes.WASTELAND = get(BiomeDictionary.Type.WASTELAND);
        BiomeTypes.BEACH = get(BiomeDictionary.Type.BEACH);
        BiomeTypes.VOID = get(BiomeDictionary.Type.VOID);
        BiomeTypes.NETHER = get(BiomeDictionary.Type.NETHER);
        BiomeTypes.END = get(BiomeDictionary.Type.END);
    }

    private static BiomeTypes.Type get(BiomeDictionary.Type type) {
        return new BiomeTypes.Type(biome -> BiomeDictionary.hasType(biome, type));
    }

}
