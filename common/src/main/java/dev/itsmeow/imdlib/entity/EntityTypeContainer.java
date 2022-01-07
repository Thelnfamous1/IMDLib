package dev.itsmeow.imdlib.entity;

import com.google.common.collect.ImmutableList;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.itsmeow.imdlib.IMDLib;
import dev.itsmeow.imdlib.entity.interfaces.ISelectiveVariantTypes;
import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.variant.EntityVariantList;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.mixin.SpawnPlacementsInvoker;
import dev.itsmeow.imdlib.util.HeadType;
import dev.itsmeow.imdlib.util.config.ConfigBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EntityTypeContainer<T extends Mob> {

    protected final IEntityTypeDefinition<T> definition;
    public boolean despawn;
    public Supplier<Set<ResourceKey<Biome>>> spawnBiomesSupplier;
    public EntityConfiguration config;
    /* Other optional data */
    protected EntityVariantList variantList;
    protected EntityDataAccessor<String> variantDataKey;
    /* Internals */
    protected boolean placementRegistered = false;
    protected Set<ResourceKey<Biome>> spawnBiomesCache = null;
    protected Supplier<Set<ResourceKey<Biome>>> spawnCostBiomesSupplier;
    protected Set<ResourceKey<Biome>> spawnCostBiomesCache = null;
    protected HeadType headType;
    protected RegistrySupplier<ModSpawnEggItem> egg;
    protected EntityType<T> entityType;

    protected final CustomConfigurationHolder<T> customConfigHolder = new CustomConfigurationHolder<>(this);
    protected final CustomConfigurationHolder<T> customConfigHolderClient = new CustomConfigurationHolder<>(this);

    protected final LazyLoadedValue<MobSpawnSettings.SpawnerData> spawnEntry = new LazyLoadedValue<>(() -> new MobSpawnSettings.SpawnerData(this.entityType, config.spawnWeight.get(), config.spawnMinGroup.get(), config.spawnMaxGroup.get()));

    public EntityTypeContainer(IEntityTypeDefinition<T> def) {
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

    public static List<String> setBiomesToIDs(Set<ResourceKey<Biome>> set) {
        return set.parallelStream().filter(Objects::nonNull).map(biomeResourceKey -> biomeResourceKey.location().toString()).collect(Collectors.toList());
    }

    /* Simple Getters */
    public IEntityTypeDefinition<T> getDefinition() {
        return definition;
    }

    public EntityType<T> getEntityType() {
        return entityType;
    }

    public EntityConfiguration getConfiguration() {
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


    public CustomConfigurationHolder<T> getCustomConfiguration() {
        return customConfigHolder;
    }

    public CustomConfigurationHolder<T> getCustomConfigurationClient() {
        return customConfigHolderClient;
    }

    /* Protected/package-private operations */
    void setHeadType(HeadType headType) {
        this.headType = headType;
    }

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

    public RegistrySupplier<ModSpawnEggItem> getEggItem() {
        return egg;
    }

    void registerPlacement() {
        if (definition.getPlacementType() != null && definition.getPlacementPredicate() != null && definition.getPlacementHeightMapType() != null) {
            if (!placementRegistered) {
                SpawnPlacementsInvoker.invokeRegister(this.entityType, definition.getPlacementType(), definition.getPlacementHeightMapType(), definition.getPlacementPredicate());
                placementRegistered = true;
            }
        }
    }

    protected void createConfiguration(ConfigBuilder builder) {
        this.config = new EntityConfiguration(builder);
    }

    protected void customConfigurationLoad() {
        if (definition.getCustomConfigLoad() != null) {
            definition.getCustomConfigLoad().load(customConfigHolder);
        }
    }

    protected void clientCustomConfigurationLoad() {
        if (definition.getCustomClientConfigLoad() != null) {
            definition.getCustomClientConfigLoad().load(customConfigHolderClient);
        }
    }


    protected void customConfigurationInit(ConfigBuilder builder) {
        if (definition.getCustomConfigInit() != null) {
            definition.getCustomConfigInit().init(customConfigHolder, builder);
        }
    }

    protected void clientCustomConfigurationInit(ConfigBuilder builder) {
        if (definition.getCustomClientConfigInit() != null) {
            builder.push(this.getEntityName());
            definition.getCustomClientConfigInit().init(customConfigHolderClient, builder);
            builder.pop();
        }
    }

    public void onCreateEntityType() {
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

    public void setSpawnBiomesSupplier(Supplier<Set<ResourceKey<Biome>>> biomesSupplier) {
        this.spawnBiomesCache = null;
        this.spawnBiomesSupplier = biomesSupplier;
    }

    public void setSpawnCostBiomesSupplier(Supplier<Set<ResourceKey<Biome>>> biomesSupplier) {
        this.spawnCostBiomesCache = null;
        this.spawnCostBiomesSupplier = biomesSupplier;
    }

    /* Variant getters */
    public Optional<IVariant> getVariantForName(String name) {
        return this.variantList.getVariantForName(name);
    }

    @Deprecated
    public Optional<IVariant> getVariantForIndex(int index) {
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

    /* Subclasses */
    public static class Builder<T extends Mob> extends AbstractEntityBuilder<T, EntityTypeContainer<T>, Builder<T>> {

        private Builder(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public static <T extends Mob> Builder<T> create(Class<T> EntityClass, EntityType.EntityFactory<T> factory, String entityNameIn, Supplier<AttributeSupplier.Builder> attributeMap, String modid) {
            return new Builder<>(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public EntityTypeContainer<T> rawBuild() {
            return new EntityTypeContainer<>(new EntityTypeDefinition<>(this));
        }

        @Override
        public Builder<T> getImplementation() {
            return this;
        }

    }

    public class EntityConfiguration {
        public Supplier<Boolean> doSpawning;
        public Supplier<Boolean> biomeVariants;
        public Supplier<Integer> spawnMinGroup;
        public Supplier<Integer> spawnMaxGroup;
        public Supplier<Integer> spawnWeight;
        public Supplier<Boolean> useSpawnCosts;
        public Supplier<Double> spawnCostPer;
        public Supplier<Double> spawnMaxCost;
        public Supplier<List<? extends String>> biomesList;
        public Supplier<List<? extends String>> spawnCostBiomes;
        public Supplier<Boolean> doDespawn;

        protected EntityConfiguration(ConfigBuilder builder) {
            EntityTypeContainer<T> container = EntityTypeContainer.this;
            builder.push(container.getEntityName());
            {
                if (definition.getSpawnClassification() == MobCategory.CREATURE)
                    doDespawn = builder.define("can_despawn", "Allows the entity to despawn freely when no players are nearby, like vanilla monsters do", definition.despawns());
                if (hasSpawns()) {
                    builder.push("spawning");
                    {
                        doSpawning = builder.define("spawn_naturally", "Enables natural spawning - More info on these options: https://minecraft.fandom.com/wiki/Spawn#Java_Edition", true);
                        spawnWeight = builder.defineInRange("spawn_weight", "The spawn weight compared to other entities (typically between 6-20)", definition.getSpawnWeight(), 1, 9999);
                        spawnMinGroup = builder.defineInRange("minimum_group_size", "Minimum amount of entities in spawned groups", definition.getSpawnMinGroup(), 1, 9999);
                        spawnMaxGroup = builder.defineInRange("maximum_group_size", "Maximum amount of entities in spawned groups - Must be greater or equal to min value", definition.getSpawnMaxGroup(), 1, 9999);
                        biomesList = builder.defineList("spawn_biomes", "Enter biome IDs. Supports modded biomes https://minecraft.fandom.com/wiki/Biome#Biome_IDs", () -> setBiomesToIDs(definition.getDefaultSpawnBiomes().get()), "", input -> input instanceof String);
                        if (ISelectiveVariantTypes.class.isAssignableFrom(getEntityClass())) {
                            biomeVariants = builder.define("biome_based_variants", "Enables biome based variant selection. This will make this entity choose variants tailored to the biome they spawn in (Only applies to natural spawns)", true);
                        }
                        builder.push("spawn_costs");
                        {
                            useSpawnCosts = builder.define("use_spawn_costs", "Whether to use spawn costs in spawning or not", definition.useSpawnCosts());
                            spawnCostPer = builder.defineInRange("cost_per_spawn", "Cost to spawning algorithm per entity spawned", definition.getSpawnCostPer(), Double.MIN_VALUE, Double.MAX_VALUE);
                            spawnMaxCost = builder.defineInRange("maximum_cost_per_biome", "Maximum cost the spawning algorithm can accrue for this entity", definition.getSpawnMaxCost(), Double.MIN_VALUE, Double.MAX_VALUE);
                            spawnCostBiomes = builder.defineList("spawn_cost_biomes", "Enter biome IDs to use these costs in. Supports modded biomes. An empty list will use spawn_biomes https://minecraft.fandom.com/wiki/Biome#Biome_IDs", new ArrayList<>(), "", input -> input instanceof String);
                        }
                        builder.pop();
                    }
                    builder.pop();
                }
                EntityTypeContainer.this.customConfigurationInit(builder);
            }
            builder.pop();
        }

        protected void load() {
            if (hasSpawns()) {
                despawn = definition.getSpawnClassification() == MobCategory.CREATURE ? doDespawn.get() : definition.despawns();
                Function<Supplier<List<? extends String>>, Set<ResourceKey<Biome>>> biomesLoader = (configList) -> {
                    HashSet<ResourceKey<Biome>> biomeKeys = new HashSet<>();
                    for (String biomeName : configList.get()) {
                        ResourceLocation rl = new ResourceLocation(biomeName);
                        try {
                            Registry<Biome> reg = IMDLib.getStaticServerInstance().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
                            Biome biome = reg.get(rl);
                            if (biome == null) {
                                LogManager.getLogger().error("Invalid biome \"" + biomeName + "\" for entity " + getEntityName() + ". No biome exists with that name. Skipping.");
                            } else {
                                biomeKeys.add(ResourceKey.create(Registry.BIOME_REGISTRY, rl));
                            }
                        } catch (Exception e) {
                            LogManager.getLogger().error("Invalid biome name: \"" + biomeName + "\" for entity " + getEntityName() + ". Is it formatted correctly? Skipping.");
                        }
                    }
                    return biomeKeys;
                };

                EntityTypeContainer.this.setSpawnBiomesSupplier(() -> biomesLoader.apply(biomesList));
                if (useSpawnCosts.get()) {
                    EntityTypeContainer.this.setSpawnCostBiomesSupplier(spawnCostBiomes.get().size() == 0 ? spawnBiomesSupplier : () -> biomesLoader.apply(spawnCostBiomes));
                }
            }
            EntityTypeContainer.this.customConfigurationLoad();
        }
    }

    public static class CustomConfigurationHolder<T extends Mob> {
        protected Map<String, Pair<Class<?>, Supplier<?>>> values = new HashMap<>();
        protected EntityTypeContainer<T> container;

        CustomConfigurationHolder(EntityTypeContainer<T> container) {
            this.container = container;
        }

        public int getInt(String path) {
            return getIntHolder(path).get();
        }

        public String getString(String path) {
            return getStringHolder(path).get();
        }

        public double getDouble(String path) {
            return getDoubleHolder(path).get();
        }

        public long getLong(String path) {
            return getLongHolder(path).get();
        }

        public boolean getBoolean(String path) {
            return getBooleanHolder(path).get();
        }

        public <V> Supplier<V> getAnyHolder(Class<? extends V> clazz, String path) {
            Pair<Class<?>, Supplier<?>> v = values.get(path);
            return v.getLeft().isAssignableFrom(clazz) ? () -> clazz.cast(v.getRight().get()) : null;
        }

        public Supplier<Integer> getIntHolder(String path) {
            Pair<Class<?>, Supplier<?>> v = values.get(path);
            return v.getLeft().isAssignableFrom(Integer.class) ? () -> (Integer) v.getRight().get() : null;
        }

        public Supplier<String> getStringHolder(String path) {
            Pair<Class<?>, Supplier<?>> v = values.get(path);
            return v.getLeft().isAssignableFrom(String.class) ? () -> (String) v.getRight().get() : null;
        }

        public Supplier<Double> getDoubleHolder(String path) {
            Pair<Class<?>, Supplier<?>> v = values.get(path);
            return v.getLeft().isAssignableFrom(Double.class) ? () -> (Double) v.getRight().get() : null;
        }

        public Supplier<Long> getLongHolder(String path) {
            Pair<Class<?>, Supplier<?>> v = values.get(path);
            return v.getLeft().isAssignableFrom(Long.class) ? () -> (Long) v.getRight().get() : null;
        }

        public Supplier<Boolean> getBooleanHolder(String path) {
            Pair<Class<?>, Supplier<?>> v = values.get(path);
            return v.getLeft().isAssignableFrom(Boolean.class) ? () -> (Boolean) v.getRight().get() : null;
        }

        public void put(String path, Class<?> type, Supplier<?> value) {
            values.put(path, Pair.of(type, value));
        }
    }

    @FunctionalInterface
    public interface CustomConfigurationLoad {
        void load(CustomConfigurationHolder<?> holder);
    }

    @FunctionalInterface
    public interface CustomConfigurationInit {
        void init(CustomConfigurationHolder<?> holder, ConfigBuilder builder);
    }
}
