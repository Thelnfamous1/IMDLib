package dev.itsmeow.imdlib.entity.util.fabric;

import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

public class BiomeTypesImpl {

    private static final LazyLoadedValue<WritableRegistry<Biome>> REG = new LazyLoadedValue(() -> IMDLib.getStaticServerInstance().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));

    public static void init() {
        BiomeTypes.HOT = new BiomeTypes.Type(biome -> {
            Biome biomeIn = get(biome);
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature > 0.85F || category == Biome.BiomeCategory.DESERT;
        }, biomeContext -> {
            float temperature = biomeContext.getProperties().getClimateProperties().getTemperature();
            Biome.BiomeCategory category = biomeContext.getProperties().getCategory();
            return temperature > 0.85F || category == Biome.BiomeCategory.DESERT;
        });
        BiomeTypes.COLD = new BiomeTypes.Type(biome -> {
            Biome biomeIn = get(biome);
            float temperature = biomeIn.getBaseTemperature();
            Biome.BiomeCategory category = biomeIn.getBiomeCategory();
            return temperature < 0.15F || category == Biome.BiomeCategory.ICY;
        }, biomeContext -> {
            float temperature = biomeContext.getProperties().getClimateProperties().getTemperature();
            Biome.BiomeCategory category = biomeContext.getProperties().getCategory();
            return temperature < 0.15F || category == Biome.BiomeCategory.ICY;
        });
        BiomeTypes.SPARSE = reject();
        BiomeTypes.DENSE = reject();
        BiomeTypes.WET = new BiomeTypes.Type(biome -> get(biome).isHumid(), biomeContext -> biomeContext.getProperties().getClimateProperties().getDownfall() > 0.85F);
        BiomeTypes.DRY = catMatch(Biome.BiomeCategory.DESERT);
        BiomeTypes.SAVANNA = catMatch(Biome.BiomeCategory.SAVANNA);
        BiomeTypes.CONIFEROUS = catMatch(Biome.BiomeCategory.TAIGA);
        BiomeTypes.JUNGLE = catMatch(Biome.BiomeCategory.JUNGLE);
        BiomeTypes.SPOOKY = reject();
        BiomeTypes.DEAD = reject();
        BiomeTypes.LUSH = reject();
        BiomeTypes.MUSHROOM = catMatch(Biome.BiomeCategory.MUSHROOM);
        BiomeTypes.MAGICAL = reject();
        BiomeTypes.RARE = reject();
        BiomeTypes.PLATEAU = pathContains("plateau");
        BiomeTypes.MODIFIED = pathContains("modified");
        BiomeTypes.OCEAN = catMatch(Biome.BiomeCategory.OCEAN);
        BiomeTypes.RIVER = catMatch(Biome.BiomeCategory.RIVER);
        BiomeTypes.WATER = catPredicate(c -> c == Biome.BiomeCategory.OCEAN || c == Biome.BiomeCategory.RIVER);
        BiomeTypes.MESA = catMatch(Biome.BiomeCategory.MESA);
        BiomeTypes.FOREST = catMatch(Biome.BiomeCategory.FOREST);
        BiomeTypes.PLAINS = catMatch(Biome.BiomeCategory.PLAINS);
        BiomeTypes.MOUNTAIN = catMatch(Biome.BiomeCategory.EXTREME_HILLS);
        BiomeTypes.HILL = catMatch(Biome.BiomeCategory.EXTREME_HILLS);
        BiomeTypes.SWAMP = catMatch(Biome.BiomeCategory.SWAMP);
        BiomeTypes.SANDY = catMatch(Biome.BiomeCategory.DESERT);
        BiomeTypes.SNOWY = catMatch(Biome.BiomeCategory.ICY);
        BiomeTypes.WASTELAND = reject();
        BiomeTypes.BEACH = catMatch(Biome.BiomeCategory.BEACH);
        BiomeTypes.VOID = catMatch(Biome.BiomeCategory.NONE);
        //TODO come up with a better check for overworld
        BiomeTypes.OVERWORLD = catPredicate(c -> c != Biome.BiomeCategory.NETHER && c != Biome.BiomeCategory.THEEND);
        BiomeTypes.NETHER = catMatch(Biome.BiomeCategory.NETHER);
        BiomeTypes.END = catMatch(Biome.BiomeCategory.THEEND);
    }

    private static Biome get(ResourceKey<Biome> biome) {
        return REG.get().get(biome.location());
    }

    private static Biome.BiomeCategory cat(ResourceKey<Biome> biome) {
        return REG.get().get(biome).getBiomeCategory();
    }

    private static BiomeTypes.Type catMatch(Biome.BiomeCategory category) {
        return new BiomeTypes.Type(biome -> cat(biome) == category, biomeContext -> biomeContext.getProperties().getCategory() == category);
    }

    private static BiomeTypes.Type catPredicate(Predicate<Biome.BiomeCategory> predicate) {
        return new BiomeTypes.Type(biome -> predicate.test(cat(biome)), biomeContext -> predicate.test(biomeContext.getProperties().getCategory()));
    }

    private static BiomeTypes.Type pathContains(String content) {
        return new BiomeTypes.Type(biome -> biome.location().getPath().contains(content), biomeContext -> biomeContext.getKey().getPath().contains(content));
    }

    private static BiomeTypes.Type reject() {
        return new BiomeTypes.Type(biome -> false, biomeContext -> false);
    }
}
