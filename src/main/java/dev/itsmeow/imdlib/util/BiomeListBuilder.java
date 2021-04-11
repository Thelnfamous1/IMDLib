package dev.itsmeow.imdlib.util;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BiomeListBuilder {

    private final Set<Biome> extras = new HashSet<>();
    private final Set<BiomeDictionary.Type> list = new HashSet<>();
    private final Set<BiomeDictionary.Type> blacklist = new HashSet<>();
    private final Set<Biome> blacklistBiome = new HashSet<>();
    private final Set<BiomeDictionary.Type> required = new HashSet<>();

    private BiomeListBuilder() {

    }

    public static BiomeListBuilder create() {
        return new BiomeListBuilder();
    }

    public BiomeListBuilder extra(Biome... extraBiomes) {
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

    public BiomeListBuilder withoutBiomes(Biome... biomes) {
        blacklistBiome.addAll(Arrays.asList(biomes));
        return this;
    }

    public Biome[] collect() {
        Set<Biome> set = new HashSet<>(extras);
        for(BiomeDictionary.Type extraT : list) {
            set.addAll(BiomeDictionary.getBiomes(extraT));
        }
        if(required.size() > 0 || blacklist.size() > 0) {
            for(Biome biome : ForgeRegistries.BIOMES.getValues()) {
                Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
                if(types.size() > 0) {
                    boolean pass = true;
                    for(BiomeDictionary.Type type : required) {
                        if (!types.contains(type)) {
                            pass = false;
                            break;
                        }
                    }
                    for(BiomeDictionary.Type type : blacklist) {
                        if(types.contains(type)) {
                            pass = false;
                            break;
                        }
                    }
                    if(blacklistBiome.contains(biome)) {
                        pass = false;
                    }
                    if(pass) {
                        set.add(biome);
                    }
                }
            }
        }
        return set.toArray(new Biome[0]);
    }

}
