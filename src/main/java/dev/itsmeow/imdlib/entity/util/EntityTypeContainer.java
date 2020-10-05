package dev.itsmeow.imdlib.entity.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.ImmutableList;

import dev.itsmeow.imdlib.entity.util.builder.AbstractEntityBuilder;
import dev.itsmeow.imdlib.entity.util.builder.EntityTypeDefinition;
import dev.itsmeow.imdlib.entity.util.builder.IEntityTypeDefinition;
import dev.itsmeow.imdlib.item.ModSpawnEggItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeContainer<T extends MobEntity> {

    public EntityType<T> entityType;
    public ModSpawnEggItem egg;

    public final Class<T> entityClass;
    public final String entityName;
    public final EntityClassification spawnType;
    public final Function<World, T> factory;
    public final boolean hasEgg;
    public final int eggColorSolid;
    public final int eggColorSpot;
    public int spawnWeight;
    public int spawnMinGroup;
    public int spawnMaxGroup;
    public boolean biomeVariants = true;
    public boolean doSpawning = true;
    public boolean useSpawnCosts;
    public double spawnCostPer;
    public double spawnMaxCost;
    public final float width;
    public final float height;
    public boolean despawn;
    public final EntitySpawnPlacementRegistry.PlacementType placementType;
    public final Heightmap.Type heightMapType;
    public final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate;
    protected boolean placementRegistered = false;

    public Set<Biome> spawnBiomes;

    protected EntityConfiguration config;

    protected final CustomConfigurationHolder customConfig;
    protected final CustomConfigurationHolder customClientConfig;

    protected Supplier<Set<Biome>> defaultBiomeSupplier;

    protected EntityVariantList variantList;
    protected int variantMax;
    protected DataParameter<String> variantDataKey;

    protected final String modid;
    protected final Supplier<AttributeModifierMap.MutableAttribute> attributeMap;

    protected NonNullLazy<MobSpawnInfo.Spawners> spawnEntry;

    protected EntityTypeContainer(IEntityTypeDefinition<T> def) {
        this.modid = def.getModId();
        this.entityClass = def.getEntityClass();
        this.factory = def.getEntityFactory();
        this.entityName = def.getEntityName();
        this.hasEgg = def.hasEgg();
        this.eggColorSolid = def.getEggSolidColor();
        this.eggColorSpot = def.getEggSpotColor();
        this.spawnWeight = def.getSpawnWeight();
        this.spawnMinGroup = def.getSpawnMinGroup();
        this.spawnMaxGroup = def.getSpawnMaxGroup();
        this.useSpawnCosts = def.useSpawnCosts();
        this.spawnCostPer = def.getSpawnCostPer();
        this.spawnMaxCost = def.getSpawnMaxCost();
        this.spawnType = def.getSpawnClassification();
        this.width = def.getWidth();
        this.height = def.getHeight();
        this.despawn = def.despawns();
        this.customConfig = def.getCustomConfig();
        this.customClientConfig = def.getCustomClientConfig();
        this.defaultBiomeSupplier = def.getSpawnBiomes();
        if(defaultBiomeSupplier != null) {
            spawnBiomes = defaultBiomeSupplier.get();
        } else {
            spawnBiomes = new HashSet<Biome>();
        }
        this.placementType = def.getPlacementType();
        this.heightMapType = def.getPlacementHeightMapType();
        this.placementPredicate = def.getPlacementPredicate();
        this.variantMax = def.getVariantAmount();
        if(this.hasVariants()) {
            variantList = new EntityVariantList(variantMax);
            variantList.add(def.getVariants());
        }
        this.attributeMap = def.getAttributeMap();
        this.spawnEntry = NonNullLazy.of(() -> new MobSpawnInfo.Spawners(this.entityType, this.spawnWeight, this.spawnMinGroup, this.spawnMaxGroup));
    }

    public void onCreateEntityType() {
        if(this.hasEgg) {
            this.egg = new ModSpawnEggItem(this);
        }
    }

    public static class Builder<T extends MobEntity> extends AbstractEntityBuilder<T, EntityTypeContainer<T>, Builder<T>> {

        private Builder(Class<T> EntityClass, Function<World, T> func, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            super(EntityClass, func, entityNameIn, attributeMap, modid);
        }

        public EntityTypeContainer<T> rawBuild() {
            return new EntityTypeContainer<T>(new EntityTypeDefinition<T>(this));
        }

        @Override
        public Builder<T> getImplementation() {
            return this;
        }

        public static <T extends MobEntity> Builder<T> create(Class<T> EntityClass, Function<World, T> func, String entityNameIn, Supplier<AttributeModifierMap.MutableAttribute> attributeMap, String modid) {
            return new Builder<T>(EntityClass, func, entityNameIn, attributeMap, modid);
        }

    }

    public void initConfiguration(ForgeConfigSpec.Builder builder) {
        this.config = new EntityConfiguration(builder);
    }

    public EntityConfiguration getConfiguration() {
        return this.config;
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
        public ForgeConfigSpec.BooleanValue doDespawn;

        protected EntityConfiguration(ForgeConfigSpec.Builder builder) {
            EntityTypeContainer<T> container = EntityTypeContainer.this;
            builder.push(container.entityName);
            this.loadSpawning(builder);
            this.loadSpawnValues(builder, container);
            doDespawn = builder.comment("True if this entity can despawn freely when no players are nearby.").worldRestart().define("doDespawn", container.despawn);
            if(ISelectiveVariantTypes.class.isAssignableFrom(EntityTypeContainer.this.entityClass)) {
                biomeVariants = builder.comment("Setting to true enables biome based variant spawning. This will make this entity choose variants based on the biome they spawn in. (No longer affects spawn eggs as it did in the past, only natural spawns)").worldRestart().define("biomeBasedVariants", container.biomeVariants);
            }
            EntityTypeContainer.this.customConfigurationInit(builder);
            builder.pop();
        }

        public void loadSpawning(ForgeConfigSpec.Builder builder) {
            doSpawning = builder.comment("Disables natural spawning").worldRestart().define("doSpawning", true);

        }

        public void loadSpawnValues(ForgeConfigSpec.Builder builder, EntityTypeContainer<T> container) {
            spawnWeight = builder.comment("The spawn chance compared to other entities (typically between 6-20)").worldRestart().defineInRange("weight", container.spawnWeight, 1, 9999);
            spawnMinGroup = builder.comment("Must be greater than 0").worldRestart().defineInRange("minGroup", container.spawnMinGroup, 1, 9999);
            spawnMaxGroup = builder.comment("Must be greater or equal to min value!").worldRestart().defineInRange("maxGroup", container.spawnMaxGroup, 1, 9999);
            useSpawnCosts = builder.comment("Whether to use spawn costs in spawning or not").worldRestart().define("useSpawnCosts", container.useSpawnCosts);
            spawnCostPer = builder.comment("Cost to spawning algorithm per entity spawned").worldRestart().defineInRange("costPer", container.spawnCostPer, 0.01, 9999);
            spawnMaxCost = builder.comment("Maxmimum cost the spawning algorithm can accrue for this entity").worldRestart().defineInRange("maxCost", container.spawnMaxCost, 0.01, 9999);
            biomesList = builder.comment("Enter biome Resource Locations. Supports modded biomes.").worldRestart().defineList("spawnBiomes", container.getBiomeIDs(), (Predicate<Object>) input -> input instanceof String);
        }
    }

    public List<String> getBiomeIDs() {
        return spawnBiomes.parallelStream().map(b -> b.getRegistryName().toString()).collect(Collectors.toList());
    }

    public void configurationLoad() {
        EntityTypeContainer<T>.EntityConfiguration section = this.getConfiguration();
        spawnMaxGroup = section.spawnMaxGroup.get();
        spawnMinGroup = section.spawnMinGroup.get();
        spawnWeight = section.spawnWeight.get();
        doSpawning = section.doSpawning.get();
        despawn = section.doDespawn.get();
        this.spawnBiomes = new HashSet<Biome>();
        for(String biomeName : section.biomesList.get()) {
            try {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
                if(biome == null) {
                    LogManager.getLogger().error("Invalid biome \"" + biomeName + "\" for entity " + this.entityName + ". No biome exists with that name. Skipping.");
                } else {
                    this.spawnBiomes.add(biome);
                }
            } catch(Exception e) {
                LogManager.getLogger().error("Invalid biome name: \"" + biomeName + "\" for entity " + this.entityName + ". Is it formatted correctly? Skipping.");
            }
        }
        if(section.useSpawnCosts.get()) {
            spawnCostPer = section.spawnCostPer.get();
            spawnMaxCost = section.spawnMaxCost.get();
        }
        if(section.biomeVariants != null) {
            this.biomeVariants = section.biomeVariants.get();
        }
        if(this.customConfig != null) {
            this.customConfig.customConfigurationLoad();
        }
    }

    public void clientConfigurationLoad() {
        if(this.customClientConfig != null) {
            this.customClientConfig.customConfigurationLoad();
        }
    }

    public void customConfigurationInit(ForgeConfigSpec.Builder builder) {
        if(this.customConfig != null) {
            this.customConfig.customConfigurationInit(builder);
        }
    }

    public void clientCustomConfigurationInit(ForgeConfigSpec.Builder builder) {
        if(this.customClientConfig != null) {
            builder.push(this.entityName);
            this.customClientConfig.customConfigurationInit(builder);
            builder.pop();
        }
    }

    public static interface CustomConfigurationHolder {
        void customConfigurationInit(ForgeConfigSpec.Builder builder);

        void customConfigurationLoad();
    }

    @SuppressWarnings("deprecation")
    public static <T extends MobEntity> boolean waterSpawn(EntityType<T> type, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
        return pos.getY() > 45 && pos.getY() < (world.getSeaLevel() - 1) && world.getBlockState(pos).getBlock() == Blocks.WATER;
    }

    public void registerPlacement() {
        if(this.placementType != null && this.placementPredicate != null && this.heightMapType != null) {
            if(!placementRegistered) {
                EntitySpawnPlacementRegistry.<T>register(this.entityType, placementType, heightMapType, placementPredicate);
                placementRegistered = true;
            }
        }
    }

    public String getModId() {
        return modid;
    }

    public Supplier<AttributeModifierMap.MutableAttribute> getAttributeBuilder() {
        return attributeMap;
    }

    public boolean registerAttributes() {
        if(attributeMap != null) {
            return GlobalEntityTypeAttributes.put(entityType, attributeMap.get().create()) != null;
        }
        return false;
    }

    public boolean hasVariants() {
        return this.variantMax > 0;
    }

    public int getVariantMax() {
        return this.variantMax;
    }

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
        if(this.variantDataKey == null) {
            this.variantDataKey = EntityDataManager.<String>createKey(this.entityClass, DataSerializers.STRING);
        }
        return this.variantDataKey;
    }

    public MobSpawnInfo.Spawners getSpawnEntry() {
        return spawnEntry.get();
    }

}
