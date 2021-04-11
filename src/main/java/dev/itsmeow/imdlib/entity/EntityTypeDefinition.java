package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationInit;
import dev.itsmeow.imdlib.entity.EntityTypeContainer.CustomConfigurationLoad;
import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.IPlacementPredicate;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap.Type;

import java.util.Set;
import java.util.function.Supplier;

public class EntityTypeDefinition<T extends MobEntity> implements IEntityTypeDefinition<T> {

    private final AbstractEntityBuilder<T, ?, ?> builder;

    public EntityTypeDefinition(AbstractEntityBuilder<T, ?, ?> builder) {
        this.builder = builder;
    }

    @Override
    public String getModId() {
        return builder.modid;
    }

    @Override
    public Class<T> getEntityClass() {
        return builder.entityClass;
    }

    @Override
    public EntityType.IFactory<T> getEntityFactory() {
        return builder.factory;
    }

    @Override
    public String getEntityName() {
        return builder.entityName;
    }

    @Override
    public EntityClassification getSpawnClassification() {
        return builder.spawnType;
    }

    @Override
    public boolean hasEgg() {
        return builder.hasEgg;
    }

    @Override
    public int getEggSolidColor() {
        return builder.eggColorSolid;
    }

    @Override
    public int getEggSpotColor() {
        return builder.eggColorSpot;
    }

    @Override
    public boolean hasSpawns() {
        return builder.hasSpawns;
    }

    @Override
    public int getSpawnWeight() {
        return builder.spawnWeight;
    }

    @Override
    public int getSpawnMinGroup() {
        return builder.spawnMinGroup;
    }

    @Override
    public int getSpawnMaxGroup() {
        return builder.spawnMaxGroup;
    }

    @Override
    public float getWidth() {
        return builder.width;
    }

    @Override
    public float getHeight() {
        return builder.height;
    }

    @Override
    public boolean despawns() {
        return builder.despawn;
    }

    @Override
    public int getVariantAmount() {
        return builder.variantCount;
    }

    @Override
    public IVariant[] getVariants() {
        return builder.variants;
    }

    @Override
    public CustomConfigurationLoad getCustomConfigLoad() {
        return builder.customConfigLoad;
    }

    @Override
    public CustomConfigurationInit getCustomConfigInit() {
        return builder.customConfigInit;
    }

    @Override
    public CustomConfigurationLoad getCustomClientConfigLoad() {
        return builder.customClientConfigLoad;
    }

    @Override
    public CustomConfigurationInit getCustomClientConfigInit() {
        return builder.customClientConfigInit;
    }

    @Override
    public Supplier<Set<Biome>> getDefaultSpawnBiomes() {
        return builder.defaultBiomeSupplier;
    }

    @Override
    public PlacementType getPlacementType() {
        return builder.placementType;
    }

    @Override
    public Type getPlacementHeightMapType() {
        return builder.heightMapType;
    }

    @Override
    public IPlacementPredicate<T> getPlacementPredicate() {
        return builder.placementPredicate;
    }
}
