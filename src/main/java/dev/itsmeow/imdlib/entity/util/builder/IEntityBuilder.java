package dev.itsmeow.imdlib.entity.util.builder;

import java.util.function.Function;
import java.util.function.Supplier;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainer.CustomConfigurationHolder;
import dev.itsmeow.imdlib.entity.util.IVariant;
import dev.itsmeow.imdlib.util.BiomeListBuilder;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;

public interface IEntityBuilder<T extends MobEntity, C extends EntityTypeContainer<T>, B extends IEntityBuilder<T, C, B>> {

    public B spawn(EntityClassification type, int weight, int min, int max);

    public B spawnCosts(double cost, double maxCost);

    public B egg(int solid, int spot);

    public B size(float width, float height);

    public B despawn();

    public B config(CustomConfigurationHolder config);

    public B clientConfig(CustomConfigurationHolder config);

    public B placement(EntitySpawnPlacementRegistry.PlacementType type, Heightmap.Type heightMap, EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate);

    public B defaultPlacement(EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate);

    public B waterPlacement();

    public B waterPlacement(EntitySpawnPlacementRegistry.IPlacementPredicate<T> predicate);

    public B biomes(BiomeDictionary.Type... biomeTypes);

    public B biomes(Supplier<RegistryKey<Biome>[]> biomes);

    public B biomes(Function<BiomeListBuilder, BiomeListBuilder> biomes);

    public B variants(IVariant... variants);

    public B variants(String... nameTextures);

    public B variants(int max);

    public B variants(Function<String, IVariant> constructor, String... variants);
    
    public HeadType.Builder<T, C, B> head(String headName);
    
    public HeadType.Builder<T, C, B> head();
    
    public void setHeadBuild(Function<C, HeadType> builder);

    public String getMod();

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
