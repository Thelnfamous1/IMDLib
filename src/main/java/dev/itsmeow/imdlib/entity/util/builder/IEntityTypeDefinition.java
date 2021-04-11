package dev.itsmeow.imdlib.entity.util.builder;

import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;

public interface IEntityTypeDefinition<T extends MobEntity> {

    String getModId();

    Class<T> getEntityClass();

    EntityType.IFactory<T> getEntityFactory();

    String getEntityName();

    EntityClassification getSpawnClassification();

    boolean hasEgg();

    int getEggSolidColor();

    int getEggSpotColor();

    boolean hasSpawns();

    int getSpawnWeight();

    int getSpawnMinGroup();

    int getSpawnMaxGroup();

    float getWidth();

    float getHeight();

    boolean despawns();

    int getVariantAmount();

    IVariant[] getVariants();

    @Nullable
    CustomConfigurationLoad getCustomConfigLoad();

    @Nullable
    CustomConfigurationInit getCustomConfigInit();

    @Nullable
    CustomConfigurationLoad getCustomClientConfigLoad();

    @Nullable
    CustomConfigurationInit getCustomClientConfigInit();

    Supplier<Set<Biome>> getDefaultSpawnBiomes();

    EntitySpawnPlacementRegistry.PlacementType getPlacementType();

    Heightmap.Type getPlacementHeightMapType();

    EntitySpawnPlacementRegistry.IPlacementPredicate<T> getPlacementPredicate();

}
