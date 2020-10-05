package dev.itsmeow.imdlib.entity.util.builder;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import dev.itsmeow.imdlib.entity.util.IVariant;
import dev.itsmeow.imdlib.entity.util.EntityTypeContainer.CustomConfigurationHolder;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

public interface IEntityTypeDefinition<T extends MobEntity> {

    public String getModId();

    public Class<T> getEntityClass();

    public Function<World, T> getEntityFactory();

    public String getEntityName();

    public EntityClassification getSpawnClassification();

    public boolean hasEgg();

    public int getEggSolidColor();

    public int getEggSpotColor();

    public int getSpawnWeight();

    public int getSpawnMinGroup();

    public int getSpawnMaxGroup();

    public boolean useSpawnCosts();

    public double getSpawnCostPer();

    public double getSpawnMaxCost();

    public float getWidth();

    public float getHeight();

    public boolean despawns();

    public int getVariantAmount();

    public IVariant[] getVariants();

    @Nullable
    public CustomConfigurationHolder getCustomConfig();

    @Nullable
    public CustomConfigurationHolder getCustomClientConfig();

    public Supplier<Set<Biome>> getSpawnBiomes();

    public EntitySpawnPlacementRegistry.PlacementType getPlacementType();

    public Heightmap.Type getPlacementHeightMapType();

    public EntitySpawnPlacementRegistry.IPlacementPredicate<T> getPlacementPredicate();

    public Supplier<AttributeModifierMap.MutableAttribute> getAttributeMap();

}
