package dev.itsmeow.imdlib.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BiomeListBuilder {

    private final Set<ResourceKey<Biome>> extras = new HashSet<>();
    private final Set<BiomeDictionary.Type> list = new HashSet<>();
    private final Set<BiomeDictionary.Type> blacklist = new HashSet<>();
    private final Set<ResourceKey<Biome>> blacklistBiome = new HashSet<>();
    private final Set<BiomeDictionary.Type> required = new HashSet<>();
    private boolean onlyOverworld = false;

    private BiomeListBuilder() {

    }

    public static BiomeListBuilder create() {
        return new BiomeListBuilder();
    }

    public BiomeListBuilder extra(RegistryKey<Biome>... extraBiomes) {
        extras.addAll(Arrays.asList(extraBiomes));
        return this;
    }

    public BiomeListBuilder extra(BiomeDictionary.Type... types) {
        list.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeListBuilder withoutTypes(BiomeDictionary.Type... types) {
        blacklist.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeListBuilder withTypes(BiomeDictionary.Type... types) {
        required.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeListBuilder withoutBiomes(RegistryKey<Biome>... biomes) {
        blacklistBiome.addAll(Arrays.asList(biomes));
        return this;
    }

    public BiomeListBuilder onlyOverworld() {
        this.onlyOverworld = true;
        return this;
    }

    public ResourceKey<Biome>[] collect() {
        Set<ResourceKey<Biome>> set = new HashSet<>(extras);
        for (BiomeDictionary.Type extraT : list) {
            set.addAll(BiomeDictionary.getBiomes(extraT));
        }
        if (required.size() > 0 || blacklist.size() > 0) {
            for (ResourceLocation biomeRL : ForgeRegistries.BIOMES.getKeys()) {
                RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Keys.BIOMES, biomeRL);
                Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biomeKey);
                if (types.size() > 0) {
                    boolean pass = true;
                    for (BiomeDictionary.Type type : required) {
                        if (!types.contains(type)) {
                            pass = false;
                            break;
                        }
                    }
                    for (BiomeDictionary.Type type : blacklist) {
                        if (types.contains(type)) {
                            pass = false;
                            break;
                        }
                    }
                    if (blacklistBiome.contains(biomeKey)) {
                        pass = false;
                    }
                    if (onlyOverworld && !types.contains(BiomeDictionary.Type.OVERWORLD)) {
                        pass = false;
                    }
                    if (pass) {
                        set.add(biomeKey);
                    }
                }
            }
        }
        return set.toArray(new RegistryKey[0]);
    }

}
