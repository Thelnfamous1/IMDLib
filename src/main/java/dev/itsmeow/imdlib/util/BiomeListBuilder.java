package dev.itsmeow.imdlib.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class BiomeListBuilder {

    private final Set<RegistryKey<Biome>> extras = new HashSet<>();
    private final Set<BiomeDictionary.Type> list = new HashSet<>();
    private final Set<BiomeDictionary.Type> blacklist = new HashSet<>();
    private final Set<RegistryKey<Biome>> blacklistBiome = new HashSet<>();
    private final Set<BiomeDictionary.Type> required = new HashSet<>();

    private BiomeListBuilder() {

    }

    public static BiomeListBuilder create() {
        return new BiomeListBuilder();
    }

    public BiomeListBuilder extra(RegistryKey<Biome>... extraBiomes) {
        for(RegistryKey<Biome> biome : extraBiomes) {
            extras.add(biome);
        }
        return this;
    }

    public BiomeListBuilder extra(BiomeDictionary.Type... types) {
        for(BiomeDictionary.Type type : types) {
            list.add(type);
        }
        return this;
    }

    public BiomeListBuilder withoutTypes(BiomeDictionary.Type... types) {
        for(BiomeDictionary.Type type : types) {
            blacklist.add(type);
        }
        return this;
    }

    public BiomeListBuilder withTypes(BiomeDictionary.Type... types) {
        for(BiomeDictionary.Type type : types) {
            required.add(type);
        }
        return this;
    }

    public BiomeListBuilder withoutBiomes(RegistryKey<Biome>... biomes) {
        for(RegistryKey<Biome> biome : biomes) {
            blacklistBiome.add(biome);
        }
        return this;
    }

    public RegistryKey<Biome>[] collect() {
        Set<RegistryKey<Biome>> set = new HashSet<>();
        set.addAll(extras);
        for(BiomeDictionary.Type extraT : list) {
            set.addAll(BiomeDictionary.getBiomes(extraT));
        }
        if(required.size() > 0 || blacklist.size() > 0) {
            for(ResourceLocation biomeRL : ForgeRegistries.BIOMES.getKeys()) {
                RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Keys.BIOMES, biomeRL);
                Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biomeKey);
                if(types != null) {
                    boolean pass = true;
                    for(BiomeDictionary.Type type : required) {
                        if(!types.contains(type)) {
                            pass = false;
                        }
                    }
                    for(BiomeDictionary.Type type : blacklist) {
                        if(types.contains(type)) {
                            pass = false;
                        }
                    }
                    if(blacklistBiome.contains(biomeKey)) {
                        pass = false;
                    }
                    if(pass) {
                        set.add(biomeKey);
                    }
                }
            }
        }
        return set.toArray(new RegistryKey[0]);
    }

}
