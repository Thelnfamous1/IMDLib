package dev.itsmeow.imdlib.entity;

import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Set;
import java.util.function.Supplier;

public class EntityTypeDefinition<T extends Mob> implements IEntityTypeDefinition<T> {

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
    public EntityType.EntityFactory<T> getEntityFactory() {
        return builder.factory;
    }

    @Override
    public String getEntityName() {
        return builder.entityName;
    }

    @Override
    public MobCategory getSpawnClassification() {
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

    /*
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

     */

    @Override
    public Supplier<Set<ResourceKey<Biome>>> getDefaultSpawnBiomes() {
        return builder.defaultBiomeSupplier;
    }

    @Override
    public SpawnPlacements.Type getPlacementType() {
        return builder.placementType;
    }

    @Override
    public Heightmap.Types getPlacementHeightMapType() {
        return builder.heightMapType;
    }

    @Override
    public SpawnPlacements.SpawnPredicate<T> getPlacementPredicate() {
        return builder.placementPredicate;
    }

    @Override
    public Supplier<AttributeSupplier.Builder> getAttributeMap() {
        return builder.attributeMap;
    }

    @Override
    public double getSpawnCostPer() {
        return builder.spawnCostPer;
    }

    @Override
    public double getSpawnMaxCost() {
        return builder.spawnMaxCost;
    }

    @Override
    public boolean useSpawnCosts() {
        return builder.useSpawnCosts;
    }

}
