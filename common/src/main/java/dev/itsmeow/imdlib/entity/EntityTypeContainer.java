package dev.itsmeow.imdlib.entity;

import com.google.common.collect.ImmutableList;
import dev.itsmeow.imdlib.config.EntityTypeContainerConfigHandler;
import dev.itsmeow.imdlib.entity.interfaces.ISelectiveVariantTypes;
import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.variant.EntityVariantList;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.mixin.SpawnPlacementsInvoker;
import dev.itsmeow.imdlib.util.ClassLoadHacks;
import dev.itsmeow.imdlib.util.HeadType;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EntityTypeContainer<T extends Mob> {

    protected final IEntityTypeDefinition<T> definition;
    /* FIGURE this out
    protected final CustomConfigurationHolder<T> customConfigHolder = new CustomConfigurationHolder<>(this);
    protected final CustomConfigurationHolder<T> customConfigHolderClient = new CustomConfigurationHolder<>(this);

     */
    /* Other optional data */
    protected EntityVariantList variantList;
    protected EntityDataAccessor<String> variantDataKey;
    protected boolean despawn;
    /* Internals */
    protected boolean placementRegistered = false;
    protected Supplier<Set<ResourceKey<Biome>>> spawnBiomesSupplier;
    protected Set<ResourceKey<Biome>> spawnBiomesCache = null;
    protected Supplier<Set<ResourceKey<Biome>>> spawnCostBiomesSupplier;
    protected Set<ResourceKey<Biome>> spawnCostBiomesCache = null;
    public EntityTypeContainerConfigHandler config;
    protected HeadType headType;
    protected ModSpawnEggItem egg;
    protected EntityType<T> entityType;
    protected final LazyLoadedValue<MobSpawnSettings.SpawnerData> spawnEntry = new LazyLoadedValue<>(() -> new MobSpawnSettings.SpawnerData(this.entityType, config.spawnWeight.get(), config.spawnMinGroup.get(), config.spawnMaxGroup.get()));

    protected EntityTypeContainer(IEntityTypeDefinition<T> def) {
        this.definition = def;
        if (this.hasVariants()) {
            variantList = new EntityVariantList(def.getVariantAmount());
            variantList.add(def.getVariants());
        }
        spawnBiomesSupplier = def.getDefaultSpawnBiomes() != null ? def.getDefaultSpawnBiomes() : HashSet::new;
        spawnCostBiomesSupplier = spawnBiomesSupplier;
    }

    /* Static methods */
    @SuppressWarnings("deprecation")
    public static <T extends Mob> boolean waterSpawn(EntityType<T> type, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random rand) {
        return pos.getY() > 45 && pos.getY() < (world.getSeaLevel() - 1) && world.getFluidState(pos).getType() == Fluids.WATER;
    }

    protected static List<String> setBiomesToIDs(Set<ResourceKey<Biome>> set) {
        return set.parallelStream().filter(Objects::nonNull).map(biomeResourceKey -> biomeResourceKey.location().toString()).collect(Collectors.toList());
    }

    /* Simple Getters */
    public IEntityTypeDefinition<T> getDefinition() {
        return definition;
    }

    public EntityType<T> getEntityType() {
        return entityType;
    }

    public EntityTypeContainerConfigHandler getConfiguration() {
        return this.config;
    }

    public String getModId() {
        return definition.getModId();
    }

    public boolean hasEgg() {
        return definition.hasEgg();
    }

    public boolean hasSpawns() {
        return definition.hasSpawns();
    }

    public float getWidth() {
        return definition.getWidth();
    }

    public float getHeight() {
        return definition.getHeight();
    }

    public Class<T> getEntityClass() {
        return definition.getEntityClass();
    }

    public String getEntityName() {
        return definition.getEntityName();
    }

    public Supplier<AttributeSupplier.Builder> getAttributeBuilder() {
        return definition.getAttributeMap();
    }

    public HeadType getHeadType() {
        return this.headType;
    }

    /* Protected/package-private operations */
    void setHeadType(HeadType headType) {
        this.headType = headType;
    }

    /*TODO figure out
    public CustomConfigurationHolder<T> getCustomConfiguration() {
        return customConfigHolder;
    }

    public CustomConfigurationHolder<T> getCustomConfigurationClient() {
        return customConfigHolderClient;
    }

     */

    public boolean hasVariants() {
        return definition.getVariantAmount() > 0;
    }

    public int getVariantMax() {
        return definition.getVariantAmount();
    }

    public MobSpawnSettings.SpawnerData getSpawnEntry() {
        return spawnEntry.get();
    }

    public boolean despawns() {
        return despawn;
    }

    public ModSpawnEggItem getEggItem() {
        return egg;
    }

    protected void onCreateEntityType() {
        if (this.hasEgg()) {
            this.egg = new ModSpawnEggItem(this);
        }
    }

    void registerPlacement() {
        if (definition.getPlacementType() != null && definition.getPlacementPredicate() != null && definition.getPlacementHeightMapType() != null) {
            if (!placementRegistered) {
                SpawnPlacementsInvoker.invokeRegister(this.entityType, definition.getPlacementType(), definition.getPlacementHeightMapType(), definition.getPlacementPredicate());
                placementRegistered = true;
            }
        }
    }





    /* Biome Getter/Setters */
    public List<String> getBiomeIDs() {
        return setBiomesToIDs(this.getSpawnBiomes());
    }

    public List<String> getSpawnCostBiomeIDs() {
        return setBiomesToIDs(this.getSpawnCostBiomes());
    }

    public Set<ResourceKey<Biome>> getSpawnBiomes() {
        if (this.spawnBiomesCache == null) {
            this.spawnBiomesCache = spawnBiomesSupplier.get();
        }
        return this.spawnBiomesCache;
    }

    public Set<ResourceKey<Biome>> getSpawnCostBiomes() {
        if (this.spawnCostBiomesCache == null) {
            this.spawnCostBiomesCache = spawnCostBiomesSupplier.get();
        }
        return this.spawnCostBiomesCache;
    }

    protected void setSpawnBiomesSupplier(Supplier<Set<ResourceKey<Biome>>> biomesSupplier) {
        this.spawnBiomesCache = null;
        this.spawnBiomesSupplier = biomesSupplier;
    }

    protected void setSpawnCostBiomesSupplier(Supplier<Set<ResourceKey<Biome>>> biomesSupplier) {
        this.spawnCostBiomesCache = null;
        this.spawnCostBiomesSupplier = biomesSupplier;
    }

    /* Variant getters */
    public IVariant getVariantForName(String name) {
        return this.variantList.getVariantForName(name);
    }

    @Deprecated
    public IVariant getVariantForIndex(int index) {
        return this.variantList.getVariantForIndex(index);
    }

    public ImmutableList<IVariant> getVariants() {
        return this.variantList.getVariantList();
    }

    @Deprecated
    public int getVariantIndex(IVariant variant) {
        return this.variantList.getVariantIndex(variant);
    }

    public EntityDataAccessor<String> getVariantDataKey() {
        if (this.variantDataKey == null) {
            this.variantDataKey = SynchedEntityData.defineId(this.getEntityClass(), EntityDataSerializers.STRING);
        }
        return this.variantDataKey;
    }

    @ExpectPlatform
    public static EntityTypeContainerConfigHandler getConfigHandlerFor(EntityTypeContainer<?> container) {
        throw new RuntimeException();
    }


}
