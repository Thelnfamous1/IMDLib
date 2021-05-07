package dev.itsmeow.betteranimalsplus.fabric;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

public class BiomeTypesImpl {

    public static void init() {
        BiomeTypes.FOREST = new BiomeTypes.Type((biome) -> IMDLib.REGISTRIES.get().get(Registry.BIOME_REGISTRY).get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.FOREST);

    }
}
