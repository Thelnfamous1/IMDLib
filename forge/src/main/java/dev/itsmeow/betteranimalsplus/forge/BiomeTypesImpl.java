package dev.itsmeow.betteranimalsplus.forge;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeTypesImpl {

    public static void init() {
        BiomeTypes.FOREST = new BiomeTypes.Type((biome) -> BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST));

    }
}
