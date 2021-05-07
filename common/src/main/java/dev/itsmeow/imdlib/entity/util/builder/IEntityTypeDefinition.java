package dev.itsmeow.imdlib.entity.util.builder;

import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public interface IEntityTypeDefinition<T extends Mob> {

    String getModId();

    Class<T> getEntityClass();

    EntityType.EntityFactory<T> getEntityFactory();

    String getEntityName();

    MobCategory getSpawnClassification();

    boolean hasEgg();

    int getEggSolidColor();

    int getEggSpotColor();

    boolean hasSpawns();

    int getSpawnWeight();

    int getSpawnMinGroup();

    int getSpawnMaxGroup();

    boolean useSpawnCosts();

    double getSpawnCostPer();

    double getSpawnMaxCost();

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

    Supplier<Set<ResourceKey<Biome>>> getDefaultSpawnBiomes();

    SpawnPlacements.Type getPlacementType();

    Heightmap.Types getPlacementHeightMapType();

    SpawnPlacements.SpawnPredicate<T> getPlacementPredicate();

    Supplier<AttributeSupplier.Builder> getAttributeMap();

}
