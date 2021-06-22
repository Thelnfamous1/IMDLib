package dev.itsmeow.imdlib.entity.util;


import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

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

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }

    public static class Type {
        private final Predicate<ResourceKey<Biome>> validator;

        public Type(Predicate<ResourceKey<Biome>> validator) {
            this.validator = validator;
        }

        public boolean hasType(ResourceKey<Biome> biome) {
            return validator.test(biome);
        }
    }
}