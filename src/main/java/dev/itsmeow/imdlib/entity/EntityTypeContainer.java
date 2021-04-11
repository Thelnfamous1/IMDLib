package dev.itsmeow.imdlib.entity;

import com.google.common.collect.ImmutableList;
import dev.itsmeow.imdlib.entity.interfaces.ISelectiveVariantTypes;
import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.variant.EntityVariantList;
import dev.itsmeow.imdlib.entity.util.variant.IVariant;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import dev.itsmeow.imdlib.util.HeadType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EntityTypeContainer<T extends MobEntity> {

    protected final IEntityTypeDefinition<T> definition;

    /* Other optional data */
    protected EntityVariantList variantList;
    protected DataParameter<String> variantDataKey;
    protected boolean despawn;

    /* Internals */
    protected boolean placementRegistered = false;

    protected Supplier<Set<RegistryKey<Biome>>> spawnBiomesSupplier;
    protected Set<RegistryKey<Biome>> spawnBiomesCache = null;

    protected Supplier<Set<RegistryKey<Biome>>> spawnCostBiomesSupplier;
    protected Set<RegistryKey<Biome>> spawnCostBiomesCache = null;

    protected EntityConfiguration config;
    protected HeadType headType;
    protected ModSpawnEggItem egg;
    protected EntityType<T> entityType;

    protected final CustomConfigurationHolder<T> customConfigHolder = new CustomConfigurationHolder<>(this);
    protected final CustomConfigurationHolder<T> customConfigHolderClient = new CustomConfigurationHolder<>(this);

    protected final NonNullLazy<MobSpawnInfo.Spawners> spawnEntry = NonNullLazy.of(() -> new MobSpawnInfo.Spawners(this.entityType, config.spawnWeight.get(), config.spawnMinGroup.get(), config.spawnMaxGroup.get()));

    protected EntityTypeContainer(IEntityTypeDefinition<T> def) {
        this.definition = def;
        if (this.hasVariants()) {
            variantList = new EntityVariantList(def.getVariantAmount());
            variantList.add(def.getVariants());
        }
        spawnBiomesSupplier = def.getDefaultSpawnBiomes() != null ? def.getDefaultSpawnBiomes() : HashSet::new;
        spawnCostBiomesSupplier = spawnBiomesSupplier;
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

    public Supplier<AttributeModifierMap.MutableAttribute> getAttributeBuilder() {
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

    public boolean hasVariants() {
        return definition.getVariantAmount() > 0;
    }

    public int getVariantMax() {
        return definition.getVariantAmount();
    }

    public MobSpawnInfo.Spawners getSpawnEntry() {
        return spawnEntry.get();
    }

    public boolean despawns() {
        return despawn;
    }

    /* Protected/package-private operations */
    void setHeadType(HeadType headType) {
        this.headType = headType;
    }

    protected void onCreateEntityType() {
        if (this.hasEgg()) {
            this.egg = new ModSpawnEggItem(this);
        }
    }

    void registerPlacement() {
        if (definition.getPlacementType() != null && definition.getPlacementPredicate() != null && definition.getPlacementHeightMapType() != null) {
            if (!placementRegistered) {
                EntitySpawnPlacementRegistry.register(this.entityType, definition.getPlacementType(), definition.getPlacementHeightMapType(), definition.getPlacementPredicate());
                placementRegistered = true;
            }
        }
    }

    @SuppressWarnings("deprecation")
    boolean registerAttributes() {
        return definition.getAttributeMap() != null && GlobalEntityTypeAttributes.put(entityType, definition.getAttributeMap().get().create()) != null;
    }

    protected void createConfiguration(ForgeConfigSpec.Builder builder) {
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


    protected void customConfigurationInit(ForgeConfigSpec.Builder builder) {
        if (definition.getCustomConfigInit() != null) {
            definition.getCustomConfigInit().init(customConfigHolder, builder);
        }
    }

    protected void clientCustomConfigurationInit(ForgeConfigSpec.Builder builder) {
        if (definition.getCustomClientConfigInit() != null) {
            builder.push(this.getEntityName());
            definition.getCustomClientConfigInit().init(customConfigHolderClient, builder);
            builder.pop();
        }
    }

    /* Biome Getter/Setters */
    public List<String> getBiomeIDs() {
        return setBiomesToIDs(this.getSpawnBiomes());
    }

    public List<String> getSpawnCostBiomeIDs() {
        return setBiomesToIDs(this.getSpawnCostBiomes());
    }

    public Set<RegistryKey<Biome>> getSpawnBiomes() {
        if (this.spawnBiomesCache == null) {
            this.spawnBiomesCache = spawnBiomesSupplier.get();
        }
        return this.spawnBiomesCache;
    }

    public Set<RegistryKey<Biome>> getSpawnCostBiomes() {
        if (this.spawnCostBiomesCache == null) {
            this.spawnCostBiomesCache = spawnCostBiomesSupplier.get();
        }
        return this.spawnCostBiomesCache;
    }

    protected void setSpawnBiomesSupplier(Supplier<Set<RegistryKey<Biome>>> biomesSupplier) {
        this.spawnBiomesCache = null;
        this.spawnBiomesSupplier = biomesSupplier;
    }

    protected void setSpawnCostBiomesSupplier(Supplier<Set<RegistryKey<Biome>>> biomesSupplier) {
        this.spawnCostBiomesCache = null;
        this.spawnCostBiomesSupplier = biomesSupplier;
    }

    /* Static methods */
    @SuppressWarnings("deprecation")
    public static <T extends MobEntity> boolean waterSpawn(EntityType<T> type, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
        return pos.getY() > 45 && pos.getY() < (world.getSeaLevel() - 1) && world.getFluidState(pos).getFluid() == Fluids.WATER;
    }

    protected static List<String> setBiomesToIDs(Set<RegistryKey<Biome>> set) {
        return set.parallelStream().filter(Objects::nonNull).map(b -> b.getLocation().toString()).collect(Collectors.toList());
    }

    /* Variant getters */
    @Nullable
    @CheckForNull
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

    public DataParameter<String> getVariantDataKey() {
        if (this.variantDataKey == null) {
            this.variantDataKey = EntityDataManager.createKey(this.getEntityClass(), DataSerializers.STRING);
        }
        return this.variantDataKey;
    }

    /* Subclasses */
    public static class Builder<T extends MobEntity> extends AbstractEntityBuilder<T, EntityTypeContainer<T>, Builder<T>> {

        private Builder(Class<T> EntityClass, EntityType.IFactory<T> factory, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            super(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

        public EntityTypeContainer<T> rawBuild() {
            return new EntityTypeContainer<>(new EntityTypeDefinition<>(this));
        }

        @Override
        public Builder<T> getImplementation() {
            return this;
        }

        public static <T extends MobEntity> Builder<T> create(Class<T> EntityClass, EntityType.IFactory<T> factory, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            return new Builder<>(EntityClass, factory, entityNameIn, attributeMap, modid);
        }

    }

    public class EntityConfiguration {
        public ForgeConfigSpec.BooleanValue doSpawning;
        public ForgeConfigSpec.BooleanValue biomeVariants;
        public ForgeConfigSpec.IntValue spawnMinGroup;
        public ForgeConfigSpec.IntValue spawnMaxGroup;
        public ForgeConfigSpec.IntValue spawnWeight;
        public ForgeConfigSpec.BooleanValue useSpawnCosts;
        public ForgeConfigSpec.DoubleValue spawnCostPer;
        public ForgeConfigSpec.DoubleValue spawnMaxCost;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> biomesList;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> spawnCostBiomes;
        public ForgeConfigSpec.BooleanValue doDespawn;

        protected EntityConfiguration(ForgeConfigSpec.Builder builder) {
            EntityTypeContainer<T> container = EntityTypeContainer.this;
            builder.push(container.getEntityName());
            {
                if (definition.getSpawnClassification() == EntityClassification.CREATURE)
                    doDespawn = builder.comment("Allows the entity to despawn freely when no players are nearby, like vanilla monsters do").worldRestart().define("can_despawn", definition.despawns());
                if (hasSpawns()) {
                    builder.push("spawning");
                    {
                        doSpawning = builder.comment("Enables natural spawning - More info on these options: https://minecraft.fandom.com/wiki/Spawn#Java_Edition").worldRestart().define("spawn_naturally", true);
                        spawnWeight = builder.comment("The spawn weight compared to other entities (typically between 6-20)").worldRestart().defineInRange("spawn_weight", definition.getSpawnWeight(), 1, 9999);
                        spawnMinGroup = builder.comment("Minimum amount of entities in spawned groups").worldRestart().defineInRange("minimum_group_size", definition.getSpawnMinGroup(), 1, 9999);
                        spawnMaxGroup = builder.comment("Maximum amount of entities in spawned groups - Must be greater or equal to min value").worldRestart().defineInRange("maximum_group_size", definition.getSpawnMaxGroup(), 1, 9999);
                        biomesList = builder.comment("Enter biome IDs. Supports modded biomes https://minecraft.fandom.com/wiki/Biome#Biome_IDs").worldRestart().defineList("spawn_biomes", container.getBiomeIDs(), input -> input instanceof String);
                        if (ISelectiveVariantTypes.class.isAssignableFrom(getEntityClass())) {
                            biomeVariants = builder.comment("Enables biome based variant selection. This will make this entity choose variants tailored to the biome they spawn in (Only applies to natural spawns)").worldRestart().define("biome_based_variants", true);
                        }
                        builder.push("spawn_costs");
                        {
                            useSpawnCosts = builder.comment("Whether to use spawn costs in spawning or not").worldRestart().define("use_spawn_costs", definition.useSpawnCosts());
                            spawnCostPer = builder.comment("Cost to spawning algorithm per entity spawned").worldRestart().defineInRange("cost_per_spawn", definition.getSpawnCostPer(), Double.MIN_VALUE, Double.MAX_VALUE);
                            spawnMaxCost = builder.comment("Maximum cost the spawning algorithm can accrue for this entity").worldRestart().defineInRange("maximum_cost_per_biome", definition.getSpawnMaxCost(), Double.MIN_VALUE, Double.MAX_VALUE);
                            spawnCostBiomes = builder.comment("Enter biome IDs to use these costs in. Supports modded biomes. An empty list will use spawn_biomes https://minecraft.fandom.com/wiki/Biome#Biome_IDs").worldRestart().defineList("spawn_cost_biomes", new ArrayList<>(), input -> input instanceof String);
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
                despawn = definition.getSpawnClassification() == EntityClassification.CREATURE ? doDespawn.get() : definition.despawns();
                Function<ForgeConfigSpec.ConfigValue<List<? extends String>>, Set<RegistryKey<Biome>>> biomesLoader = (configList) -> {
                    HashSet<RegistryKey<Biome>> biomeKeys = new HashSet<>();
                    for (String biomeName : configList.get()) {
                        try {
                            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
                            if (biome == null || biome.getRegistryName() == null) {
                                LogManager.getLogger().error("Invalid biome \"" + biomeName + "\" for entity " + getEntityName() + ". No biome exists with that name. Skipping.");
                            } else {
                                biomeKeys.add(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, biome.getRegistryName()));
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

    public static class CustomConfigurationHolder<T extends MobEntity> {
        protected Map<String, ForgeConfigSpec.ConfigValue<?>> values = new HashMap<>();
        protected EntityTypeContainer<T> container;

        CustomConfigurationHolder(EntityTypeContainer<T> container) {
            this.container = container;
        }

        public ForgeConfigSpec.IntValue getInt(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.IntValue ? (ForgeConfigSpec.IntValue) v : null;
        }

        @SuppressWarnings("unchecked")
        public ForgeConfigSpec.ConfigValue<String> getString(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v.get() instanceof String ? (ForgeConfigSpec.ConfigValue<String>) v : null;
        }

        public ForgeConfigSpec.DoubleValue getDouble(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.DoubleValue ? (ForgeConfigSpec.DoubleValue) v : null;
        }

        public ForgeConfigSpec.LongValue getLong(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.LongValue ? (ForgeConfigSpec.LongValue) v : null;
        }

        public ForgeConfigSpec.BooleanValue getBoolean(String path) {
            ForgeConfigSpec.ConfigValue<?> v = values.get(path);
            return v instanceof ForgeConfigSpec.BooleanValue ? (ForgeConfigSpec.BooleanValue) v : null;
        }

        public void put(ForgeConfigSpec.ConfigValue<?> value) {
            String path = String.join("/", value.getPath().toArray(new String[0]));
            path = path.substring(("entities/" + container.getEntityName() + "/").length());
            values.put(path, value);
        }
    }

    @FunctionalInterface
    public interface CustomConfigurationLoad {
        void load(CustomConfigurationHolder<?> holder);
    }

    @FunctionalInterface
    public interface CustomConfigurationInit {
        void init(CustomConfigurationHolder<?> holder, ForgeConfigSpec.Builder builder);
    }
}
