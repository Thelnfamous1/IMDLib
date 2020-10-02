package dev.itsmeow.imdlib.entity.util.builder;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.itsmeow.imdlib.entity.util.EntityTypeContainer.CustomConfigurationHolder;
import dev.itsmeow.imdlib.entity.util.IVariant;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.IPlacementPredicate;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap.Type;

public class EntityTypeDefinition<T extends MobEntity> implements IEntityTypeDefinition<T> {

    private AbstractEntityBuilder<T, ?, ?> builder;

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
    public Function<World, T> getEntityFactory() {
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
    public CustomConfigurationHolder getCustomConfig() {
        return builder.customConfig;
    }

    @Override
    public CustomConfigurationHolder getCustomClientConfig() {
        return builder.customClientConfig;
    }

    @Override
    public Supplier<Set<Biome>> getSpawnBiomes() {
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

    @Override
    public Supplier<AttributeModifierMap.MutableAttribute> getAttributeMap() {
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

}
