package dev.itsmeow.imdlib.entity.util.builder;

import java.util.function.Function;
import java.util.function.Supplier;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.BiomeListBuilder;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;

public interface IEntityBuilder<T extends MobEntity, C extends EntityTypeContainer<T>, B extends IEntityBuilder<T, C, B>> {

    B spawn(EntityClassification type, int weight, int min, int max);

    B egg(int solid, int spot);

    B size(float width, float height);

    B despawn();

    B config(CustomConfigurationInit configurationInit);

    B clientConfig(CustomConfigurationInit configurationInit);

    B config(CustomConfigurationInit configurationInit, CustomConfigurationLoad configurationLoad);

    B clientConfig(CustomConfigurationInit configurationInit, CustomConfigurationLoad configurationLoad);

    B placement(EntitySpawnPlacementRegistry.PlacementType type, Heightmap.Type heightMap, EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate);

    B defaultPlacement(EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate);

    B waterPlacement();

    B waterPlacement(EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate);

    B biomes(BiomeDictionary.Type... biomeTypes);

    B biomesOverworld(BiomeDictionary.Type... biomeTypes);

    B biomes(Supplier<Biome[]> biomes);

    B biomes(Function<BiomeListBuilder, BiomeListBuilder> biomes);

    B variants(IVariant... variants);

    B variants(String... nameTextures);

    B variants(int max);

    B variants(Function<String, IVariant> constructor, String... variants);
    
    HeadType.Builder<T, C, B> head(String headName);
    
    HeadType.Builder<T, C, B> head();
    
    void setHeadBuild(Function<C, HeadType> builder);

    String getMod();

    default void preBuild() {
    }

    default void postBuild(C container) {
    }

    C rawBuild();

    default C build() {
        preBuild();
        C container = rawBuild();
        postBuild(container);
        return container;
    }
}
