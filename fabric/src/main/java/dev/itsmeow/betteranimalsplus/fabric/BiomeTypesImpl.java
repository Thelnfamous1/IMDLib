package dev.itsmeow.betteranimalsplus.fabric;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

public class BiomeTypesImpl {

    public static void init() {
        //otherwise it was getting the vanilla registry but .get gets the architectury registry
        me.shedaniel.architectury.registry.Registry<Biome> reg = IMDLib.REGISTRIES.get().get(Registry.BIOME_REGISTRY);

        BiomeTypes.HOT = new BiomeTypes.Type(biome -> {
            Biome biomeIn = reg.get(biome.location());
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature > 0.75 || category == Biome.BiomeCategory.DESERT;
        });
        BiomeTypes.COLD = new BiomeTypes.Type(biome -> {
            Biome biomeIn = reg.get(biome.location());
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature < 0.1F || category == Biome.BiomeCategory.ICY;
        });
        BiomeTypes.SPARSE = new BiomeTypes.Type(biome -> false);
        BiomeTypes.DENSE = new BiomeTypes.Type(biome -> false);
        BiomeTypes.WET = new BiomeTypes.Type(biome -> reg.get(biome.location()).isHumid());
        BiomeTypes.DRY = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.DESERT);
        BiomeTypes.SAVANNA = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.SAVANNA);
        BiomeTypes.CONIFEROUS = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.TAIGA);
        BiomeTypes.JUNGLE = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.JUNGLE);
        BiomeTypes.SPOOKY = new BiomeTypes.Type(biome -> false);
        BiomeTypes.DEAD = new BiomeTypes.Type(biome -> false);
        BiomeTypes.LUSH = new BiomeTypes.Type(biome -> false);
        BiomeTypes.MUSHROOM = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.MUSHROOM);
        BiomeTypes.MAGICAL = new BiomeTypes.Type(biome -> false);
        BiomeTypes.RARE = new BiomeTypes.Type(biome -> false);
        BiomeTypes.PLATEAU = new BiomeTypes.Type((biome) -> biome.location().getPath().contains("plateau"));
        BiomeTypes.MODIFIED = new BiomeTypes.Type((biome) -> biome.location().getPath().contains("modified"));
        BiomeTypes.OCEAN = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.OCEAN);
        BiomeTypes.RIVER = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.RIVER);
        BiomeTypes.WATER = new BiomeTypes.Type((biome) -> {
            Biome.BiomeCategory category = reg.get(biome.location()).getBiomeCategory();
            return category == Biome.BiomeCategory.OCEAN || category == Biome.BiomeCategory.RIVER;
        });
        BiomeTypes.MESA = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.MESA);
        BiomeTypes.FOREST = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.FOREST);
        BiomeTypes.PLAINS = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.PLAINS);
        BiomeTypes.MOUNTAIN = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.EXTREME_HILLS);
        BiomeTypes.HILL = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.EXTREME_HILLS);
        BiomeTypes.SWAMP = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.SWAMP);
        BiomeTypes.SANDY = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.DESERT);
        BiomeTypes.SNOWY = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.ICY);
        BiomeTypes.WASTELAND = new BiomeTypes.Type(biome -> false);
        BiomeTypes.BEACH = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.BEACH);
        BiomeTypes.VOID = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.NONE);
        BiomeTypes.NETHER = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.NETHER);
        BiomeTypes.END = new BiomeTypes.Type((biome) -> reg.get(biome.location()).getBiomeCategory() == Biome.BiomeCategory.THEEND);
    }
}
