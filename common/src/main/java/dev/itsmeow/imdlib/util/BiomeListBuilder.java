package dev.itsmeow.imdlib.util;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BiomeListBuilder {

    private final Set<ResourceKey<Biome>> extras = new HashSet<>();
    private final Set<BiomeTypes.Type> list = new HashSet<>();
    private final Set<BiomeTypes.Type> blacklist = new HashSet<>();
    private final Set<ResourceKey<Biome>> blacklistBiome = new HashSet<>();
    private final Set<BiomeTypes.Type> required = new HashSet<>();
    private boolean onlyOverworld = false;

    private BiomeListBuilder() {
    }

    public static BiomeListBuilder create() {
        return new BiomeListBuilder();
    }

    @SafeVarargs
    public final BiomeListBuilder extra(ResourceKey<Biome>... extraBiomes) {
        extras.addAll(Arrays.asList(extraBiomes));
        return this;
    }

    public BiomeListBuilder extra(BiomeTypes.Type... types) {
        list.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeListBuilder withoutTypes(BiomeTypes.Type... types) {
        blacklist.addAll(Arrays.asList(types));
        return this;
    }

    public BiomeListBuilder withTypes(BiomeTypes.Type... types) {
        required.addAll(Arrays.asList(types));
        return this;
    }

    @SafeVarargs
    public final BiomeListBuilder withoutBiomes(ResourceKey<Biome>... biomes) {
        blacklistBiome.addAll(Arrays.asList(biomes));
        return this;
    }

    public BiomeListBuilder onlyOverworld() {
        this.onlyOverworld = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public ResourceKey<Biome>[] collect() {
        Set<ResourceKey<Biome>> set = new HashSet<>(extras);
        for (BiomeTypes.Type extraT : list) {
            set.addAll(BiomeTypes.getBiomes(extraT));
        }
        if (required.size() > 0 || blacklist.size() > 0) {
            WritableRegistry<Biome> registry = IMDLib.getStaticServerInstance().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            for (Biome biome : registry) {
                Optional<ResourceKey<Biome>> biomeKeyOpt = registry.getResourceKey(biome);
                biomeKeyOpt.ifPresent(biomeKey -> {
                    Set<BiomeTypes.Type> types = BiomeTypes.getTypes(biomeKey);
                    if (types.size() > 0) {
                        boolean pass = true;
                        for (BiomeTypes.Type type : required) {
                            if (!types.contains(type)) {
                                pass = false;
                                break;
                            }
                        }
                        for (BiomeTypes.Type type : blacklist) {
                            if (types.contains(type)) {
                                pass = false;
                                break;
                            }
                        }
                        if (blacklistBiome.contains(biomeKey)) {
                            pass = false;
                        }
                        if (onlyOverworld && !types.contains(BiomeTypes.OVERWORLD)) {
                            pass = false;
                        }
                        if (pass) {
                            set.add(biomeKey);
                        }
                    }
                });
            }
        }
        return set.toArray(new ResourceKey[0]);
    }

}
