package dev.itsmeow.imdlib.entity.util.fabric;

import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeTypesImpl {

    private static WritableRegistry<Biome> REG;

    public static void init() {
        REG = RegistryAccess.builtin().registryOrThrow(Registry.BIOME_REGISTRY);
        BiomeTypes.HOT = new BiomeTypes.Type(biome -> {
            Biome biomeIn = get(biome);
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature > 0.85F || category == Biome.BiomeCategory.DESERT;
        });
        BiomeTypes.COLD = new BiomeTypes.Type(biome -> {
            Biome biomeIn = get(biome);
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature < 0.15F || category == Biome.BiomeCategory.ICY;
        });
        BiomeTypes.SPARSE = new BiomeTypes.Type(biome -> false);
        BiomeTypes.DENSE = new BiomeTypes.Type(biome -> false);
        BiomeTypes.WET = new BiomeTypes.Type(biome -> get(biome).isHumid());
        BiomeTypes.DRY = catMatch(Biome.BiomeCategory.DESERT);
        BiomeTypes.SAVANNA = catMatch(Biome.BiomeCategory.SAVANNA);
        BiomeTypes.CONIFEROUS = catMatch(Biome.BiomeCategory.TAIGA);
        BiomeTypes.JUNGLE = catMatch(Biome.BiomeCategory.JUNGLE);
        BiomeTypes.SPOOKY = new BiomeTypes.Type(biome -> false);
        BiomeTypes.DEAD = new BiomeTypes.Type(biome -> false);
        BiomeTypes.LUSH = new BiomeTypes.Type(biome -> false);
        BiomeTypes.MUSHROOM = catMatch(Biome.BiomeCategory.MUSHROOM);
        BiomeTypes.MAGICAL = new BiomeTypes.Type(biome -> false);
        BiomeTypes.RARE = new BiomeTypes.Type(biome -> false);
        BiomeTypes.PLATEAU = new BiomeTypes.Type((biome) -> biome.location().getPath().contains("plateau"));
        BiomeTypes.MODIFIED = new BiomeTypes.Type((biome) -> biome.location().getPath().contains("modified"));
        BiomeTypes.OCEAN = catMatch(Biome.BiomeCategory.OCEAN);
        BiomeTypes.RIVER = catMatch(Biome.BiomeCategory.RIVER);
        BiomeTypes.WATER = new BiomeTypes.Type((biome) -> {
            Biome.BiomeCategory category = cat(biome);
            return category == Biome.BiomeCategory.OCEAN || category == Biome.BiomeCategory.RIVER;
        });
        BiomeTypes.MESA = catMatch(Biome.BiomeCategory.MESA);
        BiomeTypes.FOREST = catMatch(Biome.BiomeCategory.FOREST);
        BiomeTypes.PLAINS = catMatch(Biome.BiomeCategory.PLAINS);
        BiomeTypes.MOUNTAIN = catMatch(Biome.BiomeCategory.EXTREME_HILLS);
        BiomeTypes.HILL = catMatch(Biome.BiomeCategory.EXTREME_HILLS);
        BiomeTypes.SWAMP = catMatch(Biome.BiomeCategory.SWAMP);
        BiomeTypes.SANDY = catMatch(Biome.BiomeCategory.DESERT);
        BiomeTypes.SNOWY = catMatch(Biome.BiomeCategory.ICY);
        BiomeTypes.WASTELAND = new BiomeTypes.Type(biome -> false);
        BiomeTypes.BEACH = catMatch(Biome.BiomeCategory.BEACH);
        BiomeTypes.VOID = catMatch(Biome.BiomeCategory.NONE);
        //TODO come up with a better check for overworld
        BiomeTypes.OVERWORLD = new BiomeTypes.Type((biome) -> {
            Biome.BiomeCategory category = cat(biome);
            return category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND;
        });
        BiomeTypes.NETHER = catMatch(Biome.BiomeCategory.NETHER);
        BiomeTypes.END = catMatch(Biome.BiomeCategory.THEEND);
    }

    private static Biome get(ResourceKey<Biome> biome) {
        return REG.get(biome.location());
    }

    private static Biome.BiomeCategory cat(ResourceKey<Biome> biome) {
        return get(biome).getBiomeCategory();
    }

    private static BiomeTypes.Type catMatch(Biome.BiomeCategory category) {
        return new BiomeTypes.Type(biome -> cat(biome) == category);
    }
}
