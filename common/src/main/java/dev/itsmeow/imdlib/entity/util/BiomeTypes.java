package dev.itsmeow.imdlib.entity.util;


import com.google.common.collect.Lists;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.itsmeow.imdlib.IMDLib;
import me.shedaniel.architectury.registry.BiomeModifications;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BiomeTypes {

    public static Type HOT;
    public static Type COLD;

    //Tags specifying the amount of vegetation a biome has. Specifying neither implies a biome to have moderate amounts*/
    public static Type SPARSE;
    public static Type DENSE;

    //Tags specifying how moist a biome is. Specifying neither implies the biome as having moderate humidity*/
    public static Type WET;
    public static Type DRY;

    /*Tree-based tags, SAVANNA refers to dry, desert-like trees (Such as Acacia), CONIFEROUS refers to snowy trees (Such as Spruce) and JUNGLE refers to jungle trees.
     * Specifying no tag implies a biome has temperate trees (Such as Oak)*/
    public static Type SAVANNA;
    public static Type CONIFEROUS;
    public static Type JUNGLE;

    /*Tags specifying the nature of a biome*/
    public static Type SPOOKY;
    public static Type DEAD;
    public static Type LUSH;
    public static Type MUSHROOM;
    public static Type MAGICAL;
    public static Type RARE;
    public static Type PLATEAU;
    public static Type MODIFIED;

    public static Type OCEAN;
    public static Type RIVER;
    /**
     * A general tag for all water-based biomes. Shown as present if OCEAN or RIVER are.
     **/
    public static Type WATER;

    /*Generic types which a biome can be*/
    public static Type MESA;
    public static Type FOREST;
    public static Type PLAINS;
    public static Type MOUNTAIN;
    public static Type HILL;
    public static Type SWAMP;
    public static Type SANDY;
    public static Type SNOWY;
    public static Type WASTELAND;
    public static Type BEACH;
    public static Type VOID;

    /*Tags specifying the dimension a biome generates in. Specifying none implies a biome that generates in a modded dimension*/
    public static Type OVERWORLD;
    public static Type NETHER;
    public static Type END;
    //private static Map<Type,  Set<ResourceKey<Biome>>> computedBiomes = new HashMap<>();

    static {
        init();
    }

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }

    public static Set<Type> getTypes(BiomeModifications.BiomeContext ctx) {
        return Type.TYPES.stream().filter(t -> t.hasType(ctx)).collect(Collectors.toSet());
    }

    public static Set<Type> getTypes(ResourceKey<Biome> biome) {
        return Type.TYPES.stream().filter(t -> t.hasType(biome)).collect(Collectors.toSet());
    }

    public static Set<ResourceKey<Biome>> getBiomes(Type type) {
        /*if(computedBiomes.containsKey(type)) {
            return computedBiomes.get(type);
        }*/
        WritableRegistry<Biome> reg = null;
        try {
            reg = IMDLib.getStaticServerInstance().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        } catch(RuntimeException e) {
            return new HashSet<>();
        }
        Set<ResourceKey<Biome>> res = reg.stream().map(reg::getResourceKey).filter(Optional::isPresent).map(Optional::get).filter(type::hasType).collect(Collectors.toSet());
        //computedBiomes.put(type, res);
        return res;
    }

    public static class Type {
        protected static Set<Type> TYPES = new HashSet<>();
        private final Predicate<ResourceKey<Biome>> validator;
        private final Predicate<BiomeModifications.BiomeContext> ctxValidator;
        private final Map<ResourceKey<Biome>, Boolean> validations = new HashMap<>();
        private final Set<ResourceKey<Biome>> DEFAULTS = new HashSet<>();

        public Type(Predicate<ResourceKey<Biome>> validator, Predicate<BiomeModifications.BiomeContext> ctxValidator) {
            this.validator = validator;
            this.ctxValidator = ctxValidator;
            TYPES.add(this);
        }

        public Type addDefaults(ResourceKey<Biome>... biomes) {
            DEFAULTS.addAll(Lists.newArrayList(biomes));
            return this;
        }

        public boolean hasType(BiomeModifications.BiomeContext ctx) {
            ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, ctx.getKey());
            if(DEFAULTS.contains(key)) {
                return true;
            }
            if(validations.containsKey(key)) {
                return validations.get(key);
            }
            boolean res = ctxValidator.test(ctx);
            validations.put(key, res);
            return res;
        }

        public boolean hasType(ResourceKey<Biome> biome) {
            if(DEFAULTS.contains(biome)) {
                return true;
            }
            if(validations.containsKey(biome)) {
                return validations.get(biome);
            }
            boolean res = validator.test(biome);
            validations.put(biome, res);
            return res;
        }
    }
}