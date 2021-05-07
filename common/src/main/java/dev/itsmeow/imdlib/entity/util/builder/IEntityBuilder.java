package dev.itsmeow.imdlib.entity.util.builder;

import dev.itsmeow.imdlib.entity.EntityTypeContainer;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.BiomeTypes;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.util.BiomeListBuilder;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IEntityBuilder<T extends Mob, C extends EntityTypeContainer<T>, B extends IEntityBuilder<T, C, B>> {

    B spawn(MobCategory type, int weight, int min, int max);

    B spawnCosts(double cost, double maxCost);

    B egg(int solid, int spot);

    B size(float width, float height);

    B despawn();

    B config(CustomConfigurationInit configurationInit);

    B clientConfig(CustomConfigurationInit configurationInit);

    B config(CustomConfigurationInit configurationInit, CustomConfigurationLoad configurationLoad);

    B clientConfig(CustomConfigurationInit configurationInit, CustomConfigurationLoad configurationLoad);

    B placement(SpawnPlacements.Type type, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<T> predicate);

    B defaultPlacement(SpawnPlacements.SpawnPredicate<T> predicate);

    B waterPlacement();

    B waterPlacement(SpawnPlacements.SpawnPredicate<T> predicate);

    B biomes(BiomeTypes.Type... biomeTypes);

    B biomesOverworld(BiomeTypes.Type... biomeTypes);

    B biomes(Supplier<ResourceKey<Biome>[]> biomes);

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
