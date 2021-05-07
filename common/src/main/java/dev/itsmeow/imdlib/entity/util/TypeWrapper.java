package dev.itsmeow.imdlib.entity.util;

import net.minecraft.world.level.biome.Biome;

public interface TypeWrapper {
    Biome.BiomeCategory getFabricType();

    //BiomeDictionary#Type super class we use :)
    Object getForgeType();
}
